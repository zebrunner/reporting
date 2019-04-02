'use strict';

import ImagesViewerController from '../../components/modals/images-viewer/images-viewer.controller';
import IssuesModalController from '../../components/modals/issues/issues.controller';

const testDetailsController = function testDetailsController($scope, $rootScope, $q, TestService, API_URL,
                                                             modalsService, $state, $transitions,
                                                             UtilService, $mdDialog) {
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
        testRun: null,
        testsLoading: true,
        testsFilteredEmpty: true,
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
        updateTest,
        get empty() {
            return !Object.keys(vm.testRun.tests || {}).length ;
        },
        openImagesViewerModal,
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

    function updateTest(test, isPassed) {
        var newStatus = isPassed ? 'PASSED' : 'FAILED';
        if (test.status !== newStatus) {
            test.status = newStatus;
        }
        else {
            return;
        }
        var message;
        TestService.updateTest(test).then(function(rs) {
            if (rs.success) {
                message = 'Test was marked as ' + test.status;
                addTestEvent(message, test);
                alertify.success(message);
            }
            else {
                console.error(rs.message);
            }
        });
    }

    function  addTestEvent(message, test) {
        var testEvent = {};
        testEvent.description = message;
        testEvent.jiraId = Math.floor(Math.random() * 90000) + 10000;
        testEvent.testCaseId = test.testCaseId;
        testEvent.type = 'EVENT';
        TestService.createTestWorkItem(test.id, testEvent).
            then(function(rs) {
                if (rs.success) {
                } else {
                    alertify.error('Failed to add event test "' + test.id);
                }
            })
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
        })
        .finally(() => {
            vm.testsLoading = false;
        });
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

                vm.testRun.tests = {};
                TestService.setTests = data;
                TestService.getTests.forEach(function(test) {
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
        $state.go('tests.runInfo', {
            testRunId: vm.testRun.id,
            testId: testId
        });
    }

    function addTest(test) {
        test.elapsed = test.finishTime ? (test.finishTime - test.startTime) : Number.MAX_VALUE;
        prepareArtifacts(test);
        angular.forEach(test.tags, function (tag) {
            if (tag.name === 'TESTRAIL_TESTCASE_UUID' || tag.name === 'QTEST_TESTCASE_UUID') {
                tag.value = tag.value.split('-').pop();
            }
        });

        vm.testRun.tests[test.id] = test;

        if (vm.testGroupMode === 'PLAIN') {
            vm.testRun.tags = collectTags(vm.testRun.tests);
        } else {
            addGroupingItem(test);
        }

        onTagSelect(testGroupDataToStore.tags);
        onStatusButtonClick(testGroupDataToStore.statuses);
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
        vm.testsFilteredEmpty = true;
        angular.forEach(tests, function (test) {
            test.showByStatus = false;
            if (statuses && statuses.length) {
                test.showByStatus = statuses.includes(test.status.toLowerCase());
            } else {
                test.showByStatus = true;
            }
            if (test.showByStatus) {
                vm.testsFilteredEmpty = false;
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
        vm.testsFilteredEmpty = false;
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
            controller: IssuesModalController,
            template: require('../../components/modals/issues/issues.html'),
            parent: angular.element(document.body),
            targetEvent: event,
            controllerAs: '$ctrl',
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
            vm.subscriptions[vm.testRun.id] = subscribeTestsTopic(vm.testRun.id);
            UtilService.websocketConnected(wsName);
        }, function () {
            UtilService.reconnectWebsocket(wsName, initWebsocket);
        });
    }

    function getEventFromMessage(message) {
        return JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
    }

    function isCurrentTestRunStatistics(event) {
        return vm.testRun.id === +event.testRunStatistics.testRunId;
    }

    function subscribeStatisticsTopic() {
        return vm.zafiraWebsocket.subscribe('/topic/' + TENANT + '.statistics', function (data) {
            const event = getEventFromMessage(data.body);

            if (!isCurrentTestRunStatistics(event)) {
                return;
            }

            Object.assign(vm.testRun, event.testRunStatistics);
            $scope.$apply();
        });
    }

    function subscribeTestRunsTopic() {
        return vm.zafiraWebsocket.subscribe('/topic/' + TENANT + '.testRuns', function (data) {
            const event = getEventFromMessage(data.body);
            const testRun = angular.copy(event.testRun);

            if (vm.testRun.id !== +testRun.id) { return; }

            vm.testRun.status = testRun.status;
            vm.testRun.reviewed = testRun.reviewed;
            vm.testRun.elapsed = testRun.elapsed;
            vm.testRun.platform = testRun.platform;
            vm.testRun.env = testRun.env;
            vm.testRun.comments = testRun.comments;
            vm.testRun.reviewed = testRun.reviewed;
            $scope.$apply();
        });
    }

    function subscribeTestsTopic() {
        return vm.zafiraWebsocket.subscribe('/topic/' + TENANT + '.testRuns.' + vm.testRun.id + '.tests', function (data) {
            const event = getEventFromMessage(data.body);

            addTest(event.test);
            $scope.$apply();
        });
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

        const onTransStartSubscription = $transitions.onStart({}, function(trans) {
            const toState = trans.to();

            if (toState.name !== 'tests.runInfo') {
                TestService.clearDataCache();
            }

            onTransStartSubscription();
        });
    }

    //TODO: implement lazyLoading after webpack is applied
    function openImagesViewerModal(event, artifact, test) {
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
                test,
                activeArtifactId: artifact.id,
            }
        });
    }
};

export default testDetailsController;
