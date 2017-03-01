'use strict';

ZafiraApp.controller('NavigationCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$cookieStore', '$route', '$modal', 'ProjectProvider', '$window', 'UtilService', 'UserService', 'DashboardService', 'ConfigService', function($scope, $rootScope, $http, $location, $cookieStore, $route, $modal, ProjectProvider, $window, UtilService, UserService, DashboardService, ConfigService) {

	$scope.UtilService = UtilService;
	
	$scope.project = null;
	$scope.version = null;
	$scope.projects = [];
	$scope.views = [];
	
	$scope.currentUser = null;
	
	$scope.pefrDashboardId = null;
	
	$scope.loadProjects = function(){
		ConfigService.getConfig("projects").then(function(projects) {
			$scope.projects = projects;
		});
	};
	
	$scope.loadViews = function(){
		$http.get('views' + ProjectProvider.getProjectIdQueryParam()).then(function successCallback(views) {
			$scope.views = views.data;
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
						alertify.success("Project created successfully");
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
	
	$scope.openViewDetailsModal = function(view){
		$modal.open({
			templateUrl : 'resources/templates/view-details-modal.jsp',
			resolve : {
				'configService' : function() {
					return ConfigService;
				},
				'view' : function() {
					return view;
				}
			},
			controller : function($scope, $modalInstance, configService, view){
				$scope.view = {};
				if(view != null)
				{
					$scope.view.id = view.id;
					$scope.view.name = view.name;
					$scope.view.projectId = view.project.id;
				}
				
				configService.getConfig("projects").then(function(projects) {
					$scope.projects = projects;
				});
				
				$scope.createView = function(view){
					$http.post('views', view).then(function successCallback(data) 
					{
						alertify.success("View created successfully");
					}, function errorCallback(data) {
						alertify.error('Failed to create view');
					});
					$modalInstance.close(0);
				};
				
				$scope.updateView = function(view){
					$http.put('views', view).then(function successCallback(data) 
					{
						alertify.success("View updated successfully");
					}, function errorCallback(data) {
						alertify.error('Failed to update view');
					});
					$modalInstance.close(0);
				};
				
				$scope.deleteView = function(view){
					$http.delete('views/' + view.id).then(function successCallback() 
					{
						alertify.success("View deleted successfully");
					}, function errorCallback(data) {
						alertify.error('Failed to delete view');
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
		
		ConfigService.getConfig("version").then(function(version) {
			$scope.version = version;
		});
	})();
}]);