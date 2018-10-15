(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunsStorage', ['$timeout', '$window', '$httpMock', '$q', TestRunsStorage])

    function TestRunsStorage($timeout, $window, $httpMock, $q) {

        var storage = {};
        var additional;
        var windowOffset = 0;

        return {
            takeSnapshot: takeSnapshot,
            applySnapshot: applySnapshot
        };

        function applySnapshot(controller, scope) {
            return $q(function (resolve, reject) {
                if($httpMock.isBackClicked()) {
                    triggerLoad();
                }
                controller.$onInit = function () {
                    $timeout(function() {
                        applyAfterControllerInit(scope);
                        resolve(additional);
                    }, 0, false);
                };
            });
        };

        function takeSnapshot(scope, values, window, additionalValue) {
            values.forEach(function (value) {
                storage[value] = scope[value];
            });
            windowOffset = window.scrollY;
            additional = additionalValue;
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
                    triggerLoadFinish();
                }, 0, false);
            }
        };

        function triggerLoad() {
            var evt = document.createEvent('Event');
            evt.initEvent('load-force', false, false);
            window.dispatchEvent(evt);
        };

        function triggerLoadFinish() {
            var evt = document.createEvent('Event');
            evt.initEvent('load-force-finish', false, false);
            window.dispatchEvent(evt);
        };

        function clear() {
            storage = {};
            windowOffset = 0;
        };
    }
})();
