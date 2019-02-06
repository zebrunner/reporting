(function () {
    'use strict';

    angular
        .module('app.user')
        .controller('ViewController', ViewController);

    // **************************************************************************
    function ViewController($scope, $location, $state, $mdDialog, $stateParams, UtilService, ConfigService,
                            TestRunService, JobService, ViewService, TestService, API_URL) {
        'ngInject';

        $scope.view = {};
        $scope.jobs = [];
        $scope.jobViews = {};
        $scope.testRuns = {};

        $scope.UtilService = UtilService;

        /*$scope.testSearchCriteria = {
            'page': 1,
            'pageSize': 100000
        };*/

        $scope.initStatisticWebsocket = function () {
            var sockJS = new SockJS(API_URL + "/websockets");
            $scope.statisticsStomp = Stomp.over(sockJS);
            //statisticsStomp.debug = null;
            $scope.statisticsStomp.connect({withCredentials: false}, function () {
                $scope.statisticsStomp.subscribe("/topic/tests", function (data) {
                    $scope.getMessage(data.body);
                });
            });
        };

        $scope.disconnectStatisticWebsocket = function () {
            if ($scope.statisticsStomp != null) {
                $scope.statisticsStomp.disconnect();
            }
        };

        $scope.$on('$destroy', function () {
            $scope.disconnectStatisticWebsocket();
        });

        $scope.getMessage = function (message) {
            var event = JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
            if (event.type == 'TEST') {
                $scope.addTest(event.test, true);
                $scope.$apply();
            }
            return true;
        };

        $scope.addTest = function (test, isEvent) {

            test.elapsed = test.finishTime != null ? (test.finishTime - test.startTime) : Number.MAX_VALUE;

            /*for (var env in $scope.jobViews) {
                $scope.jobViews[env].filter(function (view) {
                    return view.testRun.id == test.testRun.id;
                })[0].testRun;
            }*/

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
                    }
                    testRun.blocker = test.blocker;
                    break;
                case "SKIPPED":
                    testRun.skipped = testRun.skipped + changeByAmount;
                    break;
                case "IN_PROGRESS":
                    testRun.inProgress = testRun.inProgress + changeByAmount;
                    break;
                default:
                    break;
            }
        };

        ConfigService.getConfig("jenkins").then(function(rs) {
            $scope.jenkinsEnabled = rs.data.connected;
        });

        $scope.loadView = function(){
            ViewService.getViewById($stateParams.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.view = rs.data;
                }
                else
                {
                    console.error('Failed to load view');
                }
            });
        };

        $scope.loadJobs = function(){
            JobService.getAllJobs().then(function(rs) {
                if(rs.success)
                {
                    $scope.jobs = rs.data;
                }
                else
                {
                    console.error('Failed to load jobs');
                }
            });
        };

        $scope.selectForRerun = function(env, scope)
        {
            for(var i = 0; i < $scope.jobViews[env].length; i++)
            {
                var testRun = $scope.jobViews[env][i].testRun;
                if(testRun)
                {
                    switch(scope)
                    {
                        case "All":
                            testRun.rebuild = true;
                            break;
                        case "Failed":
                            testRun.rebuild = "FAILED" == testRun.status ? true : false;
                            break;
                        case "None":
                            testRun.rebuild = false;
                            break;
                    }
                }
            }
        };

        $scope.loadJobViews = function(){
            JobService.getJobViews($stateParams.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.jobViews = rs.data;
                    for (var env in $scope.jobViews)
                    {
                        $scope.loadJobTestRuns($stateParams.id, env, $scope.jobViews[env]);
                    }
                }
                else
                {
                    console.error('Failed to load jobs views');
                }
            });
        };

        $scope.loadJobTestRuns = function(viewId, env, jobViews){
            return JobService.getLatestJobTestRuns(viewId, jobViews, env).then(function(rs) {
                if(rs.success)
                {
                    for(var i = 0; i < $scope.jobViews[env].length; i++)
                    {
                        var testRun = rs.data[jobViews[i].job.id];
                        if(testRun)
                        {
                            testRun.rebuild = false;
                            testRun.tests = {};
                            if(testRun.status == 'IN_PROGRESS') {
                                $scope.loadTests(testRun);
                            }
                            jobViews[i].testRun = testRun;
                            $scope.testRuns[testRun.id] = testRun;
                        }
                    }
                }
                else
                {
                    console.error('Failed to load job test runs');
                }
            });
        };

        $scope.loadTests = function (testRun) {
            //$scope.testSearchCriteria.testRunId = testRun.id;
            TestService.searchTests({'page':1, 'pageSize':100000, 'testRunId':testRun.id}).then(function(rs) {
                if(rs.success)
                {
                    var data = rs.data;
                    var inProgressTests = 0;
                    for (var i = 0; i < data.results.length; i++) {
                        var test = data.results[i];
                        if (test.status == 'IN_PROGRESS') {
                            inProgressTests++;
                        }
                        $scope.addTest(test, false);
                    }
                    testRun.inProgress = inProgressTests;
                }
                else
                {
                    console.error(rs.message);
                }
            });
        };

        $scope.showJobsViewDialog = function(event, jobView) {
            $mdDialog.show({
                controller: JobsViewController,
                template: require('./jobs_view_modal.html'),
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    'viewId': parseInt($stateParams.id),
                    'jobs': $scope.jobs,
                    'existingJobView': jobView
                }
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.rebuildJobs = function(id) {
            var rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
            if(id)
            {
                var testRun = $scope.testRuns[id];
                testRun.rebuild = true;
                $scope.rebuildTestRun(testRun, rerunFailures);
            }
            else
            {
                for (var env in $scope.jobViews)
                {
                    for(var i = 0; i < $scope.jobViews[env].length; i++)
                    {
                        var testRun = $scope.jobViews[env][i].testRun;
                        $scope.rebuildTestRun(testRun, rerunFailures);
                    }
                }
            }
        };

        $scope.rebuildTestRun = function(testRun, rerunFailures)
        {
            if(testRun && testRun.rebuild)
            {
                testRun.rebuild = false;
                TestRunService.rerunTestRun(testRun.id, rerunFailures).then(function(rs){
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
        };

        // --------------------  Context menu ------------------------

        $scope.openTestRun = function (testRun) {
        	window.open($location.$$absUrl.split("views")[0] + "tests/runs/" + testRun.id, '_blank');
        };

        $scope.copyLink = function (testRun) {
            var node = document.createElement('pre');
            node.textContent = $location.$$absUrl.split("views")[0] + "tests/runs/" + testRun.id;
            document.body.appendChild(node);

            var selection = getSelection();
            selection.removeAllRanges();

            var range = document.createRange();
            range.selectNodeContents(node);
            selection.addRange(range);

            document.execCommand('copy');
            selection.removeAllRanges();
            document.body.removeChild(node);
        };

        $scope.rebuild = function (job, testRun) {
    		if($scope.jenkinsEnabled)
    		{
    			$scope.rebuildJobs(job.testRun.id);
    		}
    		else
    		{
    			window.open(job.jobURL + "/" + testRun.buildNumber + '/rebuild/parameterized', '_blank');
    		}
        };

        // ---------------------------------------------------------------


        (function initController() {
            $scope.initStatisticWebsocket();
            $scope.loadJobs();
            $scope.loadView();
            $scope.loadJobViews();
        })();
    }

    // **************************************************************************
    function JobsViewController($scope, $mdDialog, $state, $stateParams, JobService, viewId, jobs, existingJobView) {
        'ngInject';

        $scope.edit = existingJobView != null;

        $scope.jobs = jobs;
        $scope.jobView = {};
        $scope.jobViews = [];
        $scope.jobsSelected = [];

        $scope.selectJob = function(id, isChecked) {
            if(isChecked) {
                $scope.jobsSelected.push(id);
            } else {
                var idx = $scope.jobsSelected.indexOf(id);
                if(idx > -1){
                    $scope.jobsSelected.splice(idx, 1);
                }
            }
        };

        if($scope.edit)
        {
            $scope.jobView.position = existingJobView[0].position;
            $scope.jobView.size = existingJobView[0].size;
            $scope.jobView.env = existingJobView[0].env;
            for(var i = 0; i < existingJobView.length; i++)
            {
                $scope.selectJob(existingJobView[i].job.id, true);
            }
        };

        $scope.createJobView = function(){
            var jobsViews = [];
            for(var i = 0; i < $scope.jobsSelected.length; i++)
            {
                var jobView = {'viewId': viewId, 'job': {'id': $scope.jobsSelected[i]}, 'env': $scope.jobView.env, 'position': $scope.jobView.position, 'size': $scope.jobView.size};
                $scope.jobViews.push(jobView);
            }
            JobService.createJobView($scope.jobViews).then(function(rs) {
                if(rs.success)
                {
                    $scope.hide();
                    $state.reload();
                    alertify.success('Job view created successfully');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.updateJobView = function(env){
            var jobsViews = [];
            for(var i = 0; i < $scope.jobsSelected.length; i++)
            {
                var jobView = {'viewId': viewId, 'job': {'id': $scope.jobsSelected[i]}, 'env': $scope.jobView.env, 'position': $scope.jobView.position, 'size': $scope.jobView.size};
                $scope.jobViews.push(jobView);
            }
            JobService.updateJobViews(viewId, $scope.jobViews, env).then(function(rs) {
                if(rs.success)
                {
                    $scope.hide();
                    $state.reload();
                    alertify.success('Job view updated successfully');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.deleteJobView = function(env){
            JobService.deleteJobViews($stateParams.id, env).then(function(rs) {
                if(rs.success)
                {
                    $scope.hide();
                    $state.reload();
                    alertify.success('Job view deleted successfully');
                }
                else
                {
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
        })();
    }
})();
