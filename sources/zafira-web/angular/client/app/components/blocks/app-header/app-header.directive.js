(function () {
    'use strict';

    require('./app-header.controller');

    angular.module('app.appHeader').directive('appHeader', function () {
        return {
            template: require('./app-header.html'),
            controller: 'AppHeaderController',
            scope: {
                mainData: '=',
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true,
            bindToController: true
        };
    });

})();
