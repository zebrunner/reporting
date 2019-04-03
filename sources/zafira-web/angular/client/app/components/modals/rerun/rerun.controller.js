(function () {
    'use strict';

    angular.module('app').controller('TestRunRerunController', TestRunRerunController);

    function TestRunRerunController($scope, $mdDialog, TestRunService, testRun, toolsService) {
        'ngInject';

        $scope.rerunFailures = true;
        $scope.testRun = testRun;

        $scope.rebuild = function (testRun, rerunFailures) {
            if (toolsService.jenkins.enabled) {
                TestRunService.rerunTestRun(testRun.id, rerunFailures).then(function(rs) {
                    if(rs.success)
                    {
                        testRun.status = 'IN_PROGRESS';
                        alertify.success("Rebuild triggered in CI service");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
            else {
                window.open(testRun.jenkinsURL + '/rebuild/parameterized', '_blank');
            }
            $scope.hide(true);
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }

})();
