(function () {
    'use strict';

    angular
        .module('app.testcase')
        .controller('TestCaseListController', ['$scope', '$location', 'TestService', 'TestCaseService', 'UtilService', 'ProjectProvider', TestCaseListController])

       // **************************************************************************
    function TestCaseListController($scope, $location, TestService, TestCaseService, UtilService, ProjectProvider) {

    	var DEFAULT_SC = {page : 1, pageSize : 20};

    	$scope.UtilService = UtilService;

    	$scope.sc = angular.copy(DEFAULT_SC);
    	$scope.tests = {};

    	$scope.search = function (page) {
            $scope.sc.date = null;
            $scope.sc.toDate = null;
            $scope.sc.fromDate = null;

    		if(page)
    		{
    			$scope.sc.page = page;
    		}

            if ($scope.sc.period == ""){
                $scope.sc.date = $scope.sc.chosenDate;
            }
            else if ($scope.sc.period == "before"){
                $scope.sc.toDate =  $scope.sc.chosenDate;
            }
            else if ($scope.sc.period == "after") {
                $scope.sc.fromDate = $scope.sc.chosenDate;
            }
            else if ($scope.sc.period == "between") {
                $scope.sc.fromDate = $scope.sc.chosenDate;
                $scope.sc.toDate =  $scope.sc.endDate;
            }

    		TestCaseService.searchTestCases(ProjectProvider.initProject($scope.sc)).then(function(rs) {
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
        		TestService.searchTests({page: 1, pageSize: 10, sortOrder: 'DESC', testCaseId : testCase.id}).then(function(rs) {
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

        $scope.isDateChosen = true;
        $scope.isDateBetween = false;

        $scope.changePeriod = function () {
            if ($scope.sc.period == "between") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = true;
            }
            else if ($scope.sc.period == "before" || $scope.sc.period == "after" || $scope.sc.period == "") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = false;
            }
            else {
                $scope.isDateChosen = false;
                $scope.isDateBetween = false;
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
