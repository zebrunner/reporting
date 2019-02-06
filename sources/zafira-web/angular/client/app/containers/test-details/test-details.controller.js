(function () {
    'use strict';

    angular
    .module('app.testDetails')
    .controller('TestDetailsController', TestDetailsController);

    // **************************************************************************
    function TestDetailsController(testRun, $scope, $rootScope, $mdToast,
                                   $mdMenu, $location, $window, $cookieStore,
                                   $mdDialog, $mdConstant, $interval, $timeout,
                                   $stateParams, $mdDateRangePicker, $q,
                                   FilterService, ProjectService, TestService,
                                   TestRunService, UtilService, UserService,
                                   SettingsService, ProjectProvider,
                                   ConfigService, SlackService, DownloadService,
                                   API_URL, DEFAULT_SC, OFFSET, TestRunsStorage,
                                   $tableExpandUtil, modalsService, $state) {
        'ngInject';

        const testGroupDataToStore = {
            statuses: [],
            tags: []
        };
        let TENANT;
        const vm = {
            currentMode: 'ONE',
            reverse: false,
            predicate: 'startTime',
            tags: [],
            testGroups: null,
            testGroupMode: 'PLAIN',
            testRun: testRun,
            // mobileBreakpoint: mediaBreakpoints.mobile || 0,
            // windowWidthService: windowWidthService,
            testsTagsOptions: {},
            testsStatusesOptions: {},
            subscriptions: {},
            zafiraWebsocket: null,
            showRealTimeEvents: true,

            onStatusButtonClick: onStatusButtonClick,
            onTagSelect: onTagSelect,
            resetTestsGrouping: resetTestsGrouping,
            selectTestGroup: selectTestGroup,
            switchTestGroupMode: switchTestGroupMode,
            changeTestStatus: changeTestStatus,
            showDetailsDialog: showDetailsDialog,
            goToTestDetails: goToTestDetails,
        };

        vm.$onInit = controlInit;

        return vm;

        function controlInit() {
            TENANT = $rootScope.globals.auth.tenant;
            initTestGroups();
            initWebsocket();
            initTests();
            fillTestRunMetadata();
            bindEvents();
        }

        function fillTestRunMetadata() {
            addBrowserVersion();
            initJobMetadata();
        }

        function addBrowserVersion() {
            var platform = vm.testRun.platform ? vm.testRun.platform.split(' ') : [];
            var version = null;

            if (platform.length > 1) {
                version = 'v.' + platform[1];
            }

            if(!version && vm.testRun.config && vm.testRun.config.browserVersion !== '*') {
                version = vm.testRun.config.browserVersion;
            }

            vm.testRun.browserVersion = version;
        }

        /**
         * Set default value for testGroups
         */
        function initTestGroups() {
            vm.testGroups = {
                group: {
                    'package': {
                        data: {},
                        selectedName: undefined
                    },
                    'class': {
                        data: {},
                        selectedName: undefined
                    },
                    common: {
                        data: {
                            'all': []
                        },
                        selectedName: 'all'
                    }
                },
                reverse: false,
                predicate: 'startTime',
                mode: 'package',
                apply: false
            };
        }

        function initJobMetadata() {
            if (vm.testRun.job && vm.testRun.job.jobURL) {
                !vm.testRun.jenkinsURL && (vm.testRun.jenkinsURL = vm.testRun.job.jobURL + '/' + vm.testRun.buildNumber);
                !vm.testRun.UID && (vm.testRun.UID = vm.testRun.testSuite.name + ' ' + vm.testRun.jenkinsURL);
            }
        }

        function initTests() {
            vm.testGroups.mode = 'common';

            loadTests(vm.testRun.id)
                .then(function () {
                    vm.testGroups.group.common.data.all = vm.testRun.tests;
                    showTestsByTags(vm.testRun.tests);
                    showTestsByStatuses(vm.testRun.tests);
                    vm.testRun.tags = collectTags(vm.testRun.tests);
                });
                vm.subscriptions[vm.testRun.id] = subscribeTestsTopic(vm.testRun.id);
        }

        function loadTests(testRunId) {
            const defer = $q.defer();
            const params = {
                'page': 1,
                'pageSize': 100000,
                'testRunId': testRunId
            };

            TestService.searchTests(params)
                .then(function (rs) {
                    if (rs.success) {
                        const data = rs.data.results || [];

                        data.forEach(function(test) {
                            addTest(test);
                        });

                        defer.resolve(angular.copy(data));
                    } else {
                        console.error(rs.message);
                        defer.reject(rs.message);
                    }
                });

            return defer.promise;
        }
        
        function goToTestDetails(testId) {
            $state.go('tests/runs/info', {
                testRun: vm.testRun,
                testRunId: vm.testRun.id,
                testId: testId
            });
        }

        function addTest(test) {
            test.elapsed = test.finishTime ? (test.finishTime - test.startTime) : Number.MAX_VALUE;

            test.artifactsToShow = test.artifacts.filter(function (artifact) {
                var name = artifact.name.toLowerCase();

                return !name.includes('live') && !name.includes('video');
            });
            test.tags = test.tags.filter(function (tag) {
                return tag.name !== 'TESTRAIL_TESTCASE_UUID' && tag.name !== 'QTEST_TESTCASE_UUID';
            });

            vm.testRun.tests = vm.testRun.tests || {};
            vm.testRun.tests[test.id] = test;

            if (vm.testGroupMode === 'PLAIN') {
                vm.testRun.tags = collectTags(vm.testRun.tests);
            } else {
                addGroupingItem(test);
            }

            onTagSelect(testGroupDataToStore.tags);
            onStatusButtonClick(testGroupDataToStore.statuses);
        }

        function collectTags(tests) {
            var result = [];

            angular.forEach(tests, function (test) {
                test.tags.forEach(function (tag) {
                    if (result.indexOfField('value', tag.value) === -1) {
                        result.push(tag);
                    }
                });
            });

            return result;
        }

        function addGroupingItem(test) {
            if (!vm.testGroups.group.package.data[test.notNullTestGroup] && test.notNullTestGroup) {
                vm.testGroups.group.package.data[test.notNullTestGroup] = [];
            }

            if (!vm.testGroups.group.class.data[test.testClass] && test.testClass) {
                vm.testGroups.group.class.data[test.testClass] = [];
            }

            var groupPackageIndex = vm.testGroups.group.package.data[test.notNullTestGroup].indexOfField('id', test.id);
            var classPackageIndex = vm.testGroups.group.class.data[test.testClass].indexOfField('id', test.id);

            if (groupPackageIndex !== -1) {
                vm.testGroups.group.package.data[test.notNullTestGroup].splice(groupPackageIndex, 1, test);
            } else {
                vm.testGroups.group.package.data[test.notNullTestGroup].push(test);
            }

            if (classPackageIndex !== -1) {
                vm.testGroups.group.class.data[test.testClass].splice(classPackageIndex, 1, test);
            } else {
                vm.testGroups.group.class.data[test.testClass].push(test);
            }
        }

        function showTestsByTags(tests, tags) {
            angular.forEach(tests, function (test) {
                test.show = false;
                if (tags && tags.length) {
                    tags.forEach(function (tag) {
                        if (!test.show) {
                            test.show = test.tags.map(function (testTag) {
                                return testTag.value;
                            }).includes(tag);
                        }
                    });
                } else {
                    test.show = true;
                }
            });
        }

        function showTestsByStatuses(tests, statuses) {
            angular.forEach(tests, function (test) {
                test.showByStatus = false;
                if (statuses && statuses.length) {
                    test.showByStatus = statuses.includes(test.status.toLowerCase());
                } else {
                    test.showByStatus = true;
                }
            });
        }

        function switchTestGroupMode(mode, force) {
            if (vm.testGroupMode !== mode || force) {
                vm.testGroupMode = mode;

                !force && resetTestsGrouping();

                onTestGroupingMode(function () {
                    if (!force) {
                        vm.testRun.tags = collectTags(vm.testRun.tests);
                        vm.testsTagsOptions.hashSymbolHide = false;
                        vm.testGroups.mode = 'common';
                    }
                    angular.element('.page').removeClass('groups-group-mode');
                }, function () {
                    angular.element('.page').addClass('groups-group-mode');
                    vm.testGroups.mode = 'package';
                    vm.testRun.tags = [
                        {name: 'package', value: 'Package', default: true},
                        {name: 'class', value: 'Class'}
                    ];
                    groupTests(force);

                    if (!force) {
                        vm.testsTagsOptions.initValues = ['Package'];
                        vm.testsTagsOptions.hashSymbolHide = true;
                    }
                });
            }
        }

        function resetTestsGrouping() {
            vm.testsTagsOptions.reset(); //TODO: refactoring: directive shouldn't extend passed object: ("clear functions" approach)
            vm.testsStatusesOptions.reset();
            vm.predicate = 'startTime';
            vm.reverse = false;
            vm.testGroups.predicate = 'startTime';
            vm.testGroups.reverse = false;
            if (vm.testGroupMode === 'GROUPS') {
                vm.testGroups.mode = 'package';
            }
        }

        function onTestGroupingMode(funcPlain, funcGroups) {
            switch(vm.testGroupMode) {
                case 'PLAIN':
                    funcPlain.call();
                    break;
                case 'GROUPS':
                    funcGroups.call();
                    break;
                default:
                    break;
            }
        }

        function groupTests(force) {
            if (!force) {
                initTestGroups();
            } else {
                vm.testGroups.group.package.data = {};
                vm.testGroups.group.class.data = {};
            }

            angular.forEach(vm.testRun.tests, function (value) {
                addGroupingItem(value);
            });
        }

        function selectTestGroup(group, selectName) {
            group.selectedName = group.selectedName === selectName ? undefined : selectName;
        }

        function onTagSelect(chips) {
            var fnPlain = function() {
                showTestsByTags(vm.testRun.tests, chips);
            };
            var fgGroups = function() {
                angular.forEach(vm.testRun.tests, function(test) {
                    test.show = true;
                    test.showByStatus = true;
                });
                if (chips && chips.length) {
                    vm.testGroups.mode = chips[0].toLowerCase();
                }
                vm.testGroups.apply = true;
            };

            onTestGroupingMode(fnPlain, fgGroups);
            testGroupDataToStore.tags = angular.copy(chips);
        }

        function onStatusButtonClick(statuses) {
            var fnPlain = function() {
                showTestsByStatuses(vm.testRun.tests, statuses);
            };
            var fgGroups = function() {
                showTestsByStatuses(vm.testRun.tests, statuses);
            };

            onTestGroupingMode(fnPlain, fgGroups);
            testGroupDataToStore.statuses = angular.copy(statuses);
        }

        function changeTestStatus(test, status) {
            if(test.status !== status && confirm('Do you really want mark test as ' + status + '?')) {
                test.status = status;
                TestService.updateTest(test)
                    .then(function(rs) {
                        if (rs.success) {
                            alertify.success('Test was marked as ' + status);
                        } else {
                            console.error(rs.message);
                        }
                    });
            }
        }

        function showDetailsDialog(test, event) {
            const isNew = setWorkItemIsNewStatus(test.workItems);

            modalsService.openModal({
                controller: 'TestDetailsModalController',
                template: require('../../components/modals/test-details/test-details.html'),
                parent: angular.element(document.body),
                targetEvent: event,
                locals: {
                    test: test,
                    isNewIssue: isNew.issue,
                    isNewTask: isNew.task,
                    isConnectedToJira: $rootScope.tools['JIRA'],
                    isJiraEnabled: $rootScope.jira.enabled
                }
            })
            .catch(function(response) {
                if (response) {
                    vm.testRun.tests[test.id] = angular.copy(response);
                }
            });
        }

        function setWorkItemIsNewStatus(workItems) {
            const isNew = {
                issue: true,
                task: true
            };

            workItems.length && workItems.forEach(function(item) {
                switch (item.type) {
                    case 'TASK':
                        isNew.task = false;
                        break;
                    case 'BUG':
                        isNew.issue = false;
                        break;
                }
            });

            return isNew;
        }

        function initWebsocket() {
            const wsName = 'zafira';

            vm.zafiraWebsocket = Stomp.over(new SockJS(API_URL + '/api/websockets'));
            vm.zafiraWebsocket.debug = null;
            vm.zafiraWebsocket.connect({withCredentials: false}, function () {
                vm.subscriptions.statistics = subscribeStatisticsTopic();
                vm.subscriptions.testRun = subscribeTestRunsTopic();
                UtilService.websocketConnected(wsName);
            }, function () {
                UtilService.reconnectWebsocket(wsName, initWebsocket);
            });
        }

        function getEventFromMessage(message) {
            return JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
        }

        function checkStatisticEvent(event) {
            return (vm.testRun.id !== +event.testRunStatistics.testRunId);
        }

        function subscribeStatisticsTopic() {
            return vm.zafiraWebsocket.subscribe('/topic/' + TENANT + '.statistics', function (data) {
                const event = getEventFromMessage(data.body);

                if (checkStatisticEvent(event)) {
                    return;
                }

                vm.testRun.inProgress = event.testRunStatistics.inProgress;
                vm.testRun.passed = event.testRunStatistics.passed;
                vm.testRun.failed = event.testRunStatistics.failed;
                vm.testRun.failedAsKnown = event.testRunStatistics.failedAsKnown;
                vm.testRun.failedAsBlocker = event.testRunStatistics.failedAsBlocker;
                vm.testRun.skipped = event.testRunStatistics.skipped;
                vm.testRun.reviewed = event.testRunStatistics.reviewed;
                vm.testRun.aborted = event.testRunStatistics.aborted;
                vm.testRun.queued = event.testRunStatistics.queued;
                $scope.$apply();
            });
        }

        function subscribeTestRunsTopic() {
            return vm.zafiraWebsocket.subscribe('/topic/' + TENANT + '.testRuns', function (data) {
                const event = getEventFromMessage(data.body);
                let index = -1;
                const testRun = angular.copy(event.testRun);

                // if (vm.projects && vm.projects.length && vm.projects.indexOfField('id', event.testRun.project.id) === -1) { return; }
                if (vm.testRun.id !== +testRun.id) { return; }

                vm.testRuns[index].status = testRun.status;
                vm.testRuns[index].reviewed = testRun.reviewed;
                vm.testRuns[index].elapsed = testRun.elapsed;
                vm.testRuns[index].platform = testRun.platform;
                vm.testRuns[index].env = testRun.env;
                vm.testRuns[index].comments = testRun.comments;
                vm.testRuns[index].reviewed = testRun.reviewed;
                $scope.$apply();
            });
        }

        function subscribeTestsTopic() {
            if (vm.zafiraWebsocket && vm.zafiraWebsocket.connected) {
                return vm.zafiraWebsocket.subscribe('/topic/' + TENANT + '.testRuns.' + vm.testRun.id + '.tests', function (data) {
                    const event = getEventFromMessage(data.body);

                    addTest(event.test);
                    $scope.$apply();
                });
            }
        }

        function bindEvents() {
            $scope.$on('$destroy', function () {
                if (vm.zafiraWebsocket && vm.zafiraWebsocket.connected) {
                    vm.subscriptions.statistics && vm.subscriptions.statistics.unsubscribe();
                    vm.subscriptions.testRun && vm.subscriptions.testRun.unsubscribe();
                    vm.subscriptions[vm.testRun.id] && vm.subscriptions[vm.testRun.id].unsubscribe();
                    vm.zafiraWebsocket.disconnect();
                    UtilService.websocketConnected('zafira');
                }
            });
        }
    }

})();
