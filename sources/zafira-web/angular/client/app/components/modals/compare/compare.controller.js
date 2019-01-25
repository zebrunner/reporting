(function () {
    'use strict';

    angular.module('app').controller('CompareController', [
        '$scope',
        '$mdDialog',
        '$q',
        '$location',
        'TestService',
        'selectedTestRuns',
        CompareController]);

    function CompareController($scope, $mdDialog, $q, $location, TestService, selectedTestRuns) {
        $scope.hideIdentical = false;
        $scope.allTestsIdentical = true;
        $scope.tr = {};
        angular.copy(selectedTestRuns, $scope.tr);

        var COMPARE_FIELDS = ['status', 'message'];
        var EXIST_FIELDS = {'name': '', 'testGroup': '', 'testClass': ''};

        function aggregateTests(testRuns) {
            return angular.forEach(collectUniqueTests(testRuns), function (test) {
                test.identical = areTestsIdentical(test.referrers, testRuns);
            });
        };

        function collectUniqueTests(testRuns) {
            var uniqueTests = {};
            angular.forEach(testRuns, function(testRun) {
                angular.forEach(testRun.tests, function(test) {
                    var uniqueTestKey = EXIST_FIELDS;
                    uniqueTestKey.name = test.name;
                    uniqueTestKey.testGroup = test.testGroup;
                    uniqueTestKey.testClass = test.testClass;
                    var stringKey = JSON.stringify(uniqueTestKey);
                    if(! uniqueTests[stringKey]) {
                        uniqueTests[stringKey] = test;
                        uniqueTests[stringKey].referrers = {};
                    }
                    if(!uniqueTests[stringKey].referrers[testRun.id]) {
                        uniqueTests[stringKey].referrers[testRun.id] = {};
                    }
                    uniqueTests[stringKey].referrers[testRun.id] = test.id;
                })
            });
            return uniqueTests;
        };

        function areTestsIdentical(referrers, testRuns) {
            var value = {};
            var result = {};
            var identicalCount = 'count';
            result[identicalCount] = Object.size(referrers) == Object.size(testRuns);
            for(var testRunId in referrers) {
                var test = testRuns[testRunId].tests[referrers[testRunId]];
                if(Object.size(value) == 0) {
                    for(var index = 0; index < COMPARE_FIELDS.length; index++) {
                        var field = COMPARE_FIELDS[index];
                        value[field] = test[field];
                        result[field] = true;
                    }
                    result.isIdentical = true;
                    continue;
                }
                for(var index = 0; index < COMPARE_FIELDS.length; index++) {
                    var field = COMPARE_FIELDS[index];
                    result[field] = verifyValueWithRegex(field, test[field], value[field]);
                    if(result[field] == false) {
                        result.isIdentical = false;
                        $scope.allTestsIdentical = false;
                    }
                }
            }
            if(! result[identicalCount]) {
                $scope.allTestsIdentical = false;
            }
            return result;
        };

        function verifyValueWithRegex(field, value1, value2) {
            var val1 = field == 'message' && value1 ? value1
                .replace(new RegExp("\\d+","gm"), '*')
                .replace(new RegExp("\\[.*\\]","gm"), '*')
                .replace(new RegExp("\\{.*\\}","gm"), '*')
                .replace(new RegExp(".*\\b(Session ID)\\b.*","gm"), '*')
                : value1;
            var val2 = field == 'message' && value2 ? value2
                .replace(new RegExp("\\d+", "gm"), '*')
                .replace(new RegExp("\\[.*\\]", "gm"), '*')
                .replace(new RegExp("\\{.*\\}", "gm"), '*')
                .replace(new RegExp(".*\\b(Session ID)\\b.*", "gm"), '*')
                : value2;
            return ! isEmpty(value1) && ! isEmpty(value2) ? value1 == value2 : true;
        };

        function isEmpty(value) {
            return ! value || ! value.length;
        };

        $scope.getSize = function (obj) {
            return Object.size(obj);
        };

        $scope.getTest = function (testUnique, testRun) {
            var testId = testUnique.referrers[testRun.id];
            return testRun.tests[testId];
        };

        $scope.initTestRuns = function () {
            return $q(function(resolve, reject) {
                var index = 0;
                var testRunsSize = Object.size($scope.tr);
                angular.forEach($scope.tr, function (testRun, testRunId) {
                    loadTests(testRunId).then(function (sr) {
                        $scope.tr[testRunId].tests = {};
                        sr.results.forEach(function(test) {
                            $scope.tr[testRunId].tests[test.id] = test;
                        });
                        index++;
                        if(index == testRunsSize) {
                            resolve($scope.tr);
                        }
                    });
                });
            })
        };

        function loadTests(testRunId) {
            return $q(function(resolve, reject) {
                var testSearchCriteria = {
                    'page': 1,
                    'pageSize': 100000,
                    'testRunId': testRunId
                };
                TestService.searchTests(testSearchCriteria).then(function (rs) {
                    if (rs.success) {
                        resolve(angular.copy(rs.data));
                    }
                    else {
                        reject(rs.message);
                        console.error(rs.message);
                    }
                });
            })
        };

        $scope.openTestRun = function (testRunId) {
            if ($location.$$path != $location.$$url){
                $location.search({});
            }
            if ($location.$$absUrl.match(new RegExp(testRunId, 'gi')) == null){
                window.open($location.$$absUrl + "/" + testRunId, '_blank');
            }
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        (function initController() {
            $scope.loading = true;
            $scope.initTestRuns().then(function (testRuns) {
                $scope.loading = false;
                $scope.uniqueTests = aggregateTests(testRuns);
            });
        })();
    }

})();
