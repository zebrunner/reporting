(function () {
    'use strict';

    angular
        .module('app.testcase')
        .controller('TestCaseListController', ['$scope', '$location', 'TestService', 'TestCaseService', 'UtilService', TestCaseListController])

       // **************************************************************************
    function TestCaseListController($scope, $location, TestService, TestCaseService, UtilService) {

    	var DEFAULT_SC = {page : 1, pageSize : 20};

    	$scope.UtilService = UtilService;

    	$scope.sc = angular.copy(DEFAULT_SC);
    	$scope.tests = {};

    	$scope.search = function (page) {
    		if(page)
    		{
    			$scope.sc.page = page;
    		}
    		TestCaseService.searchTestCases($scope.sc).then(function(rs) {
				if(rs.success)
        		{
        			$scope.sr = rs.data;
        		}
        		else
        		{
        			alertify.error(rs.message);
        		}
			});
        };

        $scope.loadTests = function(testCase) {

        	testCase.expand == null ? testCase.expand = true : testCase.expand = !testCase.expand;

        	if(testCase.expand)
        	{
        		TestService.searchTests({page: 1, pageSize: 10, testCaseId : testCase.id}).then(function(rs) {
    				if(rs.success)
            		{
    					$scope.tests[testCase.id] = rs.data.results;
            		}
            		else
            		{
            			alertify.error(rs.message);
            		}
    			});
        	}

        };

        $scope.reset = function () {
        	$scope.sc = angular.copy(DEFAULT_SC);
        	$scope.search();
        };

        $scope.getClassName = function(fullName) {
    		var parts = fullName.split(".");
    		return parts[parts.length - 1];
    	};

		(function initController() {
			$scope.search(1);
		})();
	}
})();
