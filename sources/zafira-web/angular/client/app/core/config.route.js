(function () {
    'use strict';

    angular.module('app')
        .config(function($stateProvider, $urlRouterProvider) {
            'ngInject';

            $stateProvider
            .state('dashboard', {
                url: '/dashboards/:id',
                component: 'dashboardComponent',
                data: {
                    requireLogin: true
                },
                params: {
                    currentUser: null
                },
                resolve: {
                    currentUser: ($stateParams, $q, UserService, $state) => {
                        'ngInject';

                        const currentUser = UserService.getCurrentUser();

                        if (!currentUser) {
                            return UserService.initCurrentUser()
                                .then(function(user) {
                                    if (!$stateParams.id) {
                                        $state.go('dashboard', {id: user.defaultDashboardId}, {notify: false});
                                    }

                                    return $q.resolve(user);
                                });
                        } else {
                            if (!$stateParams.id) {
                                $state.go('dashboard', {id: currentUser.defaultDashboardId}, {notify: false});
                            }

                            return $q.resolve(currentUser);
                        }
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
            .state('dashboards', {
                url: '/dashboards',
                component: 'dashboardComponent',
                data: {
                    requireLogin: true
                },
                params: {
                    currentUser: null
                },
                resolve: {
                    currentUser: ($stateParams, $q, UserService, $state) => {
                        'ngInject';

                        const currentUser = UserService.getCurrentUser();

                        if (!currentUser) {
                            return UserService.initCurrentUser()
                                .then(function(user) {
                                    $state.go('dashboard', {id: user.defaultDashboardId});
                                });
                        } else {
                            $state.go('dashboard', {id: currentUser.defaultDashboardId});
                        }
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
                url: '/signup',
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
                controller: function($state, AuthService) {
                    'ngInject';

                    AuthService.ClearCredentials();
                    $state.go('signin');
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
                url: '/password/reset',
                template: require('../_auth/reset-password.html'),
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
            .state('users/profile', {
                url: '/users/profile',
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
            .state('users', {
                url: '/users',
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
            // For github redirection
            // TODO: Should be only for guests?
            .state('scm/callback', {
                url: '/scm/callback',
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
            .state('tests/run', {
                url: '/tests/runs/:testRunId',
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
                    testRun: function($stateParams, $q, $state, TestRunService) {
                        'ngInject';

                        if ($stateParams.testRun) {
                            return $q.resolve($stateParams.testRun);
                        } else if ($stateParams.testRunId) {
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
                                $state.go('tests/runs');
                            });
                        } else {
                            $state.go('tests/runs');
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
            .state('tests/runs', {
                url: '/tests/runs',
                component: 'testsRunsComponent',
                params: {
                    activeTestRunId: null
                },
                data: {
                    requireLogin: true,
                    classes: 'p-tests-runs'
                },
                resolve: {
                    resolvedTestRuns: function($state, testsRunsService, $q) {
                        'ngInject';

                        const prevState = $state.current.name;
                        let force = false;

                        testsRunsService.resetFilteringState();
                        // read saved search/filtering data only if we reload current page or returning from internal page
                        if (!prevState || prevState === 'tests/run' || prevState === 'tests/runs') {
                            testsRunsService.readStoredParams();
                        } else {
                            testsRunsService.deleteStoredParams();
                            force = true;
                        }

                        return testsRunsService.fetchTestRuns(force).catch(function(err) {
                            err && err.message && alertify.error(err.message);
                            //1st approach: if can't load with user/cached searchParams reset them and reload page
                            // if (!force) {
                            //     testsRunsService.deleteStoredParams();
                            //     $state.go('tests/runs', null, { reload: true });
                            // }
                            //2nd approach: if can't load with user/cached searchParams return empty data
                            return $q.resolve([]);
                        });
                    },
                    activeTestRunId: function($stateParams, $q) { //TODO: use to implement highlighting opened tesRun
                        'ngInject';

                        const id = $stateParams.activeTestRunId ? $stateParams.activeTestRunId : undefined;

                        return $q.resolve(id);
                    }
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
            .state('tests/runs/info', {
                url: '/tests/runs/:testRunId/info/:testId',
                component: 'testRunInfoComponent',
                data: {
                    requireLogin: true
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
            .state('settings', {
                url: '/settings',
                component: 'settingsComponent',
                data: {
                    requireLogin: true
                },
                lazyLoad: ($transition$) => {
                    const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');

                    return import(/* webpackChunkName: "settings" */ '../_settings/settings.module.js')
                        .then(mod => $ocLazyLoad.load(mod.settingsModule))
                        .catch(err => {
                            throw new Error('Can\'t load settings module, ' + err);
                        });
                }
            })
            // TODO: link to this state is commented, so we can comment this state to reduce app build size
            // .state('monitors', {
            //     url: '/monitors',
            //     component: 'monitorsComponent',
            //     data: {
            //         requireLogin: true
            //     },
            //     lazyLoad: ($transition$) => {
            //         const $ocLazyLoad = $transition$.injector().get('$ocLazyLoad');
            //
            //         return import(/* webpackChunkName: "monitors" */ '../_monitors/monitors.module.js')
            //             .then(mod => $ocLazyLoad.load(mod.monitorsModule))
            //             .catch(err => {
            //                 throw new Error('Can\'t load monitorsModule module, ' + err);
            //             });
            //     }
            // })
            .state('integrations', {
                url: '/integrations',
                component: 'integrationsComponent',
                data: {
                    requireLogin: true,
                    classes: 'p-integrations'
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
                template: require('../page/404.html'),
                data: {
                    classes: 'body-wide body-err'
                }
            })
            .state('500', {
                url: '/500',
                template: require('../page/500.html'),
                data: {
                    classes: 'body-wide body-err'
                }
            });

            $urlRouterProvider
                .when('/', '/dashboards')
                .when('', '/dashboards')
                .otherwise('/404');

        });
})();
