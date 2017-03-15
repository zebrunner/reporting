'use strict';

ZafiraApp.controller('JobViewsCtrl', [ '$scope', '$http','$location', '$route', '$routeParams', '$modal', '$q', 'UtilService', 'ConfigService', 'JenkinsService', function($scope, $http, $location, $route, $routeParams, $modal, $q, UtilService, ConfigService, JenkinsService) {
	
	$scope.view = {};
	$scope.jobs = [];
	$scope.jobViews = {};
	$scope.testRuns = {};
	
	$scope.UtilService = UtilService;
	
	ConfigService.getConfig("jenkins").then(function(jenkins) {
		$scope.jenkinsEnabled = jenkins.enabled;
	});
	
	$scope.loadView = function(){
		$http.get('views/' + $routeParams.id).then(function successCallback(view) {
			$scope.view = view.data;
		}, function errorCallback(data) {
			console.error('Failed to load view');
		});
	};
	
	$scope.loadJobs = function(){
		$http.get('jobs').then(function successCallback(jobs) {
			$scope.jobs = jobs.data;
		}, function errorCallback(data) {
			console.error('Failed to load jobs');
		});
	};
	
	$scope.selectForRerun = function(env, scope)
	{
		for(var i = 0; i < $scope.jobViews[env].length; i++)
		{
			var testRun = $scope.jobViews[env][i].testRun;
			if(testRun != null)
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
		$http.get('jobs/views/' + $routeParams.id).then(function successCallback(jobViews) {
			$scope.jobViews = jobViews.data;
			for (var env in $scope.jobViews) 
			{
				$scope.loadJobTestRuns($routeParams.id, env, $scope.jobViews[env]);
			}
		}, function errorCallback(data) {
			console.error('Failed to load jobs views');
		});
	};
	
	$scope.loadJobTestRuns = function(viewId, env, jobViews){
		return $http.post('jobs/views/' + viewId + '/tests/runs?env=' + env, jobViews).then(function successCallback(testRuns) {
			for(var i = 0; i < $scope.jobViews[env].length; i++)
			{
				var testRun = testRuns.data[jobViews[i].job.id];
				if(testRun != null)
				{
					testRun.rebuild = false;
					jobViews[i].testRun = testRun;
					$scope.testRuns[testRun.id] = testRun;
				}
			}
		}, function errorCallback(data) {
			console.error('Failed to load job test runs');
		});
	};
	
	$scope.openJobsViewModal = function(jobView){
		$modal.open({
			templateUrl : 'resources/templates/jobs-view-details-modal.jsp',
			resolve : {
				'viewId' : function(){
					return parseInt($routeParams.id);
				},
				'jobs' : function(){
					return $scope.jobs;
				},
				'existingJobView' : function(){
					return jobView;
				}
			},
			controller : function($scope, $modalInstance, viewId, jobs, existingJobView){
				
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
					$http.post('jobs/views', $scope.jobViews).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
						alertify.success('Job view created successfully');
					}, function errorCallback(data) {
						alertify.error('Failed to create job view');
					});
				};
				
				$scope.updateJobView = function(env){
					var jobsViews = [];
					for(var i = 0; i < $scope.jobsSelected.length; i++)
					{
						var jobView = {'viewId': viewId, 'job': {'id': $scope.jobsSelected[i]}, 'env': $scope.jobView.env, 'position': $scope.jobView.position, 'size': $scope.jobView.size};
						$scope.jobViews.push(jobView);
					}
					$http.put('jobs/views/' + viewId + "?env=" + env, $scope.jobViews).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
						alertify.success('Job view updated successfully');
					}, function errorCallback(data) {
						alertify.error('Failed to update job view');
					});
				};
				
				$scope.deleteJobView = function(env){
					$http.delete('jobs/views/' + $routeParams.id + "?env=" + env).then(function successCallback() {
						$modalInstance.close(0);
						$route.reload();
						alertify.success('Job view deleted successfully');
					}, function errorCallback(data) {
						alertify.error('Failed to delete job view');
					});
				};
				
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		}).result.then(function(data) {
        }, function () {
        });
	};
	
	$scope.rebuildJobs = function(id) {
		var rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
		if(id != null)
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
		if(testRun != null && testRun.rebuild)
		{
			testRun.rebuild = false;
			JenkinsService.rebuildTestRun(testRun.id, rerunFailures).then(function(rs){
				if(rs.status == 200)
				{
					testRun.status = 'IN_PROGRESS';
				}
			});
		}
	};
	
	// --------------------  Context menu ------------------------
	const OPEN_TEST_RUN = ['Open', function ($itemScope) {
		var testRun = $itemScope.jobView.testRun;
        window.open($location.$$absUrl.split("views")[0] + "tests/runs?id=" + testRun.id, '_blank');
    }];
	
	const REBUILD = ['Rebuild', function ($itemScope) {
		var job = $itemScope.jobView.job;
		var testRun = $itemScope.jobView.testRun;
		if($scope.jenkinsEnabled)
		{
			$scope.rebuildJobs(testRun.id);
		}
		else
		{
			window.open($itemScope.jobView.job.jobURL + "/" + testRun.buildNumber + '/rebuild/parameterized', '_blank');
		}
    }];
	
	const COPY_TEST_RUN_LINK = ['Copy link', function ($itemScope) {
		var testRun = $itemScope.jobView.testRun;
	  	var node = document.createElement('pre');
  	    node.textContent = $location.$$absUrl.split("views")[0] + "tests/runs?id=" + testRun.id;
  	    document.body.appendChild(node);
  	    
  	    var selection = getSelection();
  	    selection.removeAllRanges();

  	    var range = document.createRange();
  	    range.selectNodeContents(node);
  	    selection.addRange(range);

  	    document.execCommand('copy');
  	    selection.removeAllRanges();
  	    document.body.removeChild(node);
	}];
	
	$scope.userMenuOptions = [
	      OPEN_TEST_RUN,
	      COPY_TEST_RUN_LINK,
	      null,
	      REBUILD
    ];
	
	(function init(){
		$scope.loadJobs();
		$scope.loadView();
		$scope.loadJobViews();
	})();
} ]);
