'use strict';

ZafiraApp.controller('NavigationCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$cookieStore', '$route', '$modal', 'ProjectProvider', '$window', 'UtilService', 'UserService', function($scope, $rootScope, $http, $location, $cookieStore, $route, $modal, ProjectProvider, $window, UtilService, UserService) {

	$scope.UtilService = UtilService;
	
	$scope.project = null;
	$scope.projects = [];
	
	$scope.currentUser = null;
	
	$scope.loadProjects = function(){
		$http.get('config/projects').success(function(projects) {
			$scope.projects = projects;
		}).error(function() {
			console.error('Failed to load projects');
		});
	};
	
	$scope.createProject = function(newProject){
		$http.post('projects').success(function(newProject) {
			
		}).error(function() {
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
					$http.post('projects', project).success(function(data) {
					}).error(function(data, status) {
						alert('Failed to create project');
					});
					$modalInstance.close(0);
				};
				
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		});
	};
	
	(function init(){
		$scope.project = ProjectProvider.getProject();
		UserService.getCurrentUser().then(function(user) {
			$scope.currentUser = user;
		});
	})();
	
}]);