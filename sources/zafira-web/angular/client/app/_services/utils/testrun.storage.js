(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunsStorage', ['$timeout', '$window', '$httpMock', TestRunsStorage])

    function TestRunsStorage($timeout, $window, $httpMock) {

        var storage = {};
        var windowOffset = 0;

        return {
            takeSnapshot: takeSnapshot,
            applySnapshot: applySnapshot
        };

        function applySnapshot(controller, scope) {
            controller.$onInit = function () {
                $timeout(function() {
                    applyAfterControllerInit(scope);
                }, 0, false);
            };
        };

        function takeSnapshot(scope, values, window) {
            values.forEach(function (value) {
                storage[value] = scope[value];
            });
            windowOffset = window.scrollY;
        };

        function applyAfterControllerInit(scope) {
            if($httpMock.isBackClicked()) {
                angular.forEach(storage, function (value, key, object) {
                    scope[key] = value;
                });
                $timeout(function () {
                    $window.scrollTo(0, windowOffset);
                    $httpMock.clearBackClicking();
                    clear();
                }, 0, false);
            }
        };

        function clear() {
            storage = {};
            windowOffset = 0;
        };
    }
})();
