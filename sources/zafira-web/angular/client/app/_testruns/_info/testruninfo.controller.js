(function () {
    'use strict';

    angular
        .module('app.testruninfo')
        .controller('TestRunInfoController', ['$scope', '$log', '$timeout', '$window', '$q', 'TestService', 'TestRunService', '$stateParams', TestRunInfoController])

    // **************************************************************************
    function TestRunInfoController($scope, $log, $timeout, $window, $q, TestService, TestRunService, $stateParams) {

        $scope.testRun = {};
        $scope.test = {};

        $scope.tabs = [
            { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."},
            { title: 'Screenshots', content: "You can swipe left and right on a mobile device to change tabs."},
            { title: 'Raw logs', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
        ];

        function getTestRun(id) {
            return $q(function (resolve, reject) {
                TestRunService.searchTestRuns({id: id}).then(function (rs) {
                    if(rs.success && rs.data.results.length) {
                        resolve(rs.data.results[0]);
                    } else {
                        reject(rs.message);
                    }
                });
            });
        };

        function getTest(testRunId) {
            return $q(function (resolve, reject) {
                TestService.searchTests({testRunId: testRunId}).then(function (rs) {
                    if(rs.success && rs.data.results) {
                        resolve(rs.data.results);
                    } else {
                        reject(rs.message);
                    }
                });
            });
        };

        var rfb;
        var display;
        var ratio;

        $scope.loading = true;

        var container  = angular.element('video')[0];
        var containerHeightProperty = 'offsetHeight';
        var containerWidthProperty = 'offsetWidth';

        $scope.initVNCWebsocket = function(wsURL) {
            rfb = new RFB(angular.element('#vnc')[0], wsURL, { shared: true, credentials: { password: 'selenoid' } });
            //rfb._viewOnly = true;
            rfb.addEventListener("connect",  connected);
            rfb.addEventListener("disconnect",  disconnected);
            rfb.scaleViewport = true;
            rfb.resizeSession = true;
            display = rfb._display;
            display._scale = 1;
            angular.element($window).bind('resize', function(){
                autoscale(display, ratio, container);
            });
        };

        function connected(e) {
            $scope.loading = false;
            var canvas = document.getElementsByTagName("canvas")[0];
            ratio = canvas.width / canvas.height;
            autoscale(display, ratio, container);

        };

        function disconnected(e) {
            $scope.hide();
        };

        function autoscale(display, ratio, window) {
            var size = calculateSize(window, ratio);
            display.autoscale(size.width, size.height, false);
        };

        function calculateSize(window, ratio) {
            var width = window[containerWidthProperty];
            var height = ratio > 1 ?  width / ratio : width * ratio;
            if(height > window[containerHeightProperty])
            {
                height = window[containerHeightProperty] - 100;
                width = ratio < 1 ? height / ratio : height * ratio;
            }
            return {height: height, width: width};
        };

        $scope.$on('$destroy', function () {
            if(rfb && rfb._connected) {
                rfb.disconnect();
            }
        });

        (function init() {
            getTestRun($stateParams.id).then(function (rs) {
                getTest(rs.id).then(function (testsRs) {
                    $scope.testRun = rs;
                    $scope.test = testsRs.filter(function (t) {
                        return t.id === parseInt($stateParams.testId);
                    })[0];
                    var videoArtifacts = $scope.test.artifacts ? $scope.test.artifacts.filter(function (artifact) {
                        return artifact.name.toLowerCase().includes('live demo')
                    }) : [];
                    $scope.wsURL = videoArtifacts && videoArtifacts.length ? videoArtifacts[0].link : undefined;
                    $scope.initVNCWebsocket($scope.wsURL);
                    $scope.testRun.tests = testsRs;
                });
            });
        })();
    }

})();
