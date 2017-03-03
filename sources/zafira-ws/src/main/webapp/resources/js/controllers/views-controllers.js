'use strict';

ZafiraApp.controller('JobViewsCtrl', [ '$scope', '$http','$location', '$route', '$routeParams', '$modal', 'UtilService', 'ConfigService', function($scope, $http, $location, $route, $routeParams, $modal, UtilService, ConfigService) {
	
	$scope.view = {};
	$scope.jobViews = {};
	$scope.jobs = [];
	$scope.jenkinsEnabled = false;
	$scope.jobsSelected = [];
	
	$scope.loadView = function(){
		$http.get('views/' + $routeParams.id).then(function successCallback(view) {
			$scope.view = view.data;
		}, function errorCallback(data) {
			console.error('Failed to load view');
		});
	};
	
	$scope.loadJobViews = function(){
		$http.get('jobs/views/' + $routeParams.id).then(function successCallback(jobViews) {
			$scope.jobViews = jobViews.data;
			console.log($scope.jobViews);
		}, function errorCallback(data) {
			console.error('Failed to load jobs views');
		});
	};
	
	$scope.loadJobs = function(){
		$http.get('jobs').then(function successCallback(jobs) {
			$scope.jobs = jobs.data;
		}, function errorCallback(data) {
			console.error('Failed to load jobs');
		});
	};
	
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
					$scope.jobView.position = existingJobView.jobViews[0].position;
					$scope.jobView.size = existingJobView.jobViews[0].size;
					$scope.jobView.env = existingJobView.jobViews[0].env;
					for(var i = 0; i < existingJobView.jobViews.length; i++)
					{
						$scope.selectJob(existingJobView.jobViews[i].job.id, true);
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
	
	$scope.rebuildJobs = function(testRunIds) {		 
		var rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
		for(var i = 0; i < testRunIds.length; i++)
		{
			$http.get('tests/runs/' + testRunIds[i] + '/rerun?rerunFailures=' + rerunFailures).then(function successCallback(data) {
				alertify.success('CI job is rebuilding, it may take some time before status is updated');
			}, function errorCallback(data) {
				alertify.error('Unable to rebuild CI job');
			}); 
		}
	};
	
	// --------------------  Context menu ------------------------
	const OPEN_TEST_RUN = ['Open', function ($itemScope) {
		var testRun = $itemScope.jtr.testRuns[$itemScope.jobView.job.id];
        window.open($location.$$absUrl.split("views")[0] + "tests/runs?id=" + testRun.id, '_blank');
    }];
	
	const REBUILD = ['Rebuild', function ($itemScope) {
		
		var testRun = $itemScope.jtr.testRuns[$itemScope.jobView.job.id];
		if($scope.jenkinsEnabled)
		{
			$scope.rebuildJobs([testRun.id]);
		}
		else
		{
			window.open($itemScope.jobView.job.jobURL + "/" + testRun.buildNumber + '/rebuild/parameterized', '_blank');
		}
    }];
	
	const COPY_TEST_RUN_LINK = ['Copy link', function ($itemScope) {
		var testRun = $itemScope.jtr.testRuns[$itemScope.jobView.job.id];
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
		$scope.loadView();
		$scope.loadJobViews();
		$scope.loadJobs();
		ConfigService.getConfig("jenkins").then(function(jenkins) {
			$scope.jenkinsEnabled = jenkins.enabled;
		});
	})();
} ]);
