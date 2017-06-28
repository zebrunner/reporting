(function () {
    'use strict';

    angular.module('app')
        .config(['$ocLazyLoadProvider', function($ocLazyLoadProvider) {
            $ocLazyLoadProvider.config({
                debug: false,
                events: false,
                modules: [
                    {
                        name: 'fontawesome',
                        files: [
                            'bower_components/font-awesome/css/font-awesome.css'
                        ]
                    },
                    {
                        name: 'weather-icons',
                        files: [
                            'bower_components/weather-icons/css/weather-icons.min.css'
                        ]
                    },
                    {
                        name: 'angular-material-data-table',
                        files: [
                            'bower_components/angular-material-data-table/dist/md-data-table.min.js'
                        ]
                    },
                    {
                        name: 'angular-wizard',
                        files: [
                            'bower_components/angular-wizard/dist/angular-wizard.min.js'
                        ]
                    },
                    {
                        name: 'email',
                        files: [
                            'app/email/email.module.js',
                            'app/email/email.controller.js',
                            'app/email/email.service.js',
                        ],
                        serie: true // load in the exact order. Otherwise, you get errors
                    },
                    {
                        name: 'googlemap',
                        files: [
                            'js!http://maps.google.com/maps/api/js', // prefix with `js!`, see https://github.com/ocombe/ocLazyLoad/issues/170
                            'bower_components/ngmap/build/scripts/ng-map.min.js'
                        ]
                    },
                    {
                        name: 'fullcalendar',
                        files: [
                            'bower_components/moment/min/moment.min.js',
                            'bower_components/fullcalendar/dist/fullcalendar.min.js',
                            'bower_components/angular-ui-calendar/src/calendar.js'
                        ],
                        serie: true // load in the exact order. Otherwise, fullcalendar may complain about lack of moment
                    },
                    {
                        // textAngular also requires fontawesome, Bootstrap 3
                        // Note: textAngular-sanitize.js & angular-sanitize.js are the SAME, include one or the other but not both
                        // How to load 1.5.x: https://github.com/ocombe/ocLazyLoad/issues/286
                        // Note: textangular.js not textangular.min.js, see https://github.com/fraywing/textAngular/pull/866
                        name: 'textAngular', 
                        files: [
                            'bower_components/textAngular/dist/textAngular-sanitize.min.js',
                            'bower_components/rangy/rangy-core.min.js',
                            'bower_components/rangy/rangy-selectionsaverestore.min.js',
                            'bower_components/textAngular/dist/textAngular.js', 
                            'bower_components/textAngular/dist/textAngularSetup.js'
                        ],
                        serie: true // load in the exact order. Otherwise, you get errors
                    }
                ]

            })

        }]
    );

})(); 
