(function () {
    'use strict';

    angular
        .module('app.user')
        .controller('CommentsController', ['$scope', '$location', 'UserService', 'UtilService', CommentsController])

    // **************************************************************************
    function CommentsController($scope, $mdDialog, TestRunService, testRun) {
        $scope.title = testRun.testSuite.name;
        $scope.testRun = testRun;

        $scope.markReviewed = function(){
            var rq = {};
            rq.comment = $scope.testRun.comments;
            if((rq.comment == null || rq.comment == "") && ((testRun.failed > 0 && testRun.failed > testRun.failedAsKnown) || testRun.skipped > 0))
            {
                alertify.error('Unable to mark as Reviewed test run with failed/skipped tests without leaving some comment!');
            }
            else
            {
                TestRunService.markTestRunAsReviewed($scope.testRun.id, rq).then(function(rs) {
                    if(rs.success)
                    {
                        $scope.hide();
                        alertify.success('Test run #' + $scope.testRun.id + ' marked as reviewed');
                        if ($scope.testRun.isSlackAvailable)
                        {
                            if(confirm("Would you like to post latest test run status to slack?"))
                            {
                                SlackService.triggerReviewNotif($scope.testRun.id);
                            }
                        }
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {

        })();
    }
})();
