(function () {
    'use strict';

    angular
        .module('app.testrun')
        .controller('TestRunListController', ['$scope', '$location', 'TestService', 'TestRunService', 'TestService', 'UtilService', TestRunListController])

    // **************************************************************************
    function TestRunListController($scope, $location, TestService, TestRunService, UtilService) {

        var DEFAULT_SC = {page : 1, pageSize : 20};

        $scope.UtilService = UtilService;

        $scope.sc = angular.copy(DEFAULT_SC);
        $scope.tests = {};

        var OFFSET = new Date().getTimezoneOffset() * 60 * 1000;

        $scope.predicate = 'startTime';
        $scope.reverse = false;

        $scope.UtilService = UtilService;
        $scope.testRunId = $location.search().id;

        $scope.testRunsToCompare = [];
        $scope.compareQueryString = "";

        $scope.testRuns = {};
        $scope.totalResults = 0;

        $scope.showRealTimeEvents = false;

        $scope.showReset = $scope.testRunId != null;

        $scope.testRunSearchCriteria = {
            'page': 1,
            'pageSize': 20
        };

        $scope.testSearchCriteria = {
            'page': 1,
            'pageSize': 100000
        };

        $scope.search = function (page, pageSize) {

            $scope.testRunSearchCriteria.page = page;

            if (pageSize) {
                $scope.testRunSearchCriteria.pageSize = pageSize;
            }

            if ($scope.testRunId) {
                $scope.testRunSearchCriteria.id = $scope.testRunId;
            }
            else {
                //$scope.testRunSearchCriteria = ProjectProvider.initProject($scope.testRunSearchCriteria);
            }

            if ($scope.startedAt) {
                $scope.testRunSearchCriteria.date = new Date(Date.parse($scope.startedAt) + OFFSET);
            }

            TestRunService.searchTestRuns($scope.sc).then(function(rs) {
                if(rs.success)
                {
                    $scope.sr = rs.data;

                    $scope.testRuns = {};

                    $scope.testRunSearchCriteria.page = rs.data.page;
                    $scope.testRunSearchCriteria.pageSize = rs.data.pageSize;
                    $scope.totalResults = rs.data.totalResults;

                    for (var i = 0; i < rs.data.results.length; i++) {
                        $scope.addTestRun(rs.data.results[i]);
                    }

                    if ($scope.testRunId) {
                        $scope.loadTests($scope.testRunId);
                    }
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.loadTests = function (testRunId) {
            $scope.lastTestRunOpened = testRunId;
            $scope.testSearchCriteria.testRunId = testRunId;
            TestService.searchTests($scope.testSearchCriteria).then(function(rs) {
                if(rs.success)
                {
                    $scope.tests[testRunId] = rs.data.results;

                    $scope.userSearchResult = rs.data;
                    $scope.testSearchCriteria.page = rs.data.page;
                    $scope.testSearchCriteria.pageSize = rs.data.pageSize;

                    for (var i = 0; i < rs.data.results.length; i++) {
                        $scope.addTest(rs.data.results[i], false);
                    }
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.loadEnvironments = function () {
            TestRunService.getEnvironments().then(function(rs) {
                if(rs.success)
                {
                    $scope.environments = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.switchTestRunExpand = function (testRun) {
            testRun.expand == null ? testRun.expand = true : testRun.expand = !testRun.expand;
            if(testRun.expand) {
                $scope.loadTests(testRun.id);
            }
        };

        $scope.addTestRun = function (testRun) {
            testRun.showDetails = $scope.testRunId ? true : false;
            if ($scope.testRuns[testRun.id] == null) {
                testRun.jenkinsURL = testRun.job.jobURL + "/" + testRun.buildNumber;
                testRun.UID = testRun.testSuite.name + " " + testRun.jenkinsURL;
                testRun.tests = {};
                $scope.testRuns[testRun.id] = testRun;
            }
            else {
                $scope.testRuns[testRun.id].status = testRun.status;
                $scope.testRuns[testRun.id].reviewed = testRun.reviewed;
            }
            /*ConfigService.getConfig("slack/" + testRun.id).then(function successCallback(rs){
                $scope.testRuns[testRun.id].isSlackAvailable = rs.available;
            });*/
        };

        $scope.addTest = function (test, isEvent) {

            test.elapsed = test.finishTime != null ? (test.finishTime - test.startTime) : Number.MAX_VALUE;

            var testRun = $scope.testRuns[test.testRunId];
            if (testRun == null) {
                return;
            }

            if (isEvent) {
                if (testRun.tests[test.id] != null) {
                    $scope.updateTestRunResults(testRun, testRun.tests[test.id], -1);
                }
                testRun.tests[test.id] = test;
                $scope.updateTestRunResults(testRun, test, 1);
            }
            else {
                testRun.tests[test.id] = test;
            }
        };

        $scope.updateTestRunResults = function (testRun, test, changeByAmount) {
            switch (test.status) {
                case "PASSED":
                    testRun.passed = testRun.passed + changeByAmount;
                    break;
                case "FAILED":
                    testRun.failed = testRun.failed + changeByAmount;
                    if (test.knownIssue) {
                        testRun.failedAsKnown = testRun.failedAsKnown + changeByAmount;
                    }
                    if (test.blocker) {
                        testRun.failedAsBlocker = testRun.failedAsBlocker + changeByAmount;
                        testRun.blocker = true;
                    }
                    break;
                case "SKIPPED":
                    testRun.skipped = testRun.skipped + changeByAmount;
                    break;
                default:
                    break;
            }
        };

        $scope.reset = function () {
            $scope.sc = angular.copy(DEFAULT_SC);
            $scope.search();
        };

        (function initController() {
            $scope.search(1);
            $scope.loadEnvironments();
        })();
    }
})();
