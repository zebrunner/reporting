(function () {
    'use strict';

    angular
        .module('app.testruninfo')
        .controller('TestRunInfoController', ['$scope', '$rootScope', '$log', '$timeout', '$window', '$q', 'TestService', 'TestRunService', 'UtilService', 'ArtifactService', '$stateParams', TestRunInfoController])

    // **************************************************************************
    function TestRunInfoController($scope, $rootScope, $log, $timeout, $window, $q, TestService, TestRunService, UtilService, ArtifactService, $stateParams) {

        $scope.testRun = {};
        $scope.test = {};

        $scope.tabs = [
            { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."},
            { title: 'Screenshots', content: "You can swipe left and right on a mobile device to change tabs."},
            { title: 'Raw logs', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
        ];

        var MODES = {
            live: {
                name: 'live',
                element: '.video-wrapper'
            },
            record: {
                name: 'record',
                element: 'video'
            }
        };

        $scope.MODE = {};

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
        var logsStompName;
        var logsStomp;
        $scope.logs = [];

        function followUpOnLogs(log) {
            $scope.$apply(function () {
                $scope.logs.push(log);
            });
        };

        $scope.setMode = function (modeName) {
            $scope.MODE = MODES[modeName];
            if ($scope.MODE.name == 'live') {
                rfb = ArtifactService.connectVnc(angular.element($scope.MODE.element)[0], 'offsetHeight', 'offsetWidth',  $scope.wsURL);
                var logsContainer = angular.element('#logs')[0];
                ArtifactService.provideLogs($rootScope.rabbitmq, $scope.testRun, $scope.test, logsContainer, followUpOnLogs).then(function (data) {
                    logsStomp = data.stomp;
                    logsStompName = data.name;
                });
            }
        };

        $scope.$on('$destroy', function () {
            if(rfb && rfb._connected) {
                rfb.disconnect();
            }
            if(logsStomp && logsStomp.connected) {
                logsStomp.disconnect();
                $scope.logs = [];
                UtilService.websocketConnected(logsStompName);
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
                    $scope.testRun.tests = testsRs;
                });
            });
        })();
    }

})();
