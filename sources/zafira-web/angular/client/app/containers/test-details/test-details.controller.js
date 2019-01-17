(function () {
    'use strict';

    angular
    .module('app.testDetails')
    .controller('TestDetailsController', [
        'testRun',
        '$scope',
        '$rootScope',
        '$mdToast',
        '$mdMenu',
        '$location',
        '$window',
        '$cookieStore',
        '$mdDialog',
        '$mdConstant',
        '$interval',
        '$timeout',
        '$stateParams',
        '$mdDateRangePicker',
        '$q',
        'FilterService',
        'ProjectService',
        'TestService',
        'TestRunService',
        'UtilService',
        'UserService',
        'SettingsService',
        'ProjectProvider',
        'ConfigService',
        'SlackService',
        'DownloadService',
        'API_URL',
        'DEFAULT_SC',
        'OFFSET',
        'TestRunsStorage',
        '$tableExpandUtil',
        'modalsService',
        TestDetailsController]);

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
                                   $tableExpandUtil, modalsService) {
        var testGroupDataToStore = {
            statuses: [],
            tags: []
        };
        var vm = {
            currentMode: 'ONE',
            reverse: false,
            predicate: 'startTime',
            sc: $location.search(), //TODO: change name
            tags: [],
            testGroups: null,
            testGroupMode: 'PLAIN',
            testRun: testRun,
            // mobileBreakpoint: mediaBreakpoints.mobile || 0,
            // windowWidthService: windowWidthService,
            selectedRange: {
                selectedTemplate: null,
                selectedTemplateName: null,
                dateStart: null,
                dateEnd: null,
                showTemplate: false,
                fullscreen: false
            },
            testsTagsOptions: {},
            testsStatusesOptions: {},

            onStatusButtonClick: onStatusButtonClick,
            onTagSelect: onTagSelect,
            resetTestsGrouping: resetTestsGrouping,
            selectTestGroup: selectTestGroup,
            switchTestGroupMode: switchTestGroupMode,
            changeTestStatus: changeTestStatus,
            showDetailsDialog: showDetailsDialog,
        };

        vm.$onInit = controlInit;

        return vm;

        function controlInit() {
            initTestGroups();
            //TODO: Do we need init Websocket on this page?
            initTests();
            fillTestRunMetadata();
            // $scope.populateSearchQuery();
            // var loadFilterDataPromises = [];
            // loadFilterDataPromises.push($scope.loadEnvironments());
            // loadFilterDataPromises.push($scope.loadPlatforms());
            // loadFilterDataPromises.push(loadProjects());
            // $q.all(loadFilterDataPromises).then(function(values) {
            //     loadSubjectBuilder();
            // });
            // $scope.loadSlackMappings();
            // $scope.storeSlackAvailability();
            // loadPublicFilters();
            // $scope.$broadcast('controller-inited', 'TestRunListController'); //TODO: Check if need
        }

        function fillTestRunMetadata() {
            addBrowserVersion();
            addTestRun();
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

        function addTestRun() { //TODO: refactor name
            if (vm.testRun.job && vm.testRun.job.jobURL) {
                vm.testRun.jenkinsURL = vm.testRun.job.jobURL + '/' + vm.testRun.buildNumber;
                vm.testRun.UID = vm.testRun.testSuite.name + ' ' + vm.testRun.jenkinsURL;
            }

            vm.testRun.tests = null;
        }

        function initTests() {
            vm.testGroups.mode = 'common';

            loadTests(vm.testRun.id)
                .then(function () {
                    vm.testGroups.group.common.data.all = vm.testRun.tests; //TODO: m? > it is empty at this moment
                    showTestsByTags(vm.testRun.tests);
                    showTestsByStatuses(vm.testRun.tests);
                    vm.testRun.tags = collectTags(vm.testRun.tests);
                });
            // $tableExpandUtil.expand('testRun_' + testRun.id, quick).then(function () {
                vm.testRun.expand = true;
                // vm.expandedTestRuns.push(vm.testRun.id);
                // $scope.subscribtions[testRun.id] = $scope.subscribeTestsTopic(testRun.id);
                // $scope.tr = testRun;
            // });
        }

        function loadTests(testRunId) {
            var defer = $q.defer();

            // $scope.lastTestRunOpened = testRunId;
            var params = {
                'page': 1,
                'pageSize': 100000, //TODO: return back (10000)
                // 'pageSize': 100,
                'testRunId': testRunId
            };

            TestService.searchTests(params) //TODO: in testRunId id undefined will be returned 10000 test
                .then(function (rs) {
                    if (rs.success) {
                        var data = rs.data.results || [];

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

        function addTest(test) {
            test.elapsed = test.finishTime ? (test.finishTime - test.startTime) : Number.MAX_VALUE;

            test.artifactsToShow = test.artifacts.filter(function (artifact) {
                var name = artifact.name.toLowerCase();

                return !name.includes('live') && !name.includes('video');
            });

            vm.testRun.tests = vm.testRun.tests || {};
            vm.testRun.tests[test.id] = test;

            if (vm.testGroupMode === 'PLAIN') {
                vm.testRun.tags = collectTags(vm.testRun.tests);
            } else {
                addGroupingItem(test);
            }

            // if ($scope.tr) { //TODO: check the meaning
            //     $scope.onTagSelect(testGroupDataToStore.tags);
            //     $scope.onStatusButtonClick(testGroupDataToStore.statuses);
            // }
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
                        {name: 'package', 'value': 'Package', 'default': true},
                        {name: 'class', 'value': 'Class'}
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

            $scope.testGroupMode = 'PLAIN';
            initTestGroups();
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
            testGroupDataToStore.tags = angular.copy(chips); //TODO: do we need this?
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
                templateUrl: 'app/components/modals/test-details/test-details.html',
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
    }

})();
