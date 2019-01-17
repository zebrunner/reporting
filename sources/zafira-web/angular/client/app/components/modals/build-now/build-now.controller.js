(function () {
    'use strict';

    angular.module('app').controller('BuildNowController', [
        '$scope',
        '$mdDialog',
        'TestRunService',
        'testRun',
        BuildNowController]);

    function BuildNowController($scope, $mdDialog, TestRunService, testRun) {
        $scope.title = testRun.testSuite.name;
        $scope.textRequired = false;

        $scope.testRun = testRun;

        $scope.buildNow = function () {
            $scope.hide();
            var jobParametersMap = {};
            for (var i = 0; i < $scope.jobParameters.length; i++){
                jobParametersMap[$scope.jobParameters[i].name] = $scope.jobParameters[i].value;
            }
            TestRunService.buildTestRun($scope.testRun.id, jobParametersMap).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('CI job is building, it may take some time before status is updated');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.jobParameters = [];
        $scope.isJobParametersLoaded = false;
        $scope.noValidJob = false;
        $scope.getJobParameters = function () {
            TestRunService.getJobParameters($scope.testRun.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.jobParameters = rs.data;
                    for (var i = 0; i < $scope.jobParameters.length; i++){
                        if ($scope.jobParameters[i].parameterClass === 'BOOLEAN'){
                            $scope.jobParameters[i].value = JSON.parse($scope.jobParameters[i].value);
                            if ($scope.jobParameters[i].name === "rerun_failures"){
                                $scope.jobParameters[i].value = false;
                            }
                            if ($scope.jobParameters[i].name === "debug"){
                                $scope.jobParameters[i].value = false;
                            }

                        }
                    }
                    $scope.isJobParametersLoaded = true;
                    $scope.noValidJob = $scope.jobParameters == '';
                }
                else
                {
                    $scope.hide();
                    alertify.error(rs.message);
                }
            });
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {
            $scope.getJobParameters();
        })();
    }

})();
