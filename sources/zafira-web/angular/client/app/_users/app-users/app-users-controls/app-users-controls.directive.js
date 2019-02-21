import template from './app-users-controls.html';

(function () {
    'use strict';

    angular.module('app.appUsers').directive('appUsersControls', () => {
        return {
            template,
            scope: {
                onSearch: '&',
                onReset: '&'
            },
            restrict: 'E',
            replace: true
        };
    });

})();
