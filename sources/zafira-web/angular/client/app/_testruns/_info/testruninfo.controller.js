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
        var driversQueue = [];
        $scope.selectedDriver = 0;
        $scope.OFFSET = OFFSET;
        $scope.MODE = {};
        $scope.tab =  { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."};

        var LIVE_DEMO_ARTIFACT_NAME = 'live video';
        var DEMO_ARTIFACT_NAME = 'video';

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

        function setMode (modeName, test) {
            if($scope.MODE.name != modeName) {
                $scope.MODE = MODES[modeName];
                if ($scope.MODE.name == 'live') {
                    provideVideo();
                    provideLogs();
                } else if ($scope.MODE.name == 'record') {
                    $scope.drivers = [];
                    closeAll();
                }
            }
            if($scope.MODE.name == 'record') {
                var videoArtifacts = getArtifactsByPartName(test, 'video', 'live');
                if (videoArtifacts && videoArtifacts.length) {
                    console.log('switched' + videoArtifacts);
                    $scope.drivers = $scope.drivers.concat(videoArtifacts.filter(function (value) {
                        return $scope.drivers.indexOfField('name', value.name) == -1;
                    }));
                    watchUntilPainted('#videoRecord:has(source[src])', reloadVideo);
                }
            }
        };

        var isLoaded = false;
        function reloadVideo() {
            var videoElements = angular.element('#videoRecord');
            console.log('reloaded' + videoElements);
            if(videoElements && videoElements.length) {
                videoElements[0].addEventListener('loadeddata', function() {
                    isLoaded = true;
                }, false);
                if(! isLoaded) {
                    videoElements[0].load();
                }
            }
        };

        function getArtifactsByPartName(test, partName, exclusion) {
            return test.artifacts ? test.artifacts.filter(function (artifact) {
                return artifact.name.toLowerCase().includes(partName) && ! artifact.name.toLowerCase().includes(exclusion);
            }) : [];
        };

        function addDrivers(artifacts) {
            artifacts.sort(compareByCreatedAt);
            artifacts.forEach(function (artifact) {
                addDriver(artifact);
            });
        };

        function addDriver(liveDemoArtifact) {
            if(liveDemoArtifact && $scope.drivers.indexOfField('name', liveDemoArtifact.name) == -1) {
                $scope.drivers.push(liveDemoArtifact);
                liveDemoArtifact.index = $scope.drivers.length - 1;
                driversQueue.push(liveDemoArtifact);
            }
        };

        $scope.fullScreen = function() {
            var fullScreenClass = 'full-screen';
            var vncContainer = angular.element('.video-wrapper')[0];
            var tableBlock = angular.element('.table-history')[0];
            if(vncContainer.classList.contains(fullScreenClass)) {
                vncContainer.classList.remove(fullScreenClass);
                tableBlock.style.display = 'block';
            } else {
                vncContainer.classList.add(fullScreenClass);
                tableBlock.style.display = 'none';
            }
            $scope.onResize();
        };

        $scope.switchDriver = function (index) {
            if($scope.selectedDriver != index) {
                $scope.selectedDriver = index;
                if ($scope.MODE.name == 'live') {
                    closeRfbConnection();
                    provideVideo();
                } else if ($scope.MODE.name == 'record') {
                    isLoaded = false;
                    reloadVideo();
                }
            }
        };

        var painterWatcher;
        function watchUntilPainted(elementLocator, func) {
            painterWatcher = $scope.$watch(function() { return angular.element(elementLocator).is(':visible') }, function(newVal) {
                if(newVal) {
                    func.call();
                    painterWatcher();
                }
            });
        };

        function compareByCreatedAt(a,b) {
            if (a.createdAt < b.createdAt)
                return -1;
            if (a.createdAt > b.createdAt)
                return 1;
            return 0;
        }

        /**************** Websockets **************/
        var testsWebsocketName = 'tests';

        function initTestsWebSocket(testRun) {
            $scope.testsWebsocket = Stomp.over(new SockJS(API_URL + "/websockets"));
            $scope.testsWebsocket.debug = null;
            $scope.testsWebsocket.connect({withCredentials: false}, function () {
                if($scope.testsWebsocket.connected) {
                    var closeWaitingTimeout;
                    $scope.testsWebsocket.subscribe("/topic/testRuns/" + testRun.id + "/tests", function (data) {
                        var test = $scope.getEventFromMessage(data.body).test;
                        if(test.id == $scope.test.id) {
                            if(! $scope.vncDisconnected) {
                                addDrivers(getArtifactsByPartName(test, LIVE_DEMO_ARTIFACT_NAME));
                                $scope.$apply();
                            } else {
                                var artifacts = getArtifactsByPartName(test, 'video', 'live');
                                if(artifacts && artifacts.length) {
                                    setMode('record', test);
                                    if(closeWaitingTimeout) {
                                        $timeout.cancel(closeWaitingTimeout);
                                    }
                                    closeWaitingTimeout = $timeout(function () {
                                        $scope.testsWebsocket.hasClosePermission = true;
                                    }, 2000);
                                } else {
                                    addDrivers(getArtifactsByPartName(test, LIVE_DEMO_ARTIFACT_NAME));
                                    $scope.$apply();
                                }
                            }
                            $scope.test = angular.copy(test);
                        }
                    });
                }
            }, function () {
                UtilService.reconnectWebsocket(testsWebsocketName, initTestsWebSocket());
            });
            UtilService.websocketConnected(testsWebsocketName);
        };

        var rfb;
        var logsStompName;
        var logsStomp;
        $scope.logs = [];

        function followUpOnLogs(log) {
            $scope.$apply(function () {
                if(driversQueue.length) {
                    log.driver = driversQueue.pop();
                    driversQueue = [];
                }
                $scope.logs.push(log);

            });
        };

        function provideLogs() {
            var logsContainer = angular.element('#logs')[0];
            ArtifactService.provideLogs($rootScope.rabbitmq, $scope.testRun, $scope.test, logsContainer, false, followUpOnLogs).then(function (data) {
                logsStomp = data.stomp;
                logsStompName = data.name;
            });
            return logsStomp
        };

        function provideVideo() {
            var wsUrl = $scope.drivers[$scope.selectedDriver].link;
            watchUntilPainted('#vnc', function () {
                rfb = ArtifactService.connectVnc(angular.element($scope.MODE.element)[0], 'offsetHeight', 'offsetWidth', wsUrl, vncDisconnected);
            });
            return wsUrl;
        };

        function vncDisconnected() {
            $scope.vncDisconnected = true;
        };

        $scope.getEventFromMessage = function (message) {
            return JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
        };

        $scope.onResize = function() {
            ArtifactService.resize(angular.element($scope.MODE.element)[0], rfb);
        };

        /**************** Requests **************/
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

        /**************** On destroy **************/
        $scope.$on('$destroy', function () {
            closeAll();
        });

        function closeRfbConnection() {
            if(rfb && rfb._rfb_connection_state == 'connected') {
                rfb.disconnect();
            }
        };

        function closeTestsWebsocket() {
            if($scope.testsWebsocket && $scope.testsWebsocket.connected) {
                $scope.$watch('testsWebsocket.hasClosePermission', function (newVal) {
                    if(newVal) {
                        $scope.testsWebsocket.disconnect();
                        UtilService.websocketConnected(testsWebsocketName);
                    }
                });
            }
        };

        function closeAll() {
            closeRfbConnection();
            if(logsStomp && logsStomp.connected) {
                logsStomp.disconnect();
                UtilService.websocketConnected(logsStompName);
            }
            closeTestsWebsocket();
        };

        /**************** Initialization **************/
        (function init() {
            getTestRun($stateParams.id).then(function (rs) {
                $scope.testRun = rs;
                initTestsWebSocket($scope.testRun);
                getTest(rs.id).then(function (testsRs) {
                    $scope.test = testsRs.filter(function (t) {
                        return t.id === parseInt($stateParams.testId);
                    })[0];
                    var videoArtifacts;
                    if($scope.test.status == 'IN_PROGRESS') {
                        videoArtifacts = getArtifactsByPartName($scope.test, LIVE_DEMO_ARTIFACT_NAME);
                        addDrivers(videoArtifacts);
                        $scope.testRun.tests = testsRs;
                    }
                    setMode(videoArtifacts && videoArtifacts.length ? 'live' : 'record', $scope.test);
                });
            });
        })();
    }

})();
