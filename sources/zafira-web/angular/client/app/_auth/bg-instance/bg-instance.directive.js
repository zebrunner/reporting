(function () {
    'use strict';

    angular.module('app.bgInstance').directive('bgInstance', function () {
        return {
            template: require('./bg-instance.html'),
            restrict: 'E',
            replace: true,
        };
    });
})();
