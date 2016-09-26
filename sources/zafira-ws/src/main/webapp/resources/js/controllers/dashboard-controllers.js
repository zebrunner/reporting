'use strict';

ZafiraApp.controller('DashboardCtrl', [ '$scope', '$rootScope', '$http', '$location', 'ProjectProvider', '$modal', '$route', function($scope, $rootScope, $http, $location, ProjectProvider, $modal, $route) {
	
	$scope.loadAllDashboards = function() {
		$http.get('dashboard/all').success(function(dashboards) {
			$scope.dashboards = dashboards;
			for(var i = 0; i < dashboards.length; i++)
			{
				$scope.loadDashboardData(dashboards[i]);
			}
		});
	};
	
	$scope.loadDashboardData = function(dashboard) {
		var sqlAdapter = {};
		sqlAdapter.sql = dashboard.sql;
		$http.post('dashboard/sql' + ProjectProvider.getProjectQueryParam(), sqlAdapter).success(function(data) {
			for(var j = 0; j < data.length; j++)
			{
				if(data[j].CREATED_AT)
				{
					data[j].CREATED_AT = new Date(data[j].CREATED_AT);
				}
			}
			dashboard.model = JSON.parse(dashboard.model);
			dashboard.data = {};
			dashboard.data.dataset = data;
		});
	};
	
	(function init(){
		$scope.loadAllDashboards();
	})();
	
	
	$scope.openDashboardDetailsModal = function(id, copy){
		if(id)
		{
			$http.get('dashboard/' + id).success(function(dashboard) {
				$modal.open({
					templateUrl : 'resources/templates/dashboard-details-modal.jsp',
					resolve : {
						'dashboard' : function(){
							return dashboard;
						}
					},
					controller : function($scope, $modalInstance, dashboard){
						
						$scope.dashboard = dashboard;
						if(copy)
						{
							$scope.dashboard.id = null;
						}
						
						$scope.createDashboard = function(dashboard){
							$http.post('dashboard', dashboard).success(function(data) {
								$route.reload();
							}).error(function(data, status) {
								alert('Failed to create dashboard');
							});
							$modalInstance.close(0);
						};
		
						$scope.updateDashboard = function(dashboard){
							$http.put('dashboard', dashboard).success(function(data) {
								$route.reload();
							}).error(function(data, status) {
								alert('Failed to update dashboard');
							});
							$modalInstance.close(0);
						};
						
						$scope.deleteDashboard = function(dashboard){
							$http.delete('dashboard/' + dashboard.id).success(function() {
								$route.reload();
							}).error(function(data, status) {
								alert('Failed to delete dashboard');
							});
							$modalInstance.close(0);
						};
						
						$scope.cancel = function(){
							$modalInstance.close(0);
						};
					}
				});
			}).error(function() {
				console.error('Failed to load dashboard');
			});
		}
		else
		{
			$modal.open({
				templateUrl : 'resources/templates/dashboard-details-modal.jsp',
				controller : function($scope, $modalInstance){
					
					$scope.dashboard = {};
	
					$scope.createDashboard = function(dashboard){
						$http.post('dashboard', dashboard).success(function(data) {
							$route.reload();
						}).error(function(data, status) {
							alert('Failed to create dashboard');
						});
						$modalInstance.close(0);
					};
					
					$scope.cancel = function(){
						$modalInstance.close(0);
					};
				}
			});
		}
	};
	
	$scope.data = [
	               {label: "one", value: 12.2}, 
	               {label: "two", value: 45},
	               {label: "three", value: 10}
	             ];
	$scope.options = {thickness: 10};
	
}]);
