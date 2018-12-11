(function () {
    'use strict';

    angular.module('app')
        .config(['$stateProvider', '$urlRouterProvider', '$httpProvider', '$ocLazyLoadProvider',
                function($stateProvider, $urlRouterProvider, $httpProvider, $ocLazyLoadProvider) {

                $stateProvider
	                .state('dashboard', {
	                    url: '/dashboards/:id',
	                    templateUrl: 'app/_dashboards/list.html',
                        data: {
                            requireLogin: true
                        }
	                })
	                .state('dashboards', {
	                    url: '/dashboards',
	                    templateUrl: 'app/_dashboards/list.html',
                        data: {
                            requireLogin: true
                        }
	                })
                    .state('views', {
                        url: '/views/:id',
                        templateUrl: 'app/_views/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('signin', {
                        url: '/signin',
                        templateUrl: 'app/_auth/signin.html',
                        params: {
                            referrer: null
                        }
                    })
                    .state('signup', {
                        url: '/signup',
                        templateUrl: 'app/_auth/signup.html'
                    })
                    .state('forgotPassword', {
                        url: '/password/forgot',
                        templateUrl: 'app/_auth/forgot-password.html'
                    })
                    .state('resetPassword', {
                        url: '/password/reset',
                        templateUrl: 'app/_auth/reset-password.html'
                    })
                    .state('users/profile', {
                        url: '/users/profile',
                        templateUrl: 'app/_users/profile.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('users', {
                        url: '/users',
                        templateUrl: 'app/_users/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('tests/cases', {
                        url: '/tests/cases',
                        templateUrl: 'app/_testcases/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('tests/cases/metrics', {
                        url: '/tests/cases/:id/metrics',
                        templateUrl: 'app/_testcases/metrics/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('tests/run', {
	                    url: '/tests/runs/:id',
	                    templateUrl: 'app/_testruns/list.html',
                        store: true,
                        data: {
                            requireLogin: true
                        }
	                })
                    .state('tests/runs', {
                        url: '/tests/runs',
                        templateUrl: 'app/_testruns/list.html',
                        store: true,
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('tests/runs/info', {
                        url: '/tests/runs/:id/info/:testId',
                        templateUrl: 'app/_testruns/_info/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('settings', {
                        url: '/settings',
                        templateUrl: 'app/_settings/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('monitors', {
                        url: '/monitors',
                        templateUrl: 'app/_monitors/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('integrations', {
                        url: '/integrations',
                        templateUrl: 'app/_integrations/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('certifications', {
                        url: '/certification',
                        templateUrl: 'app/_certifications/list.html',
                        data: {
                            requireLogin: true
                        }
                    })
                    .state('404', {
                        url: '/404',
                        templateUrl: 'app/page/404.html'
                    })
                    .state('500', {
                        url: '/500',
                        templateUrl: 'app/page/500.html'
                    });

                $urlRouterProvider
                    .when('/', '/dashboards')
                    .when('', '/dashboards')
                    .otherwise('/404');

            }
        ]);
})();
