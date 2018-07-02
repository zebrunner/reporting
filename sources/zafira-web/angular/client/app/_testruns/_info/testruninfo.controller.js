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
        var driversCount = 0;
        $scope.elasticsearchDataLoaded = false;
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
                    $scope.selectedDriver = 0;
                    initRecords(test);
                    closeAll();
                    tryToGetLogsFromElasticsearch();
                    break;
                default:
                    break;
            }
        };

        function setMode(modeName) {
            if($scope.MODE != modeName) {
                $scope.drivers = [];
                $scope.MODE = MODES[modeName];
            }
        };

        function getLogsFromElasticsearch(page, size) {
            return $q(function (resolve, reject) {
                ElasticsearchService.search($scope.testRun.ciRunId + '_' + $scope.test.id, page, size, $scope.test.startTime).then(function (rs) {
                    resolve(rs.map(function (r) {
                        return r._source;
                    }));
                });
            });
        };

        function tryToGetLogsFromElasticsearch() {
            $scope.logs = [];
            var page = 1;
            var size = 5;
            ElasticsearchService.count($scope.testRun.ciRunId + '_' + $scope.test.id, $scope.test.startTime).then(function (count) {
                collectElasticsearchLogs(page, size, count);
            });
        };

        function collectElasticsearchLogs(page, size, count) {
            getLogsFromElasticsearch(page, size).then(function (hits) {
                $scope.logs = $scope.logs.concat(hits);
                if(page * size <= count) {
                    page ++;
                    collectElasticsearchLogs(page, size, count);
                } else {
                    $scope.elasticsearchDataLoaded = true;
                }
            });
        };

        function initRecords(test) {
            var videoArtifacts = getArtifactsByPartName(test, 'video', 'live');
            if (videoArtifacts && videoArtifacts.length) {
                $scope.drivers = $scope.drivers.concat(videoArtifacts.filter(function (value) {
                    return $scope.drivers.indexOfField('name', value.name) == -1;
                }));
                var link = $scope.drivers && $scope.drivers.length ? $scope.drivers[0].link : '';
                watchUntilPainted('#videoRecord:has(source[src = \'' + link + '\'])', reloadVideo);
            }
        };

        $scope.videoMode = {mode: "UNKNOWN"};
        var track;

        function reloadVideo(e) {
            var videoElements = angular.element(e);
            reloadVideoOnError(videoElements[0]);
            if(videoElements && videoElements.length) {
                videoElements[0].addEventListener("loadedmetadata", onMetadataLoaded, false);
                videoElements[0].addEventListener('loadeddata', onDataLoaded, false);
                videoElements[0].addEventListener('timeupdate', onTimeUpdate, false);
                videoElements[0].addEventListener('webkitfullscreenchange', onFullScreenChange, false);
                videoElements[0].addEventListener('mozfullscreenchange', onFullScreenChange, false);
                videoElements[0].addEventListener('fullscreenchange', onFullScreenChange, false);
                videoElements[0].addEventListener('ratechange', onRateChange, false);

                videoElements[0].addEventListener('playing', function() {
                    $scope.$apply(function () {
                        $scope.videoMode.mode = "PLAYING";
                    });
                }, false);
                videoElements[0].addEventListener('play', function() {
                    $scope.$apply(function () {
                        $scope.videoMode.mode = "PLAYING";
                    });
                }, false);
                videoElements[0].addEventListener('pause', function() {
                    $scope.$apply(function () {
                        $scope.videoMode.mode = "PAUSE";
                    });
                }, false);
                videoElements[0].addEventListener('loadstart', function() {
                }, false);
            }
            loadVideo(videoElements[0], 200);
        };

        function reloadVideoOnError(videoElement) {
            var sourceElement = videoElement.getElementsByTagName('source')[0];
            var attempt = 5;
            sourceElement.addEventListener('error', function(e) {
                if(attempt > 0) {
                    loadVideo(videoElement, 5000);
                }
                attempt --;
            }, false);
        };

        function onMetadataLoaded(ev) {
            track = this.addTextTrack("captions", "English", "en");
            track.mode = 'hidden';
        };

        function onTimeUpdate(ev) {
            var activeTrack = track && track.activeCues && track.activeCues.length ? track.activeCues[0] : null;
            $scope.currentTime = ev.target.currentTime;
            if(activeTrack) {
                $scope.currentLog = {id: activeTrack.id, message: activeTrack.text};
            }
        };

        function onDataLoaded(ev) {
            $scope.$apply(function () {
                $scope.videoMode.mode = "LOADED";
            });
            var videoElement = ev.target;
            var elasticsearchDataWatcher = $scope.$watch('elasticsearchDataLoaded', function (isLoaded) {
                if(isLoaded) {
                    var videoDuration = videoElement.duration;
                    var errorTime = getLogsStartErrorTime(videoDuration, $scope.logs);
                    $scope.logs.forEach(function (log, index) {
                        var currentLogTime = log.timestamp - $scope.logs[0].timestamp + errorTime;
                        log.videoTimestamp = currentLogTime / 1000;
                    });
                    addSubtitles(track, videoDuration);
                    elasticsearchDataWatcher();
                }
            });
        };

        function onFullScreenChange(ev) {
            track.mode = track.mode == 'showing' ? 'hidden' : 'showing';
        };

        function onRateChange(ev) {
        };

        function addSubtitles(track, videoDuration) {
            if(track && ! track.cues.length) {
                $scope.logs.forEach(function (log, index) {
                    var finishTime = index != $scope.logs.length - 1 ? $scope.logs[index + 1].videoTimestamp : videoDuration;
                    var vttCue = new VTTCue(log.videoTimestamp, finishTime, log.message);
                    vttCue.id = index;
                    track.addCue(vttCue);
                });
            }
        };

        function getLogsStartErrorTime(duration, logs) {
            var logsDuration = logs[logs.length - 1].timestamp - logs[0].timestamp;
            return duration * 1000 - logsDuration;
        };

        function loadVideo(videoElement, timeout) {
            $timeout(function () {
                videoElement.load();
            }, timeout);
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
                    func.call(this, elementLocator);
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
                    $scope.testsWebsocket.subscribe("/topic/testRuns/" + testRun.id + "/tests", function (data) {
                        var test = $scope.getEventFromMessage(data.body).test;
                        if(test.id == $scope.test.id) {

                            if(test.status == 'IN_PROGRESS') {
                                addDrivers(getArtifactsByPartName(test, LIVE_DEMO_ARTIFACT_NAME));
                                driversCount = $scope.drivers.length;
                            } else {
                                setMode('record');
                                var videoArtifacts = getArtifactsByPartName(test, 'video', 'live') || [];
                                if(videoArtifacts.length == driversCount) {
                                    addDrivers(videoArtifacts);
                                    postModeConstruct(test);
                                }
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
                    watchUntilPainted('#vnc', function (e) {
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
            driversCount = $scope.drivers.length;
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

                    setMode($scope.test.status == 'IN_PROGRESS' ? 'live' : 'record');
                    $scope.MODE.initFunc.call(this, $scope.test);
                });
            });
        })();
    }

})();
