import template from './app-users.html';

(function () {
    'use strict';

    require('./app-users.controller');

    angular.module('app.appUsers').directive('appUsers', () => {
        return {
            template,
            controller: 'AppUsersController',
            scope: {
                onSearch: '&',
                source: '=',
                searchValue: '=',
                tabs: '='
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true
        };
    });

})();
