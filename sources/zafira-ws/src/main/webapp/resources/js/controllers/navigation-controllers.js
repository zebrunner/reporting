'use strict';

ZafiraApp.controller('NavigationCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$cookieStore', '$route', '$modal', 'ProjectProvider', '$window', 'UtilService', 'UserService', 'DashboardService', function($scope, $rootScope, $http, $location, $cookieStore, $route, $modal, ProjectProvider, $window, UtilService, UserService, DashboardService) {

	$scope.UtilService = UtilService;
	
	$scope.project = null;
	$scope.projects = [];
	
	$scope.currentUser = null;
	
	$scope.pefrDashboardId = null;
	
	$scope.loadProjects = function(){
		$http.get('config/projects').then(function successCallback(projects) {
			$scope.projects = projects.data;
		}, function errorCallback(projects) {
			console.error('Failed to load projects');
		});
	};
	
	$scope.createProject = function(newProject){
		$http.post('projects').then(function successCallback(newProject) {
			
		}, function errorCallback(newProject) {
			console.error('Failed to load projects');
		});
	};
	
	$scope.setProject = function(project){
		ProjectProvider.setProject(project);
		$window.location.reload();
	};
	
	$scope.openProjectDetailsModal = function(){
		$modal.open({
			templateUrl : 'resources/templates/project-details-modal.jsp',
			controller : function($scope, $modalInstance){
				$scope.project = {};
				$scope.createProject = function(project){
					$http.post('projects', project).then(function successCallback(data) {
					}, function errorCallback(data) {
						alertify.error('Failed to create project');
					});
					$modalInstance.close(0);
				};
				
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		}).result.then(function(data) {
        }, function () {
        });
	};
	
	(function init(){
		$scope.project = ProjectProvider.getProject();
		UserService.getCurrentUser().then(function(user) {
			$scope.currentUser = user;
		});
		DashboardService.getUserPerformanceDashboardId().then(function(dashboardId) {
			$scope.pefrDashboardId = dashboardId;
		});
	})();
}]);