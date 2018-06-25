(function () {
    'use strict';

    angular
        .module('app.testruninfo')
        .controller('TestRunInfoController', ['$scope', '$rootScope', '$log', '$timeout', '$window', '$q', 'ElasticsearchService', 'TestService', 'TestRunService', 'UtilService', 'ArtifactService', '$stateParams', 'OFFSET', 'API_URL', TestRunInfoController])

    // **************************************************************************
    function TestRunInfoController($scope, $rootScope, $log, $timeout, $window, $q, ElasticsearchService, TestService, TestRunService, UtilService, ArtifactService, $stateParams, OFFSET, API_URL) {

        $scope.testRun = {};
        $scope.test = {};
        $scope.drivers = [];
        var driversQueue = [];
        $scope.selectedDriver = 0;
        $scope.OFFSET = OFFSET;
        $scope.MODE = {};
        $scope.tab =  { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."};

        var LIVE_DEMO_ARTIFACT_NAME = 'live video';

        var MODES = {
            live: {
                name: 'live',
                element: '.video-wrapper',
                initFunc: initLiveMode
            },
            record: {
                name: 'record',
                element: 'video',
                initFunc: initRecordMode
            }
        };

        function postModeConstruct(test) {

            switch($scope.MODE.name) {
                case 'live':
                    provideVideo();
                    provideLogs();
                    break;
                case 'record':
                    ElasticsearchService.search($scope.testRun.ciRunId + '_' + $scope.test.id).then(function (rs) {
                        $scope.logs = rs.map(function (r) {
                            return r._source;
                        });
                    });
                    $scope.drivers = [];
                    closeAll();

                    initRecords(test);
                    break;
                default:
                    break;
            }
        };

        function setMode(modeName) {
            if($scope.MODE != modeName) {
                $scope.MODE = MODES[modeName];
            }
        };


        var isLoaded;

        function initRecords(test) {
            var videoArtifacts = getArtifactsByPartName(test, 'video', 'live');
            if (videoArtifacts && videoArtifacts.length) {
                $scope.drivers = $scope.drivers.concat(videoArtifacts.filter(function (value) {
                    return $scope.drivers.indexOfField('name', value.name) == -1;
                }));
                isLoaded = false;
                watchUntilPainted('#videoRecord:has(source[src])', reloadVideo);
            }
        };

        function reloadVideo() {
            var videoElements = angular.element('#videoRecord');
            if(videoElements && videoElements.length) {
                videoElements[0].addEventListener('loadeddata', function() {
                    isLoaded = true;
                }, false);
                videoElements[0].addEventListener('playing', function() {
                    //console.log('on play');
                }, false);
                videoElements[0].addEventListener('timeupdate', function() {
                    //console.log('timeupdate' + videoElements[0].currentTime);
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
            var vncContainer = angular.element(MODES.live.element)[0];
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
                postDriverChanged();
            }
        };

        function postDriverChanged() {
            switch($scope.MODE.name) {
                case 'live':
                    closeRfbConnection();
                    provideVideo();
                    break;
                case 'record':
                    initRecords($scope.test);
                    break;
                default:
                    break;
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
                var driversCount = 0;
                if($scope.testsWebsocket.connected) {
                    $scope.testsWebsocket.subscribe("/topic/testRuns/" + testRun.id + "/tests", function (data) {
                        var test = $scope.getEventFromMessage(data.body).test;
                        if(test.id == $scope.test.id) {

                            if(test.status == 'IN_PROGRESS') {
                                addDrivers(getArtifactsByPartName(test, LIVE_DEMO_ARTIFACT_NAME));
                                driversCount = $scope.drivers.length;
                            } else {
                                setMode('record');
                                initRecordMode(test);
                                $scope.waitForVideoArtifacts = $scope.drivers.length != driversCount ? true : false;
                            }
                            $scope.test = angular.copy(test);
                            $scope.$apply();

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
            var driversWatcher = $scope.$watchCollection('drivers', function (newVal) {
                if(newVal && newVal.length) {
                    var wsUrl = $scope.drivers[$scope.selectedDriver].link;
                    watchUntilPainted('#vnc', function () {
                        rfb = ArtifactService.connectVnc(angular.element($scope.MODE.element)[0], 'offsetHeight', 'offsetWidth', wsUrl, vncDisconnected);
                    });
                    driversWatcher();
                }
            });
        };

        function vncDisconnected() {
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
            closeTestsWebsocket();
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
        };

        /**************** Initialization **************/

        function initLiveMode(test) {
            var videoArtifacts = getArtifactsByPartName(test, LIVE_DEMO_ARTIFACT_NAME) || [];
            addDrivers(videoArtifacts);
            postModeConstruct(test);
        };

        function initRecordMode(test) {
            var videoArtifacts = getArtifactsByPartName(test, 'video', 'live') || [];
            addDrivers(videoArtifacts);
            postModeConstruct(test);
        };

        (function init() {
            getTestRun($stateParams.id).then(function (rs) {
                $scope.testRun = rs;
                initTestsWebSocket($scope.testRun);
                getTest(rs.id).then(function (testsRs) {
                    $scope.test = testsRs.filter(function (t) {
                        return t.id === parseInt($stateParams.testId);
                    })[0];
                    $scope.testRun.tests = testsRs;

                    //var videoArtifacts = getArtifactsByPartName($scope.test, LIVE_DEMO_ARTIFACT_NAME);
                    //setMode($scope.test.status == 'IN_PROGRESS' && videoArtifacts && videoArtifacts.length ? 'live' : 'record');
                    setMode($scope.test.status == 'IN_PROGRESS' ? 'live' : 'record');
                    $scope.MODE.initFunc.call(this, $scope.test);
                });
            });
        })();
    }

})();
