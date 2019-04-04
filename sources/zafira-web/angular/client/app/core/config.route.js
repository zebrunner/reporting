(function () {
    'use strict';

    angular.module('app')
        .config(function($stateProvider, $urlRouterProvider) {
            'ngInject';

            $stateProvider
                .state('home', {
                  redirectTo: (transisiton) => {
                    return transisiton.router.stateService.target('dashboard.list', {}, { location: 'replace' });
                  },
                })
                .state('dashboard', {
                    url: '/dashboards',
                    abstract: true,
                    template: '<ui-view />',
                    data: {
                        requireLogin: true
                    }
                })
                .state('dashboard.page', {
                    url: '/:dashboardId?userId&testCaseId',
                    component: 'dashboardComponent',
                    data: {
                        requireLogin: true
                    },
                    resolve: {
                        dashboard: ($transition$, $state, DashboardService, $q, $timeout) => {
                            'ngInject';

                            const { dashboardId } = $transition$.params();

                            if (dashboardId) {
                                return DashboardService.GetDashboardById(dashboardId).then(function (rs) {
                                    if (rs.success) {
                                        return rs.data;
                                    } else {
                                        //TODO: dashboards is a home page. If we redirect to dashboards we can get infinity loop. We need to add simple error page;
                                        const message = rs && rs.message || `Can\'t fetch dashboard with id: ${dashboardId}`;

                                        alertify.error(message);

                                        return $q.reject(message);
                                    }
                                });
                            } else {
                                // Timeout to avoid digest issues
                                $timeout(function () {
                                    $state.go('home');
                                });

                                return false;
                            }
                        },
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "dashboard" */ '../_dashboards/dashboard.module.js')
                            .then(mod => $ocLazyLoad.load(mod.dashboardModule))
                            .catch(err => {
                                throw new Error('Can\'t load dashboard module, ' + err);
                            });
                    }
                })
                .state('dashboard.list', {
                    url: '',
                    template: '',
                    data: {
                        requireLogin: true
                    },
                    resolve: {
                        dashboardId: (AuthService, DashboardService, UserService, $state, $q, $timeout) => {
                            'ngInject';

                            const currentUser = UserService.currentUser;
                            let { defaultDashboardId } = currentUser;

                            if (!currentUser || defaultDashboardId === undefined) {
                                //get first available dashboard
                                const hideDashboards = !AuthService.UserHasAnyPermission(['VIEW_HIDDEN_DASHBOARDS']);

                                return DashboardService.GetDashboards(hideDashboards).then(function (rs) {
                                    if (rs.success) {
                                        let defaultDashboard = rs.data.find(({ title }) => title.toLowerCase() === 'general') || rs.data[0];

                                        if (defaultDashboard) {
                                            defaultDashboardId = defaultDashboard.id;

                                            // Redirect to default dashboard
                                            // Timeout to avoid digest issues
                                            $timeout(function() {
                                                $state.go('dashboard.page', {dashboardId: defaultDashboardId}, {location: 'replace'});
                                            });

                                            return false;
                                        } else {
                                            //TODO: dashboards is a home page. If we redirect to dashboards we can get infinity loop. We need to add simple error page;
                                            const message = 'Can\'t fetch default dashboard';

                                            alertify.error(message);

                                            return $q.reject(message);
                                        }
                                    } else {
                                        //TODO: dashboards is a home page. If we redirect to dashboards we can get infinity loop. We need to add simple error page;
                                        const message = rs && rs.message || 'Can\'t fetch dashboards';

                                        alertify.error(message);

                                        return $q.reject(message);
                                    }
                                });
                            }

                            // Redirect to default dashboard
                            // Timeout to avoid digest issues
                            $timeout(function() {
                                $state.go('dashboard.page', {dashboardId: defaultDashboardId}, {location: 'replace'});
                            });

                            return false;
                        }
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "dashboard" */ '../_dashboards/dashboard.module.js')
                            .then(mod => $ocLazyLoad.load(mod.dashboardModule))
                            .catch(err => {
                                throw new Error('Can\'t load dashboard module, ' + err);
                            });
                    }
                })
                .state('views', {
                    url: '/views/:id',
                    template: require('../_views/list.html'),
                    data: {
                        requireLogin: true
                    }
                })
                .state('signin', {
                    url: '/signin',
                    component: 'signinComponent',
                    params: {
                        referrer: null,
                        referrerParams: null
                    },
                    data: {
                        onlyGuests: true,
                        classes: 'body-wide body-auth'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "auth" */ '../_auth/auth.module.js')
                            .then(mod => $ocLazyLoad.load(mod.authModule))
                            .catch(err => {
                                throw new Error('Can\'t load auth module, ' + err);
                            });
                    }
                })
                .state('signup', {
                    url: '/signup?token',
                    component: 'signupComponent',
                    data: {
                        onlyGuests: true,
                        classes: 'body-wide body-auth'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "auth" */ '../_auth/auth.module.js')
                            .then(mod => $ocLazyLoad.load(mod.authModule))
                            .catch(err => {
                                throw new Error('Can\'t load auth module, ' + err);
                            });
                    }
                })
                .state('logout', {
                    url: '/logout',
                    controller: function($state, AuthService, $timeout) {
                        'ngInject';

                        AuthService.ClearCredentials();
                        // Timeout to avoid digest issues
                        $timeout(function() {
                            $state.go('signin');
                        });
                    },
                    data: {
                        requireLogin: true
                    }
                })
                .state('forgotPassword', {
                    url: '/password/forgot',
                    component: 'forgotPasswordComponent',
                    data: {
                        onlyGuests: true,
                        classes: 'body-wide body-auth'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "auth" */ '../_auth/auth.module.js')
                            .then(mod => $ocLazyLoad.load(mod.authModule))
                            .catch(err => {
                                throw new Error('Can\'t load auth module, ' + err);
                            });
                    }
                })
                .state('resetPassword', {
                    url: '/password/reset?token',
                    component: 'resetPasswordComponent',
                    data: {
                        onlyGuests: true,
                        classes: 'body-wide body-auth'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "auth" */ '../_auth/auth.module.js')
                            .then(mod => $ocLazyLoad.load(mod.authModule))
                            .catch(err => {
                                throw new Error('Can\'t load auth module, ' + err);
                            });
                    }
                })
                .state('users', {
                    url: '/users',
                    abstract: true,
                    template: '<ui-view />',
                    data: {
                        requireLogin: true
                    }
                })
                .state('users.list', {
                    url: '',
                    component: 'usersComponent',
                    data: {
                        requireLogin: true,
                        classes: 'p-users'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "users" */ '../_users/users.module.js')
                            .then(mod => $ocLazyLoad.load(mod.usersModule))
                            .catch(err => {
                                throw new Error('Can\'t load usersModule module, ' + err);
                            });
                    }
                })
                .state('users.profile', {
                    url: '/profile',
                    component: 'userComponent',
                    data: {
                        requireLogin: true,
                        classes: 'p-user-profile'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "profile" */ '../_user/user.module.js')
                        .then(mod => $ocLazyLoad.load(mod.userModule))
                        .catch(err => {
                            throw new Error('Can\'t load userModule module, ' + err);
                        });
                    }
                })
                // For github redirection
                // TODO: Should be only for guests?
                .state('scm/callback', {
                    url: '/scm/callback?code',
                    component: 'scmComponent',
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "scm" */ '../_scm/scm.module.js')
                            .then(mod => $ocLazyLoad.load(mod.scmModule))
                            .catch(err => {
                                throw new Error('Can\'t load scm module, ' + err);
                            });
                    }
                })
                // TODO: link to this state is commented, so we can comment this state to reduce app build size
                // .state('tests/cases', {
                //     url: '/tests/cases',
                //     component: 'testcaseComponent',
                //     data: {
                //         requireLogin: true
                //     },
                //     lazyLoad: ($transition$) => {
                //         const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');
                //
                //         return import(/* webpackChunkName: "testcase" */ '../_testcases/testcase.module.js')
                //             .then(mod => $ocLazyLoad.load(mod.testcaseModule))
                //             .catch(err => {
                //                 throw new Error('Can\'t load testcase module, ' + err);
                //             });
                //     }
                // })
                // .state('tests/cases/metrics', {
                //     url: '/tests/cases/:id/metrics',
                //     component: 'testcaseMetricsComponent',
                //     data: {
                //         requireLogin: true
                //     },
                //     lazyLoad: ($transition$) => {
                //         const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');
                //
                //         return import(/* webpackChunkName: "testcase" */ '../_testcases/testcase.module.js')
                //             .then(mod => $ocLazyLoad.load(mod.testcaseModule))
                //             .catch(err => {
                //                 throw new Error('Can\'t load testcase module, ' + err);
                //             });
                //     }
                // })
                .state('tests', {
                    url: '/tests',
                    abstract: true,
                    template: '<ui-view />',
                    data: {
                        requireLogin: true
                    }
                })
                .state('tests.runs', {
                    url: '/runs',
                    component: 'testsRunsComponent',
                    params: {
                        activeTestRunId: null
                    },
                    data: {
                        requireLogin: true,
                        classes: 'p-tests-runs'
                    },
                    resolve: {
                        resolvedTestRuns: function($state, testsRunsService, $q, projectsService) {
                            'ngInject';

                            const prevState = $state.current.name;
                            const projects = projectsService.getSelectedProjects();

                            testsRunsService.resetFilteringState();
                            // read saved search/filtering data only if we reload current page or returning from internal page
                            if (!prevState || prevState === 'tests.runDetails' || prevState === 'tests.runs') {
                                testsRunsService.readStoredParams();
                            } else {
                                testsRunsService.deleteStoredParams();
                            }

                            testsRunsService.setSearchParam('projects', projects);

                            return testsRunsService.fetchTestRuns().catch(function(err) {
                                err && err.message && alertify.error(err.message);
                                //1st approach: if can't load with user/cached searchParams reset them and reload page
                                // if (!force) {
                                //     testsRunsService.deleteStoredParams();
                                // // Timeout to avoid digest issues
                                // $timeout(function() {
                                //     $state.go('tests.runs', null, { reload: true });
                                // });
                                // }
                                //2nd approach: if can't load with user/cached searchParams return empty data
                                return $q.resolve([]);
                            });
                        },
                        activeTestRunId: function($stateParams, $q) { //TODO: use to implement highlighting opened tesRun
                            'ngInject';

                            const id = $stateParams.activeTestRunId ? $stateParams.activeTestRunId : undefined;

                            return $q.resolve(id);
                        },
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "tests-runs" */ '../containers/tests-runs/tests-runs.module.js')
                        .then(mod => $ocLazyLoad.load(mod.testsRunsModule))
                        .catch(err => {
                            throw new Error('Can\'t load testsRuns module, ' + err);
                        });
                    }
                })
                .state('tests.runDetails', {
                    url: '/runs/:testRunId',
                    component: 'testDetailsComponent',
                    store: true,
                    params: {
                        testRun: null
                    },
                    data: {
                        requireLogin: true,
                        classes: 'p-tests-run-details'
                    },
                    resolve: {
                        testRun: function($stateParams, $q, $state, TestRunService, $timeout) {
                            'ngInject';

                            if ($stateParams.testRunId) {
                                const params = {
                                    id: $stateParams.testRunId
                                };

                                return TestRunService.searchTestRuns(params)
                                .then(function(response) {
                                    if (response.success && response.data.results && response.data.results[0]) {
                                        return response.data.results[0];
                                    } else { //TODO: show error message & redirect to testruns
                                        return $q.reject({message: 'Can\'t get test run with ID=' + $stateParams.testRunId});
                                    }
                                })
                                .catch(function(error) {
                                    console.log(error); //TODO: show toaster notification
                                    // Timeout to avoid digest issues
                                    $timeout(() => {
                                        $state.go('tests.runs');
                                    }, 0);
                                });
                            } else {
                                // Timeout to avoid digest issues
                                $timeout(() => {
                                    $state.go('tests.runs');
                                }, 0);
                            }
                        }
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "test-details" */ '../containers/test-details/test-details.module.js')
                            .then(mod => $ocLazyLoad.load(mod.testDetailsModule))
                            .catch(err => {
                                throw new Error('Can\'t load testDetails module, ' + err);
                            });
                    }
                })
                .state('tests.runInfo', {
                    url: '/runs/:testRunId/info/:testId',
                    component: 'testRunInfoComponent',
                    data: {
                        requireLogin: true
                    },
                    resolve: {
                        testRun: ($stateParams, $q, $state, TestRunService, $timeout) => {
                            'ngInject';

                            if ($stateParams.testRunId) {
                                const params = {
                                    id: $stateParams.testRunId
                                };

                                return TestRunService.searchTestRuns(params)
                                .then(function(response) {
                                    if (response.success && response.data.results && response.data.results[0]) {
                                        return response.data.results[0];
                                    } else {
                                        return $q.reject({message: 'Can\'t get test run with ID=' + $stateParams.testRunId});
                                    }
                                })
                                .catch(function(error) {
                                    console.log(error); //TODO: show toaster notification
                                    // Timeout to avoid digest issues
                                    $timeout(() => {
                                        $state.go('tests.runs');
                                    }, 0);
                                });
                            } else {
                                // Timeout to avoid digest issues
                                $timeout(() => {
                                    $state.go('tests.runs');
                                }, 0);
                            }
                        }
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "testRunInfo" */ '../containers/test-run-info/test-run-info.module.js')
                            .then(mod => $ocLazyLoad.load(mod.testRunInfoModule))
                            .catch(err => {
                                throw new Error('Can\'t load testRunInfo module, ' + err);
                            });
                    }
                })
                .state('monitors', {
                    url: '/monitors',
                    component: 'monitorsComponent',
                    data: {
                        requireLogin: true
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "monitors" */ '../_monitors/monitors.module.js')
                            .then(mod => $ocLazyLoad.load(mod.monitorsModule))
                            .catch(err => {
                                throw new Error('Can\'t load monitorsModule module, ' + err);
                            });
                    }
                })
                .state('integrations', {
                    url: '/integrations',
                    component: 'integrationsComponent',
                    data: {
                        requireLogin: true,
                        classes: 'p-integrations'
                    },
                    resolve: {
                        toolsServicePrepare: (toolsService, $timeout, $state) => {
                            'ngInject';

                            return toolsService.getTools()
                                .catch((err) => {
                                    err && err.message && alertify.error(err.message);
                                    // Timeout to avoid digest issues
                                    $timeout(() => {
                                        $state.go('home');
                                    }, 0);

                                    return false;
                                });
                        }
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "integrations" */ '../_integrations/integrations.module.js')
                            .then(mod => $ocLazyLoad.load(mod.integrationsModule))
                            .catch(err => {
                                throw new Error('Can\'t load integrationsModule module, ' + err);
                            });
                    }
                })
                // TODO: looks like old one, check if we can remove state and related code
                // .state('certifications', {
                //     url: '/certification',
                //     component: 'certificationComponent',
                //     data: {
                //         requireLogin: true
                //     },
                //     lazyLoad: ($transition$) => {
                //         const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');
                //
                //         return import(/* webpackChunkName: "certification" */ '../_certifications/certification.module.js')
                //             .then(mod => $ocLazyLoad.load(mod.certificationModule))
                //             .catch(err => {
                //                 throw new Error('Can\'t load certificationModule module, ' + err);
                //             });
                //     }
                // })
                .state('404', {
                    url: '/404',
                    component: 'notFoundComponent',
                    data: {
                        classes: 'body-wide body-err p-not-found'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "not-found" */ '../modules/not-found/not-found.module.js')
                            .then(mod => $ocLazyLoad.load(mod.notFoundModule))
                            .catch(err => {
                                throw new Error('Can\'t load notFoundModule module, ' + err);
                            });
                    }
                })
                .state('500', {
                    url: '/500',
                    component: 'serverErrorComponent',
                    data: {
                        classes: 'body-wide body-err p-server-error'
                    },
                    lazyLoad: ($transition$) => {
                        const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                        return import(/* webpackChunkName: "not-found" */ '../modules/server-error/server-error.module.js')
                            .then(mod => $ocLazyLoad.load(mod.serverErrorModule))
                            .catch(err => {
                                throw new Error('Can\'t load serverErrorModule module, ' + err);
                            });
                    }
                });

            $urlRouterProvider
                .when('/', '/dashboards')
                .when('', '/dashboards')
                .otherwise('/404');

        });
})();
