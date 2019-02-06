(function() {
    'use strict';

    require('./app-sidebar.scss');
    require('./app-sidebar.controller');

    angular.module('app.appSidebar').directive('appSidebar', function() {
        return {
            template: require('./app-sidebar.html'),
            controller: 'AppSidebarController',
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true,
        };
    });
})();
