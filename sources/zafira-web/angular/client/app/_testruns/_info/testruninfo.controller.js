(function () {
    'use strict';

    angular
        .module('app.testruninfo')
        .controller('TestRunInfoController', ['$scope', '$rootScope', '$log', '$timeout', '$window', '$q', 'TestService', 'TestRunService', 'UtilService', 'ArtifactService', '$stateParams', 'OFFSET', 'API_URL', TestRunInfoController])

    // **************************************************************************
    function TestRunInfoController($scope, $rootScope, $log, $timeout, $window, $q, TestService, TestRunService, UtilService, ArtifactService, $stateParams, OFFSET, API_URL) {

        $scope.testRun = {};
        $scope.test = {};
        $scope.drivers = [];

        $scope.OFFSET = OFFSET;

        $scope.tabs = [
            { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."},
            { title: 'Screenshots', content: "You can swipe left and right on a mobile device to change tabs."},
            { title: 'Raw logs', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
            { title: 'Test Info', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
        ];

        $scope.tab = $scope.tabs[0];

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

        var LIVE_DEMO_ARTIFACT_NAME = 'live demo';

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

        var testsWebsocketName = 'tests';

        function initTestsWebSocket() {
            $scope.testsWebsocket = Stomp.over(new SockJS(API_URL + "/websockets"));
            $scope.testsWebsocket.debug = null;
            $scope.testsWebsocket.connect({withCredentials: false}, function () {
                if($scope.testsWebsocket.connected) {
                    $scope.testsWebsocket.subscribe("/topic/testRuns/" + $scope.testRun.id + "/tests", function (data) {
                        var test = $scope.getEventFromMessage(data.body).test;
                        if(test.id == $scope.test.id) {
                            $scope.test = angular.copy(test);
                            var liveDemoArtifact = getArrayValue(getLiveDemoArtifacts(test), 0);
                            addDriver(liveDemoArtifact);
                            $scope.$apply();
                        }
                    });
                }
            }, function () {
                UtilService.reconnectWebsocket(testsWebsocketName, initTestsWebSocket());
            });
            UtilService.websocketConnected(testsWebsocketName);
        };

        $scope.getEventFromMessage = function (message) {
            return JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
        };

        $scope.onResize = function() {
            ArtifactService.resize(angular.element($scope.MODE.element)[0], rfb);
        };

        $scope.$on('$destroy', function () {
            if(rfb) {
                rfb.disconnect();
            }
            if(logsStomp && logsStomp.connected) {
                logsStomp.disconnect();
                $scope.logs = [];
                UtilService.websocketConnected(logsStompName);
            }
            if($scope.testsWebsocket && $scope.testsWebsocket.connected) {
                $scope.testsWebsocket.disconnect();
                UtilService.websocketConnected(testsWebsocketName);
            }
        });

        function getLiveDemoArtifacts(test) {
            return test.artifacts ? test.artifacts.filter(function (artifact) {
                return artifact.name.toLowerCase().includes(LIVE_DEMO_ARTIFACT_NAME)
            }) : [];
        };

        function getArrayValue(array, index) {
            return array && array.length ? array[index] : undefined;
        };

        function addDriver(liveDemoArtifact) {
            if(liveDemoArtifact && $scope.drivers.indexOf(liveDemoArtifact.createdAt) == -1) {
                $scope.drivers.push(liveDemoArtifact.createdAt);
            }
        };

        (function init() {
            getTestRun($stateParams.id).then(function (rs) {
                $scope.testRun = rs;
                initTestsWebSocket();
                getTest(rs.id).then(function (testsRs) {
                    $scope.test = testsRs.filter(function (t) {
                        return t.id === parseInt($stateParams.testId);
                    })[0];
                    if($scope.test.status == 'IN_PROGRESS') {
                        var videoArtifacts = getLiveDemoArtifacts($scope.test);
                        var videoArtifact = getArrayValue(videoArtifacts, 0);
                        addDriver(videoArtifact);
                        $scope.wsURL = videoArtifact ? videoArtifact.link : undefined;
                        $scope.testRun.tests = testsRs;
                    }
                });
            });
        })();
    }

})();
