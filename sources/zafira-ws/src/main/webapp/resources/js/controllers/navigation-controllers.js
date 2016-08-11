'use strict';

ZafiraApp.controller('NavigationCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$cookieStore', '$route', function($scope, $rootScope, $http, $location, $cookieStore, $route) {

	$scope.project = null;
	$scope.projects = [];
	
	$scope.loadProjects = function(){
		$http.get('config/projects').success(function(projects) {
			$scope.projects = projects;
		}).error(function() {
			console.error('Failed to load projects');
		});
	};
	
	$scope.setProject = function(project){
		$cookieStore.put("project", project);
		$scope.project = project;
		$route.reload();
	};
	
	(function init(){
		$scope.project = $cookieStore.get("project");
	})();
	
}]);