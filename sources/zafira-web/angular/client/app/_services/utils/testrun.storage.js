(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunsStorage', ['$timeout', '$window', '$httpMock', '$q', TestRunsStorage])

    function TestRunsStorage($timeout, $window, $httpMock, $q) {

        var storage = {};
        var additional;
        var windowOffset = 0;
        var testRun = {};
        var testRunId;
        var isDataPrepared = false;

        return {
            takeSnapshot: takeSnapshot,
            applySnapshot: applySnapshot,
            clear: clear,
            isPrepared: isPrepared,
            getTestRunId: function () {
                return testRunId;
            }
        };

        function applySnapshot(scope) {
            return $q(function (resolve, reject) {
                if($httpMock.isBackClicked()) {
                    triggerLoad();
                    scope.$on('controller-inited', function (event, name) {
                        if(name == 'TestRunListController') {
                            waitUntilQueueIsEmpty(function () {
                                applyAfterControllerInit(scope);
                                var resolveData = angular.copy(additional);
                                resolve(resolveData);
                            });
                        }
                    });
                };
            });
        };

        function takeSnapshot(scope, values, window, additionalValue, tr, trId) {
            values.forEach(function (value) {
                storage[value] = scope[value];
            });
            windowOffset = window.scrollY;
            additional = additionalValue;
            tr.expand = false;
            testRun = tr;
            isDataPrepared = true;
            testRunId = trId;
        };

        function applyAfterControllerInit(scope) {
            if($httpMock.isBackClicked()) {
                storage['testRun'] = testRun;
                angular.forEach(storage, function (value, key, object) {
                    scope[key] = value;
                });
                waitUntilQueueIsEmpty(function () {
                    scope.switchTestRunExpand(storage['testRun'], true);
                    waitUntilQueueIsEmpty(function () {
                        $window.scrollTo(0, windowOffset);
                        $httpMock.clearBackClicking();
                        clear();
                        triggerLoadFinish();
                    });
                });
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

        function waitUntilQueueIsEmpty(func) {
            $timeout(function () {
                func.call();
            }, 0, false);
        };

        function isPrepared() {
            return isDataPrepared;
        };

        function clear() {
            storage = {};
            windowOffset = 0;
            testRun = {};
            additional = null;
            isDataPrepared = false;
        };
    }
})();
