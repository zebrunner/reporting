'use strict';

ZafiraApp.controller('TestCasesListCtrl', [ '$scope', '$rootScope', '$http' ,'$location','UtilService', 'ProjectProvider', function($scope, $rootScope, $http, $location, UtilService, ProjectProvider) {

	$scope.UtilService = UtilService;
	
	$scope.testCasesSearchCriteria = {
		'page' : 1,
		'pageSize' : 25
	};
	
	$scope.testsSearchCriteria = {
		'page' : 1,
		'pageSize' : 10,
		'sortOrder' : 'DESC'
	};
	
	$scope.totalResults = 0;
	$scope.testCases = [];
	$scope.tests = {};
	
	$scope.loadTestCases = function(page, pageSize){
		
		$scope.testCasesSearchCriteria.page = page;
		if(pageSize)
		{
			$scope.testCasesSearchCriteria.pageSize = pageSize;
		}
		
		$http.post('tests/cases/search', ProjectProvider.initProject($scope.testCasesSearchCriteria)).success(function(data) {
			$scope.testCasesSearchCriteria.page = data.page;
			$scope.testCasesSearchCriteria.pageSize = data.pageSize;
			$scope.testCases = data.results;
			for(var i = 0; i < $scope.testCases.length; i++)
			{
				$scope.testCases[i].showDetails = false;
			}
			$scope.totalResults = data.totalResults;
		}).error(function() {
			console.error('Failed to search test cases');
		});
	};
	
	$scope.loadTests = function(testCase){
		if(testCase.showDetails)
		{
			$scope.testsSearchCriteria.testCaseId = testCase.id;
			
			$http.post('tests/search', $scope.testsSearchCriteria).success(function(data) {
				$scope.tests[testCase.id] = data.results;
			}).error(function() {
				console.error('Failed to search tests');
			});
		}
	};
	
	$scope.resetSearchCriteria = function(){
		$scope.testCasesSearchCriteria = {
			'page' : 1,
			'pageSize' : 25
		};
	};
	
	$scope.getClassName = function(fullName) {
		var parts = fullName.split(".");
		return parts[parts.length - 1];
	};
	
	(function init(){
		$scope.loadTestCases(1);
	})();
	
}]);
