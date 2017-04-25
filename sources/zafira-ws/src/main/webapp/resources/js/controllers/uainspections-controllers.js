'use strict';

ZafiraApp.controller('UAInspectionsCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$modal', '$route', '$q', '$timeout', function($scope, $rootScope, $http, $location, $modal, $route, $q, $timeout) {

	const DEFAULT_SC = {
			'page' : 1,
			'pageSize' : 20
	};
	
	$scope.showReset = false;
	$scope.inspectionsSearchCriteria = angular.copy(DEFAULT_SC);
	
	$scope.totalResults = 0;
	$scope.inspections = [];
	
	$scope.loadInspections = function(page, pageSize){
		
		$scope.inspectionsSearchCriteria.page = page;
		if(pageSize)
		{
			$scope.inspectionsSearchCriteria.pageSize = pageSize;
		}
		
		$http.post('uainspections/search', $scope.inspectionsSearchCriteria).then(function successCallback(data) {
			var data = data.data;
			$scope.inspectionsSearchCriteria.page = data.page;
			$scope.inspectionsSearchCriteria.pageSize = data.pageSize;
			$scope.inspections = data.results;
			$scope.totalResults = data.totalResults;
		}, function errorCallback(data) {
			console.error('Failed to search users');
		});
	};
	
	
	$scope.resetSearchCriteria = function(){
		$scope.inspectionsSearchCriteria = angular.copy(DEFAULT_SC);
		$scope.showReset = false;
	};
	
	(function init(){
		$scope.loadInspections(1);
	})();
	
}]);