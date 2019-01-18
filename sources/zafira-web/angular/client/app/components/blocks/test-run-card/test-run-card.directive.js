(function() {
    'use strict';

    angular.module('app.testRunCard').directive('testRunCard', function() {
        return {
            templateUrl: 'app/components/blocks/test-run-card/test-run-card.html',
            controller: function TestRunCardController(mediaBreakpoints, windowWidthService,
                                                       testsRunsService, $rootScope, UtilService,
                                                       $state, $timeout, $mdDialog, $mdToast,
                                                       SlackService, TestRunService, UserService,
                                                       $interval, DownloadService) {
                const local = {
                    currentUser: UserService.getCurrentUser(),
                    testRunInDebugMode: null,
                    stopConnectingDebug: null,
                    debugHost: null,
                    debugPort: null,
                    jenkins: $rootScope.jenkins,
                };
                const vm = {
                    testRun: null,
                    singleMode: false,
                    singleWholeInfo: false,
                    showNotifyInSlackOption: false,
                    showBuildNowOption: false,
                    showDeleteTestRunOption: false,
                    mobileBreakpoint: mediaBreakpoints.mobile || 0,
                    windowWidthService: windowWidthService,
                    isSlackAvailable: false,
                    slackChannels: null,
                    currentOffset: $rootScope.currentOffset,
                    tools: $rootScope.tools,

                    addToSelectedtestRuns: addToSelectedtestRuns,
                    showDetails: showDetails,
                    openMenu: openMenu,
                    openTestRun: openTestRun,
                    copyLink: copyLink,
                    markAsReviewed: markAsReviewed,
                    showCommentsDialog: showCommentsDialog,
                    sendAsEmail: sendAsEmail,
                    createSpreadsheet: createSpreadsheet,
                    exportTestRun: exportTestRun,
                    notifyInSlack: notifyInSlack,
                    buildNow: buildNow,
                    abort: abort,
                    rerun: rerun,
                    startDebug: startDebug,
                    onTestRunDelete: onTestRunDelete,
                    checkFilePresence: checkFilePresence,
                    downloadApplication: downloadApplication,
                    goToTestRun: goToTestRun,
                    onBackClick: onBackClick,
                };

                vm.$onInit = init;

                return vm;

                function init() {
                    initSlackChannels();
                    initSlackAvailability();
                }

                function initSlackChannels() {
                    vm.slackChannels = testsRunsService.getSlackChannels();

                    if (!vm.slackChannels) {
                        testsRunsService.fetchSlackChannels().then(function(slackChannels) {
                            vm.slackChannels = slackChannels;
                        });
                    }
                }

                function initSlackAvailability() {
                    if (testsRunsService.isSlackAvailabilityFetched()) {
                        vm.isSlackAvailable = testsRunsService.getSlackAvailability();
                    } else {
                        testsRunsService.fetchSlackAvailability().then(function(isSlackAvailable) {
                            vm.isSlackAvailable = isSlackAvailable;
                        });
                    }
                }

                function addToSelectedtestRuns() {
                    vm.onSelect && vm.onSelect(vm.testRun);
                }

                function showDetails(value) {
                    vm.singleWholeInfo = value;
                }

                function initMenuRights() {
                    vm.showNotifyInSlackOption = (vm.isSlackAvailable && vm.slackChannels.indexOf(vm.testRun.job.name) !== -1) && vm.testRun.reviewed;
                    vm.showBuildNowOption = local.jenkins.enabled;
                    vm.showDeleteTestRunOption = true;
                }

                function openMenu($event, $msMenuCtrl) {
                    initMenuRights();
                    UtilService.setOffset($event);
                    $timeout(function() {
                        vm.currentOffset = $rootScope.currentOffset;
                        $msMenuCtrl.open($event);
                    });
                }

                function openTestRun() {
                    const url = $state.href('tests/run', {testRunId: vm.testRun.id});

                    window.open(url,'_blank');
                }

                function goToTestRun() {
                    $state.go('tests/run', {testRunId: vm.testRun.id, testRun: vm.testRun});
                }

                function onBackClick() {
                    $state.go('tests/runs', {activeTestRunId: vm.testRun.id});
                }

                function copyLink() {
                    const url = $state.href('tests/run', {testRunId: vm.testRun.id}, {absolute : true});

                    url.copyToClipboard();
                }

                function markAsReviewed() {
                    showCommentsDialog();
                }

                function sendAsEmail(event) {
                    showEmailDialog([vm.testRun], event);
                }

                function createSpreadsheet(event) {
                    showCreateSpreadsheetDialog([vm.testRun], event);
                };

                function showCommentsDialog(event) {
                    $mdDialog.show({
                        controller: 'CommentsController',
                        templateUrl: 'app/components/modals/comments/comments.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose:true,
                        fullscreen: true,
                        locals: {
                            testRun: vm.testRun,
                            isSlackAvailable: vm.isSlackAvailable,
                            slackChannels: vm.slackChannels
                        }
                    }).then(function(answer) {
                        vm.testRun.reviewed = answer.reviewed;
                        vm.testRun.comments = answer.comments;
                    });
                }

                function showEmailDialog(testRuns, event) {
                    $mdDialog.show({
                        controller: 'EmailController',
                        templateUrl: 'app/components/modals/email/email.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose:true,
                        fullscreen: true,
                        locals: {
                            testRuns: testRuns
                        }
                    });
                }

                function showCreateSpreadsheetDialog(testRuns, event) {
                    $mdDialog.show({
                        controller: 'SpreadsheetController',
                        templateUrl: 'app/components/modals/spreadsheet/spreadsheet.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose:true,
                        fullscreen: true,
                        locals: {
                            testRuns: testRuns
                        }
                    })
                    .then(undefined, function(links) {
                        if (links && links.length) {
                            showToastWithLinks();
                        }
                    });
                }

                function showToastWithLinks(links) {
                    $mdToast.show({
                        hideDelay: 0,
                        position: 'bottom right',
                        locals: {
                            links: links
                        },
                        controller: function ToastWithLinksController($mdToast, links) {
                            return {
                                links: links,

                                closeToast: closeToast,
                            };

                            function closeToast() {
                                $mdToast.hide();
                            }
                        },
                        controllerAs: '$ctrl',
                        template: '<md-toast>\n' +
                            '    <a target="_blank" ng-repeat="link in links" ng-href="{{ link }}" class="md-toast-text" flex>Google spreadsheet</a>\n' +
                            '    <md-button id="close" ng-click="$ctrl.closeToast();">\n' +
                            '        Close\n' +
                            '    </md-button>\n' +
                            '</md-toast>'
                    });
                }

                function exportTestRun() {
                    TestRunService.exportTestRunResultsHTML(vm.testRun.id).then(function(rs) {
                        if (rs.success) {
                            downloadFromByteArray(vm.testRun.testSuite.name.split(' ').join('_') + '.html', rs, 'html');
                        } else {
                            alertify.error(rs.message);
                        }
                    });
                }

                function downloadFromByteArray(filename, array, contentType) {
                    const blob = new Blob([array.data], {type: contentType ? contentType : array.headers('Content-Type')});
                    const link = document.createElement('a');

                    link.style = 'display: none';
                    document.body.appendChild(link);
                    link.href = window.URL.createObjectURL(blob);
                    link.download = filename;
                    link.click();

                    //remove link after 10sec
                    $timeout(() => {
                        link && document.body.removeChild(link);
                    }, 10000);
                }

                function notifyInSlack(testRun) {
                    SlackService.triggerReviewNotif(vm.testRun.id);
                }

                function buildNow(event) {
                    showBuildNowDialog(event);
                }

                function rerun(event) {
                    showRerunDialog(event);
                }

                function showRerunDialog(event) {
                    $mdDialog.show({
                        controller: 'TestRunRerunController',
                        templateUrl: 'app/components/modals/rerun/rerun.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose: true,
                        fullscreen: true,
                        locals: {
                            testRun: vm.testRun,
                            jenkins: local.jenkins
                        }
                    });
                }

                function startDebug() {
                    if (confirm('Start debugging?')) {
                        local.testRunInDebugMode = angular.copy(vm.testRun);
                        debugTestRun(local.testRunInDebugMode);
                    }
                }

                function debugTestRun(testRunForDebug) {
                    TestRunService.debugTestRun(testRunForDebug.id).then(function (rs) {
                        if (rs.success) {
                            showDebugToast();
                            let debugLog = '';
                            let disconnectDebugTimeout;
                            const parseLogsInterval = $interval(function() {
                                TestRunService.getConsoleOutput(testRunForDebug.id, testRunForDebug.ciRunId, 200, 50).then(function (rs) {
                                    if (rs.success) {
                                        const map = rs.data;

                                        Object.keys(map).forEach(function(key) {
                                            const value = map[key];

                                            if (value.includes('Listening for transport dt_socket at address:')) {
                                                if (debugLog === '') {
                                                    getDebugData(value);
                                                }

                                                $timeout.cancel(connectDebugTimeout);

                                                disconnectDebugTimeout = $timeout(function() {
                                                    stopDebugMode();
                                                    $mdToast.hide();
                                                }, 60 * 10 * 1000);

                                                if (debugLog === '') {
                                                    debugLog = value;
                                                }

                                                if (debugLog !== value) {
                                                    $timeout.cancel(disconnectDebugTimeout);
                                                    $interval.cancel(parseLogsInterval);
                                                    mdToast.hide();
                                                    alertify.success('Tests started in debug');
                                                }
                                            }
                                        });
                                    } else {
                                        stopDebugMode();
                                        alertify.error(rs.message);
                                    }
                                });
                            }, 10000);
                            const connectDebugTimeout = $timeout(function() {
                                alertify.error('Problems with starting debug mode occurred, disabling');
                                stopDebugMode();
                            }, 60 * 10 * 1000);

                            local.stopConnectingDebug = function() {
                                $interval.cancel(parseLogsInterval);
                                $timeout.cancel(disconnectDebugTimeout);
                                $timeout.cancel(connectDebugTimeout);
                            };

                        } else {
                            alertify.error(rs.message);
                        }
                    });
                }

                function getDebugData(log){
                    if (log) {
                        const portLine = log.split('Enabling remote debug on ');
                        const debugValues = portLine[1].split(':');

                        local.debugHost = debugValues[0];
                        local.debugPort = debugValues[1].split('\n')[0];
                    }
                }

                function showDebugToast() {
                    $mdToast.show({
                        hideDelay: 1200000,
                        position: 'bottom right',
                        locals: {
                            debugPort: local.debugPort,
                            debugHost: local.debugHost,
                            stopDebugMode: stopDebugMode
                        },
                        controller : 'DebugModeController',
                        controllerAs: '$ctrl',
                        bindToController: true,
                        templateUrl : 'app/components/toasts/debug-mode/debug-mode.html'
                    });
                }

                function stopDebugMode() {
                    local.stopConnectingDebug && local.stopConnectingDebug();
                    if (local.testRunInDebugMode) {
                        abortDebug(local.testRunInDebugMode);
                        local.testRunInDebugMode = null;
                        local.debugHost = null;
                        local.debugPort = null;
                        alertify.warning('Debug mode is disabled');
                    }
                }

                function abortDebug(debuggedTestRun) {
                    if (local.jenkins.enabled) {
                        TestRunService.abortDebug(debuggedTestRun.id, debuggedTestRun.ciRunId).then(function (rs) {
                            if (rs.success) {
                                const abortCause = {};

                                abortCause.comment = 'Debug mode was disconnected';
                                TestRunService.abortTestRun(debuggedTestRun.id, debuggedTestRun.ciRunId, abortCause).then(function(rs) {
                                    if (rs.success) {
                                        debuggedTestRun.status = 'ABORTED';
                                        alertify.success('Testrun ' + debuggedTestRun.testSuite.name + ' is aborted');
                                    } else {
                                        alertify.error(rs.message);
                                    }
                                });
                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    } else {
                        alertify.error('Unable connect to jenkins');
                    }
                }

                function showBuildNowDialog(event) {
                    $mdDialog.show({
                        controller: 'BuildNowController',
                        templateUrl: 'app/components/modals/build-now/build-now.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose:true,
                        fullscreen: true,
                        locals: {
                            testRun: vm.testRun
                        }
                    });
                }

                function abort() {
                    if (local.jenkins.enabled) {
                        TestRunService.abortCIJob(vm.testRun.id, vm.testRun.ciRunId).then(function (rs) {
                            if (rs.success) {
                                const abortCause = {};

                                abortCause.comment = 'Aborted by ' + local.currentUser.username;
                                TestRunService.abortTestRun(vm.testRun.id, vm.testRun.ciRunId, abortCause).then(function(rs) {
                                    if (rs.success){
                                        vm.testRun.status = 'ABORTED';
                                        alertify.success('Testrun ' + vm.testRun.testSuite.name + ' is aborted');
                                    } else {
                                        alertify.error(rs.message);
                                    }
                                });
                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    } else {
                        alertify.error('Unable connect to jenkins');
                    }
                }

                function onTestRunDelete() {
                    if (vm.singleMode) {
                        deleteTestRun();
                    } else {
                        vm.onDelete && vm.onDelete(vm.testRun);
                    }
                }

                function deleteTestRun() {
                    const confirmation = confirm('Do you really want to delete "' + vm.testRun.testSuite.name + '" test run?');

                    if (confirmation) {
                        const id = vm.testRun.id;
                        TestRunService.deleteTestRun(id).then(function(rs) {
                            const messageData = rs.success ? {success: rs.success, id: id, message: 'Test run{0} {1} removed'} : {id: id, message: 'Unable to delete test run{0} {1}'};

                            UtilService.showDeleteMessage(messageData, [id], [], []);
                            if (rs.success) {
                                $timeout(function() {
                                    testsRunsService.clearDataCache();
                                    $state.go('tests/runs');
                                }, 1000);
                            }
                        });
                    }
                }

                function checkFilePresence() {
                    if (!vm.testRun.appVersionValid) {
                        vm.testRun.appVersionLoading = true;
                        DownloadService.check(vm.testRun.appVersion).then(function (rs) {
                            if (rs.success) {
                                vm.testRun.appVersionValid = rs.data;
                            } else {
                                //alertify.error(rs.message);
                            }
                            delete vm.testRun.appVersionLoading;

                            return rs.data;
                        });
                    }
                }

                function downloadApplication() {
                    const appVersion = $ctrl.testRun.appVersion;

                    DownloadService.download(appVersion).then(function (rs) {
                        if (rs.success) {
                            downloadFromByteArray(appVersion, rs.res);
                        } else {
                            alertify.error(rs.message);
                        }
                    });
                }
            },
            scope: {
                singleMode: '=',
                testRun: '=',
                onSelect: '&',
                onDelete: '&'
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true,
            bindToController: true
        };
    });
})();
