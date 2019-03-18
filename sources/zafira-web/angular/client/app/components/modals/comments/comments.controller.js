(function () {
    'use strict';

    angular.module('app').controller('CommentsController', CommentsController);

    function CommentsController($scope, $mdDialog, TestRunService, SlackService, testRun, isSlackAvailable, slackChannels) {
        'ngInject';

        $scope.title = testRun.testSuite.name;
        $scope.testRun = angular.copy(testRun);

        $scope.markReviewed = function(){
            var rq = {};

            rq.comment = $scope.testRun.comments;
            if ((rq.comment == null || rq.comment == "") && ((testRun.failed > 0 && testRun.failed > testRun.failedAsKnown) || testRun.skipped > 0)) {
                alertify.error('Unable to mark as Reviewed test run with failed/skipped tests without leaving some comment!');
            } else {
                TestRunService.markTestRunAsReviewed($scope.testRun.id, rq).then(function(rs) {
                    if(rs.success) {
                        $scope.testRun.reviewed = true;
                        $scope.hide($scope.testRun);
                        alertify.success('Test run #' + $scope.testRun.id + ' marked as reviewed');
                        if (isSlackAvailable && slackChannels.indexOf(testRun.job.name) !== -1) {
                            if (confirm("Would you like to post latest test run status to slack?")) {
                                SlackService.triggerReviewNotif($scope.testRun.id);
                            }
                        }
                    } else {
                        alertify.error(rs.message);
                    }
                });
            }
        };
        $scope.hide = function(testRun) {
            $mdDialog.hide(testRun);
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }

})();
