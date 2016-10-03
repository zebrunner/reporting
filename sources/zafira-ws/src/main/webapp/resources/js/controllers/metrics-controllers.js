'use strict';

ZafiraApp.controller('TestMetricsListCtrl', [ '$scope', '$rootScope', '$http' ,'$location','UtilService', 'ProjectProvider', function($scope, $rootScope, $http, $location, UtilService, ProjectProvider) {

	$scope.testMetrics = [];
	
	$scope.init = function() {
		console.log("Init...");
	};
	
	$scope.openPerformancePage = function(testCase) {
		$http.get('metrics/sync').success(function(data) {
			$route.reload();
		}).error(function(data, status) {
			alert('Devices not synced!');
		});
	};
	
	(function init(){
		$scope.init();
	})();
	
}]);
