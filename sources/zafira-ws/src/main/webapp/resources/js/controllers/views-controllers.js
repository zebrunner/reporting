'use strict';

ZafiraApp.controller('JobViewsCtrl', [ '$scope', '$http','$location', '$route', '$routeParams', '$modal', 'UtilService', 'ConfigService', function($scope, $http, $location, $route, $routeParams, $modal, UtilService, ConfigService) {
	
	$scope.view = {};
	$scope.jobViews = {};
	$scope.jobs = [];
	
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
		}, function errorCallback(data) {
			console.error('Failed to load job views');
		});
	};
	
	$scope.loadJobs = function(){
		$http.get('jobs').then(function successCallback(jobs) {
			$scope.jobs = jobs.data;
		}, function errorCallback(data) {
			console.error('Failed to load jobs');
		});
	};
	
	$scope.deleteJobViews = function(env){
		$http.delete('jobs/views/' + $routeParams.id + "?env=" + env).then(function successCallback() {
			delete $scope.jobViews[env];
			alertify.success('Job view deleted successfully');
		}, function errorCallback(data) {
			alertify.error('Failed to delete job view');
		});
	};
	
	$scope.openJobsViewModal = function(env){
		$modal.open({
			templateUrl : 'resources/templates/jobs-view-details-modal.jsp',
			resolve : {
				'viewId' : function(){
					return parseInt($routeParams.id);
				},
				'jobs' : function(){
					return $scope.jobs;
				},
				'env' : function(){
					return $scope.env;
				}
			},
			controller : function($scope, $modalInstance, viewId, env, jobs){
				
				$scope.jobs = jobs;
				$scope.jobsView = {};
				
				var jobsSelected = [];
				
				$scope.selectJob = function(id, isChecked) {		 
					if(isChecked) {
						jobsSelected.push(id);
					} else {
						var idx = jobsSelected.indexOf(id);
						if(idx > -1){
							jobsSelected.splice(idx, 1);
						}
					}          
				};
				
				$scope.create = function(){
					var jobsViews = [];
					for(var i = 0; i < jobsSelected.length; i++)
					{
						var jobView = {'viewId': viewId, 'job': {'id': jobsSelected[i]}, 'env': $scope.jobsView.env, 'position': 0, 'size': $scope.jobsView.size};
						jobsViews.push(jobView);
					}
					$http.post('jobs/views', jobsViews).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
						alertify.success('Job view created successfully');
					}, function errorCallback(data) {
						alertify.error('Failed to create job view');
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
	
	// --------------------  Context menu ------------------------
	const OPEN_TEST_RUN = ['Open', function ($itemScope) {
        window.open($location.$$absUrl + "?id=" + $itemScope.jtr.testRuns[jobView.job.id].id, '_blank');
    }];
	
	const REBUILD = ['Rebuild', function ($itemScope) {
		
		ConfigService.getConfig("jenkins").then(function(jenkins) {
			if(jenkins.enabled)
			{
				var rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
				$http.get('tests/runs/' + $itemScope.testRun.id + '/rerun?rerunFailures=' + rerunFailures).then(function successCallback(data) {
					alertify.success('CI job is rebuilding, it may take some time before status is updated');
				}, function errorCallback(data) {
					alertify.error('Unable to rebuild CI job');
				});
			}
			else
			{
				window.open($itemScope.testRun.jenkinsURL + '/rebuild/parameterized', '_blank');
			}
		});
    }];
	
	const COPY_TEST_RUN_LINK = ['Copy link', function ($itemScope) {
	  	var node = document.createElement('pre');
  	    node.textContent = $location.$$absUrl + "?id=" + $itemScope.testRun.id;
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
	})();
} ]);
