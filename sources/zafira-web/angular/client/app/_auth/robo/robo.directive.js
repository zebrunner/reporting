(function () {
    'use strict';

    angular.module('app.robo').directive('robo', function () {
        return {
            template: require('./robo.html'),
            restrict: 'E',
            replace: true,
        };
    });
})();
