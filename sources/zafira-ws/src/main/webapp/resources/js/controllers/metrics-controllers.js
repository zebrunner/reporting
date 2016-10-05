'use strict';

ZafiraApp.controller('TestMetricsListCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$routeParams', 'UtilService', 'ProjectProvider', function($scope, $rootScope, $http, $location, $routeParams, UtilService, ProjectProvider) {
	
	$scope.operations = [];
	$scope.widgetEnvs = [];
	$scope.widgets = {};
	$scope.timeSteps = ['Last week', 'Last month', 'Last year', 'All time'];
	$scope.timeStep = $scope.timeSteps[1];
	
	$scope.loadAllDashboards = function() {
		$http.get('dashboards/all').success(function(data) {
			$scope.loadWidget(data[0].widgets[0], $scope.timeStep);
		});
	};
	
	$scope.loadWidget = function(widgets) {
		var sqlAdapter = {};
		var testCaseSql = widgets.sql.replace('#{test_case_id}', $routeParams.id);
		sqlAdapter.sql = testCaseSql.replace('#{time_step}', $scope.convertInterval());
		$http.post('widgets/sql' + ProjectProvider.getProjectQueryParam(), sqlAdapter).success(function(data) {
			$scope.populateEnvsAndOperations(data);
			$scope.widgets.model = JSON.parse(widgets.model);
			$scope.widgets.data = data;
		});
	};
	
	$scope.convertInterval = function() {
		switch($scope.timeStep)
		{
			case 'Last week': return "and tm.created_at >= CURRENT_TIMESTAMP - interval '1 days'";
			case 'Last month': return "and tm.created_at >= CURRENT_TIMESTAMP - interval '1 month'";
			case 'Last year': return "and tm.created_at >= CURRENT_TIMESTAMP - interval '1 year'";
			case 'All time': return "";
			default: return "";
		}
	}
	
	$scope.chartFilter = function(operation, env) {
		var result = [];
		for(var i = 0, j = 0; i < $scope.widgets.data.length; i++) {
			if($scope.widgets.data[i].env == env && $scope.widgets.data[i].operation == operation) {
				result[j] = $scope.widgets.data[i];
				j++;
			}
		}
		$scope.widgets.data.dataset = result;
		return $scope.widgets.data;
	}
	
	$scope.populateEnvsAndOperations = function(data) {
		for(var i = 0; i < data.length; i++)
		{
			if(data[i].CREATED_AT) data[i].CREATED_AT = new Date(data[i].CREATED_AT);
			if($scope.widgetEnvs.indexOf(data[i].env) == -1) $scope.widgetEnvs[$scope.widgetEnvs.length] = data[i].env;
			if($scope.operations.indexOf(data[i].operation) == -1) $scope.operations[$scope.operations.length] = data[i].operation;
		}
	};
	
	(function init(){
		$scope.loadAllDashboards();
	})();
	
}]);
