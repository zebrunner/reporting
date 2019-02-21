import template from './app-users-fabs.html';

(function () {
    'use strict';

    angular.module('app.appUsers').directive('appUsersFabs', () => {
        return {
            template,
            scope: {
                createUser: '&'
            },
            restrict: 'E',
            replace: true
        };
    });
})();
