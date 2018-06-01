(function () {
    'use strict';

    angular
        .module('app.testruninfo')
        .controller('TestRunInfoController', ['$scope', '$log', '$q', 'TestService', 'TestRunService', '$stateParams', TestRunInfoController])

    // **************************************************************************
    function TestRunInfoController($scope, $log, $q, TestService, TestRunService, $stateParams) {

        $scope.testRun = {};
        $scope.test = {};

        $scope.tabs = [
            { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."},
            { title: 'Screenshots', content: "You can swipe left and right on a mobile device to change tabs."},
            { title: 'Raw logs', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
        ];

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

        (function init() {
            getTestRun($stateParams.id).then(function (rs) {
                getTest(rs.id).then(function (testsRs) {
                    $scope.testRun = rs;
                    $scope.test = testsRs.filter(function (t) {
                        return t.id === parseInt($stateParams.testId);
                    })[0];
                    $scope.testRun.tests = testsRs;
                });
            });
        })();
    }

})();
