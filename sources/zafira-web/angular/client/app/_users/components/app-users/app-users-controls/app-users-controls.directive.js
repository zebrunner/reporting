(function () {
    'use strict';

    angular.module('app.appUsers').directive('appUsersControls', () => {
        return {
            template: require('./app-users-controls.html'),
            scope: {
                onSearch: '&',
                onReset: '&'
            },
            restrict: 'E',
            replace: true,
            link: () => {}
        };
    });

})();
