(function () {
    'use strict';

    angular.module('app.appUsers').directive('appUsersFabs', () => {
        return {
            template: require('./app-users-fabs.html'),
            scope: {
                createUser: '&'
            },
            restrict: 'E',
            replace: true,
            link: () => {}
        };
    });
})();
