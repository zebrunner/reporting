import template from './app-group.html';

(function () {
    'use strict';

    require('./app-group.controller');

    angular.module('app.appGroup').directive('appGroup', function () {
        return {
            template,
            controller: 'AppGroupController',
            scope: {
                tabs: '=',
                groups: '='
            },
            restrict: 'E',
            replace: true
        };
    });

})();
