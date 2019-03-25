'use strict';

import ImagesViewerController from '../../components/modals/images-viewer/images-viewer.controller';

const JSZip = require('jszip');
const testRunInfoController = function testRunInfoController($scope, $rootScope, $http, $mdDialog, $interval, $log,
                                                             $filter, $anchorScroll, $location, $timeout, $window, $q,
                                                             elasticsearchService, TestRunService, UtilService,
                                                             ArtifactService, DownloadService, $stateParams, OFFSET,
                                                             API_URL, $state, $httpMock, TestRunsStorage,
                                                             TestService, $transitions) {
    'ngInject';

    const TENANT = $rootScope.globals.auth.tenant;
    const vm = {
        testRun: null,
        wsSubscription: null,
    };

    vm.$onInit = controllerInit;

    $scope.testRun = {};
    $scope.test = {};
    $scope.drivers = [];
    $scope.thumbs = {};
    var driversQueue = [];
    var driversCount = 0;
    $scope.elasticsearchDataLoaded = false;
    $scope.selectedDriver = 0;
    $scope.OFFSET = OFFSET;
    $scope.MODE = {};
    $scope.tab =  { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."};
    $scope.TestRunsStorage = TestRunsStorage;

    $scope.goToTestRuns = function () {
        $state.go('tests.runDetails', {
            testRunId: vm.testRun.id
        });
    };

    var from = 0;

    var page = 1;
    var size = 5;

    var LIVE_DEMO_ARTIFACT_NAME = 'live video';
    var SEARCH_CRITERIA = '';
    var ELASTICSEARCH_INDEX = '';
    var UTC = 'UTC';

    var MODES = {
        live: {
            name: 'live',
            element: '.testrun-info__tab-video-wrapper',
            initFunc: initLiveMode,
            logGetter: {
                from: 0,
                pageCount: null,
                getSizeFunc: function (count) {
                    return count - MODES.live.logGetter.from;
                },
                accessFunc: function (count) {
                    return count > MODES.live.logGetter.from;
                }
            }

        },
        record: {
            name: 'record',
            element: 'video',
            initFunc: initRecordMode,
            logGetter: {
                from: null,
                pageCount: page,
                getSizeFunc: function (count) {
                    return count;
                },
                accessFunc: null
            }
        }
    };

    var LIVE_LOGS_INTERVAL_NAME = 'liveLogsFromElasticsearch';
    var scrollEnable = true;

    function postModeConstruct(test) {
        var logGetter = MODES[$scope.MODE.name].logGetter;
        switch($scope.MODE.name) {
            case 'live':
                provideVideo();
                $scope.logs = [];
                tryToGetLogsLiveFromElasticsearch(logGetter, LIVE_LOGS_INTERVAL_NAME);
                break;
            case 'record':
                $scope.selectedDriver = 0;
                initRecords(test);
                closeAll();

                $scope.logs = [];
                $scope.thumbs = {};
                unrecognizedImages = {};
                scrollEnable = false;
                tryToGetLogsHistoryFromElasticsearch(logGetter).then(function (rs) {
                    $timeout(function () {
                        logGetter.pageCount = null;
                        logGetter.from = $scope.logs.length + Object.size($scope.thumbs) * 2 + Object.size(unrecognizedImages);
                        function update() {
                            $timeout(function() {
                                if (Object.size(unrecognizedImages) > 0) {
                                    tryToGetLogsHistoryFromElasticsearch(logGetter);
                                    update();
                                }
                            }, 5000, false);
                        }
                        tryToGetLogsHistoryFromElasticsearch(logGetter).then(function (rs) {
                            update();
                        });
                    }, 5000);
                });
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

    function getLogsFromElasticsearch(from, page, size) {
        return $q(function (resolve, reject) {
            elasticsearchService.search(ELASTICSEARCH_INDEX, SEARCH_CRITERIA, from, page, size, $scope.test.startTime).then(function (rs) {
                resolve(rs.map(function (r) {
                    return r._source;
                }));
            });
        });
    };

    function tryToGetLogsHistoryFromElasticsearch(logGetter) {
        return $q(function (resolve, reject) {
            elasticsearchService.count(ELASTICSEARCH_INDEX, SEARCH_CRITERIA, $scope.test.startTime).then(function (count) {
                if(logGetter.accessFunc ? logGetter.accessFunc.call(this, count) : true) {
                    var size = logGetter.getSizeFunc.call(this, count);
                    collectElasticsearchLogs(logGetter.from, logGetter.pageCount, size, count, resolve);
                }
            }, function() {});
        });
    };

    function tryToGetLogsLiveFromElasticsearch(logGetter, logIntervalName) {
        return $q(function (resolve, reject) {
            pseudoLiveDoAction(logIntervalName, 5000, function () {
                getLogsLiveFromElasticsearch(logGetter);
            });
        });
    };

    function getLogsLiveFromElasticsearch(logGetter) {
        tryToGetLogsHistoryFromElasticsearch(logGetter).then(function (count) {
            MODES.live.logGetter.from = count;
        });
    };

    var liveIntervals = {};

    function pseudoLiveDoAction(intervalName, intervalMillis, func) {
        func.call();
        liveIntervals[intervalName] = $interval(function() {func.call()}, intervalMillis);
    };

    function pseudoLiveCloseAction(intervalName) {
        $interval.cancel(liveIntervals[intervalName]);
    }

    function collectElasticsearchLogs(from, page, size, count, resolveFunc) {

        getLogsFromElasticsearch(from, page, size).then(function (hits) {
            hits.forEach(function (hit) {
                followUpOnLogs(hit);
            });
            if(! from && from != 0 && (page * size < count)) {
                page ++;
                collectElasticsearchLogs(from, page, size, count, resolveFunc);
            } else {
                $scope.elasticsearchDataLoaded = true;
                resolveFunc.call(this, count);
                var hash = $location.hash();
                if(hash && scrollEnable) {
                    $anchorScroll();
                }
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
        var attempt = new Date().getTime() - $scope.test.finishTime > 600000 ? 1 : 5;
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
            $scope.$apply(function () {
                $scope.currentLog = {id: activeTrack.id, message: activeTrack.text};
            });
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

    $scope.getVideoState = function (log) {
        var videoElement = angular.element('#videoRecord');
        videoElement[0].currentTime = log.videoTimestamp;
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

    $scope.selectedLogRow = -1;

    $scope.selectLogRow = function(ev, index) {
        var hash = ev.currentTarget.parentNode.attributes.id.value;
        $location.hash(hash);
    };

    $scope.copyLogLine = function(log) {
        var message = $filter('date')(new Date(log.timestamp), 'HH:mm:ss') + ' [' + log.threadName + '] ' + '[' + log.level + '] ' + log.message;
        message.copyToClipboard();
    };

    $scope.copyLogPermalink = function() {
        $location.$$absUrl.copyToClipboard();
    };

    $scope.$watch(function () {
        return $location.hash()
    }, function (newVal, oldVal) {
        var selectedLogRowClass = 'selected-log-row';
        if(newVal && oldVal) {
            if (newVal == oldVal) {
                watchUntilPainted('#' + newVal, function () {
                    angular.element('#' + newVal).addClass(selectedLogRowClass);
                });
            } else {
                angular.element('#' + newVal).addClass(selectedLogRowClass);
                angular.element('#' + oldVal).removeClass(selectedLogRowClass);
            }
            $scope.selectedLogRow = newVal.split('log-')[1];
        }
    });

    $scope.fullScreen = function(minimizeOnly) {
        var fullScreenClass = 'full-screen';
        var vncContainer = angular.element(MODES.live.element)[0];
        var hideArray = ['.testrun-info__tab-table-wrapper', '.testrun-info__tab-additional'];
        if(vncContainer.classList.contains(fullScreenClass)) {
            vncContainer.classList.remove(fullScreenClass);
            hideArray.forEach(function (value) {
                angular.element(value)[0].style.display = 'block';
            });
            $scope.onResize();
        } else if(! minimizeOnly) {
            vncContainer.classList.add(fullScreenClass);
            hideArray.forEach(function (value) {
                angular.element(value)[0].style.display = 'none';
            });
            $scope.onResize();
        }
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

    $scope.downloadAll = function() {
        if (!$scope.test.imageArtifacts.length) { return; }

        const promises = $scope.test.imageArtifacts.map((artifact) => {
            return DownloadService.plainDownload(artifact.link)
                .then(response => {
                    if (response.success) {
                        return {
                            fileName: `${artifact.name}.${artifact.extension}`,
                            fileData: response.res.data,
                        };
                    }

                    return $q.reject(false);
                });
        });

        $q.all(promises)
            .then(data => {
                const name = $scope.test.id + '. ' + $scope.test.name;
                const formattedData = data.reduce((out, item) => {
                    out[item.fileName] = item.fileData;

                    return out;
                }, {});

                downloadZipFile(name, formattedData);
            })
            .catch(() => {
                alertify.error('Unable to download all files, please try again.');
            });
    };

    function downloadZipFile(name, data) {
        const zip = new JSZip();
        const folder = zip.folder(name);

        angular.forEach(data, function (blob, blobName) {
            folder.file(blobName.getValidFilename(), blob, {base64: true});
        });
        zip.generateAsync({type:"blob"}).then(function(content) {
            content.download(name + '.zip');
        });
    }

    function prepareArtifacts(test) {
        const formattedArtifacts = test.artifacts.reduce(function(formatted, artifact) {
            const name = artifact.name.toLowerCase();

            if (!name.includes('live') && !name.includes('video')) {
                const links = artifact.link.split(' ');
                const pathname = new URL(links[0]).pathname;

                artifact.extension = pathname.split('/').pop().split('.').pop();
                if (artifact.extension === 'png') {
                    if (links[1]) {
                        artifact.link = links[0];
                        artifact.thumb = links[1];
                    }
                    formatted.imageArtifacts.push(artifact);
                }
                formatted.artifactsToShow.push(artifact);
            }

            return formatted;
        }, {imageArtifacts: [], artifactsToShow: []});

        test.imageArtifacts = formattedArtifacts.imageArtifacts;
        test.artifactsToShow = formattedArtifacts.artifactsToShow;
    }

    $scope.openImagesViewerModal = function(event, url) {
        const activeArtifact = $scope.test.imageArtifacts.find(function(art) {
            return art.link === url;
        });

        if (activeArtifact) {
            $mdDialog.show({
                controller: ImagesViewerController,
                template: require('../../components/modals/images-viewer/images-viewer.html'),
                controllerAs: '$ctrl',
                bindToController: true,
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: false,
                escapeToClose: false,
                locals: {
                    test: $scope.test,
                    activeArtifactId: activeArtifact.id,
                }
            });
        }
    };

    /**************** Websockets **************/
    var testsWebsocketName = 'tests';

    function initTestsWebSocket(testRun) {
        $scope.testsWebsocket = Stomp.over(new SockJS(API_URL + "/api/websockets"));
        $scope.testsWebsocket.debug = null;
        $scope.testsWebsocket.connect({withCredentials: false}, function () {
            if($scope.testsWebsocket.connected) {
                vm.wsSubscription = $scope.testsWebsocket.subscribe("/topic/" + TENANT + ".testRuns." + testRun.id + ".tests", function (data) {
                    var test = $scope.getEventFromMessage(data.body).test;

                    if($scope.test && test.id === $scope.test.id) {

                        if(test.status === 'IN_PROGRESS') {
                            addDrivers(getArtifactsByPartName(test, LIVE_DEMO_ARTIFACT_NAME));
                            driversCount = $scope.drivers.length;
                        } else {
                            pseudoLiveCloseAction(LIVE_LOGS_INTERVAL_NAME);
                            $scope.fullScreen(true);
                            setMode('record');
                            var videoArtifacts = getArtifactsByPartName(test, 'video', 'live') || [];
                            if(videoArtifacts.length === driversCount) {
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
            UtilService.reconnectWebsocket(testsWebsocketName, initTestsWebSocket);
        });
        UtilService.websocketConnected(testsWebsocketName);
    };

    var rfb;
    var logsStompName;
    var logsStomp;

    function followUpOnLogs(log) {
        if ($scope.MODE.name == 'live' && driversQueue.length) {
            log.driver = driversQueue.pop();
            driversQueue = [];
        }
        collectLogs(log);
    };

    $scope.logs = [];
    var unrecognizedImages = {};

    function collectLogs(log) {
        switch (log.level) {
            case 'META_INFO':
                collectScreenshots(log);
                break;
            default:
                $scope.logs.push(log);
                break;
        }
        $scope.$applyAsync();
    };

    function collectScreenshots(log) {
        var correlationId = getMetaLogCorrelationId(log);
        var isThumbnail = isThumb(log);
        var existsUnrecognizedImage = getUnrecognizedImageExists(isThumbnail, correlationId);
        if (existsUnrecognizedImage) {
            catchScreenshot(log, existsUnrecognizedImage, correlationId, isThumbnail);
        } else {
            preScreenshot(log, correlationId, isThumbnail);
        }
    };

    function preScreenshot(log, correlationId, isThumbnail) {
        var index = $scope.logs.length - 1;
        var appenToLog = $scope.logs[index];
        appenToLog.blobLog = appenToLog.blobLog || {};
        if(isThumbnail) {
            appenToLog.blobLog.thumb = log;
        } else {
            appenToLog.blobLog.image = log;
        }
        appenToLog.isImageExists = false;
        unrecognizedImages[correlationId] = unrecognizedImages[correlationId] || {};
        if(isThumbnail) {
            unrecognizedImages[correlationId].thumb = {'log': appenToLog, 'index': index};
        } else {
            unrecognizedImages[correlationId].image = {'log': appenToLog, 'index': index};
        }
    };

    function catchScreenshot(log, preScreenshot, correlationId, isThumbnail) {
        var path;
        var prefix = isThumbnail ? 'thumb_' : 'img_';
        $scope.thumbs[prefix + correlationId] = {'log': preScreenshot.log.message, 'thumb': log, 'index': preScreenshot.index, 'path': path};
        if(isThumbnail) {
            path = getMetaLogThumbAmazonPath(log);
            preScreenshot.log.blobLog.thumb.path = path;
            delete unrecognizedImages[correlationId].thumb;
        } else {
            path = getMetaLogAmazonPath(log);
            preScreenshot.log.blobLog.image.path = preScreenshot.log.blobLog.image.path || [];
            preScreenshot.log.blobLog.image.path.push(path);
            delete unrecognizedImages[correlationId].image;
        }
        preScreenshot.log.isImageExists = true;
    };

    function getUnrecognizedImageExists(isThumbnail, correlationId) {
        if(!unrecognizedImages[correlationId]) {
            return false;
        }
        return isThumbnail ? unrecognizedImages[correlationId].thumb : unrecognizedImages[correlationId].image;
    };

    function getMetaLogCorrelationId(log) {
        return getMetaLogHeader(log, 'AMAZON_PATH_CORRELATION_ID');
    };

    function getMetaLogAmazonPath(log) {
        return getMetaLogHeader(log, 'AMAZON_PATH');
    };

    function getMetaLogThumbAmazonPath(log) {
        return getMetaLogHeader(log, 'THUMB_AMAZON_PATH');
    };

    function getMetaLogHeader(log, headerName) {
        return log.headers[headerName];
    };

    function isThumb(log) {
        return getMetaLogThumbAmazonPath(log) !== undefined;
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
            TestService.searchTests({testRunId: testRunId, page: 1, pageSize: 100000}).then(function (rs) {
                if(rs.success && rs.data.results) {
                    resolve(rs.data.results);
                } else {
                    reject(rs.message);
                }
            });
        });
    };

    /**************** On destroy **************/
    function bindEvents() {
        $scope.$on('$destroy', function () {
            cancelIntervals();
            closeAll();
            vm.wsSubscription && vm.wsSubscription.unsubscribe();
            closeTestsWebsocket();
        });

        const onTransStartSubscription = $transitions.onStart({}, function(trans) {
            const toState = trans.to();

            if (toState.name !== 'tests.runDetails') {
                TestService.clearDataCache();
            }
            onTransStartSubscription();
        });
    }

    function cancelIntervals() {
        Object.keys(liveIntervals).forEach(name => {
            pseudoLiveCloseAction(name);
        });
    }

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

    function buildIndex() {
        var startTime = 'logs-' + $filter('date')($scope.test.startTime, 'yyyy.MM.dd', UTC);
        var finishTime = $scope.test.finishTime ? 'logs-' + $filter('date')($scope.test.finishTime, 'yyyy.MM.dd', UTC) : 'logs-' + $filter('date')(new Date().getTime(), 'yyyy.MM.dd', UTC);
        return startTime == finishTime ? startTime : startTime + ',' + finishTime;
    };

    function controllerInit() {
        $scope.testRun = angular.copy(vm.testRun);
        initTestsWebSocket($scope.testRun);

        const params = {
            'page': 1,
            'pageSize': 100000,
            'testRunId': vm.testRun.id
        };

        TestService.searchTests(params)
        .then(function (rs) {
            if (rs.success) {
                const data = rs.data.results || [];
                vm.testRun.tests = {};
                TestService.setTests = data;
                setTestParams();
            } else {
                console.error(rs.message);
            }
        })
        .finally(() => {
            bindEvents();
        });
    }

    function setTestParams() {
        const testId = parseInt($stateParams.testId, 10);

        $scope.test = TestService.getTest(testId);
        if ($scope.test) {
            SEARCH_CRITERIA = {'correlation-id': $scope.testRun.ciRunId + '_' + $scope.test.ciTestId};
            ELASTICSEARCH_INDEX = buildIndex();

            setMode($scope.test.status === 'IN_PROGRESS' ? 'live' : 'record');
            $scope.MODE.initFunc.call(this, $scope.test);
            prepareArtifacts($scope.test);
        }
    }

    return vm;
};

export default testRunInfoController;
