'use strict';

ZafiraApp.controller('DashboardCtrl', [ '$scope', '$rootScope', '$http', '$location', 'ProjectProvider', '$modal', '$route', function($scope, $rootScope, $http, $location, ProjectProvider, $modal, $route) {

	
	$scope.loadTestStatusesStatistics = function() {
		$http.get('tests/statuses/statistics' + ProjectProvider.getProjectQueryParam()).success(function(results) {
			var data = [];
			angular.element(document.querySelector('#test-results-statistics')).html('');
			for (var key in results)
			{ 
				data.push({createdAt: parseInt(key), IN_PROGRESS: results[key]["IN_PROGRESS"].count, PASSED: results[key]["PASSED"].count, FAILED: results[key]["FAILED"].count, SKIPPED: results[key]["SKIPPED"].count});
			}
			Morris.Line({
				  element: 'test-results-statistics',
				  data: data,
				  xkey: 'createdAt',
				  ykeys: ['IN_PROGRESS', 'PASSED', 'FAILED', 'SKIPPED' ],
				  lineColors: ['#3a87ad', '#5cb85c', '#d9534f', '#f0ad4e'],
				  labels: ["Incomplete", 
				           "Passed", 
				           "Failed",
				           "Skipped"],
				  dateFormat: function (x) { return new Date(x).toLocaleDateString("en-US") },
				  smooth: false
			});
		});
	};
	
	$scope.loadTestCaseOwnersStatistics = function() {
		$http.get('tests/cases/owners/statistics' + ProjectProvider.getProjectQueryParam()).success(function(results) {
			var statistics = [];
			for(var i = 0; i < results.length; i++)
			{
				var item = {};
				item.label = results[i].userName;
				item.value = results[i].count;
				statistics.push(item);
			}
			Morris.Donut({
			  element: 'test-owners-statistics',
			  data: statistics
			});
		});
	};
	
	$scope.loadAllDashboards = function() {
		$http.get('dashboard/all').success(function(dashboards) {
			$scope.dashboards = dashboards;
			$scope.loadDashboardData(0);
		});
	};
	
	$scope.loadDashboardData = function(index) {
		if($scope.dashboards && $scope.dashboards[index])
		{
			var sqlAdapter = {};
			sqlAdapter.sql = $scope.dashboards[index].sql;
			$http.post('dashboard/sql', sqlAdapter).success(function(data) {
				for(var j = 0; j < data.length; j++)
				{
					if(data[j].CREATED_AT)
					{
						data[j].CREATED_AT = new Date(data[j].CREATED_AT);
					}
				}
				$scope.dashboards[index].model = JSON.parse($scope.dashboards[index].model);
				$scope.dashboards[index].data = {};
				$scope.dashboards[index].data.dataset = data;
				$scope.loadDashboardData(index + 1);
			});
		}
	};
	
	
	$scope.loadTestCaseImplementationStatistics = function() {
		$http.get('tests/cases/implementation/statistics' + ProjectProvider.getProjectQueryParam()).success(function(results) {
			var statistics = [];
			for(var i = 0; i < results.length; i++)
			{
				var item = {};
				item.x = new Date(results[i].date).toLocaleDateString("en-US");
				item.y = results[i].count;
				statistics.push(item);
			}
			Morris.Bar({
			  element: 'test-implementation-statistics',
			  xkey: 'x',
			  ykeys: ['y'],
			  data: statistics,
			  labels: ['Tests implemented'],
			  barColors: ['#5cb85c']
			});
		});
	};
	
	
	(function init(){
		$scope.loadTestStatusesStatistics();
		$scope.loadTestCaseOwnersStatistics();
		$scope.loadTestCaseImplementationStatistics();
		$scope.loadAllDashboards();
	})();
	
	
	$scope.openDashboardDetailsModal = function(id){
		if(id)
		{
			$http.get('dashboard/' + id).success(function(dashboard) {
				$modal.open({
					templateUrl : 'resources/templates/dashboard-details-modal.jsp',
					resolve : {
						'id' : function(){
							return id;
						},
						'dashboard' : function(){
							return dashboard;
						}
					},
					controller : function($scope, $modalInstance, id, dashboard){
						
						$scope.id = id;
						$scope.dashboard = dashboard;
		
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
	
}]);
