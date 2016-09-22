'use strict';

ZafiraApp.controller('DashboardCtrl', [ '$scope', '$rootScope', '$http', '$location', 'ProjectProvider', function($scope, $rootScope, $http, $location, ProjectProvider) {

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
	})();
	
}]);
