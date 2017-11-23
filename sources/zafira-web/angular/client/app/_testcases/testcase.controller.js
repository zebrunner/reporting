(function () {
    'use strict';

    angular
        .module('app.testcase')
        .controller('TestCaseListController', ['$scope', '$location', '$mdDateRangePicker', 'TestService', 'TestCaseService', 'UtilService', 'ProjectProvider', TestCaseListController])

       // **************************************************************************
    function TestCaseListController($scope, $location, $mdDateRangePicker, TestService, TestCaseService, UtilService, ProjectProvider) {

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

            if ($scope.selectedRange.dateStart && $scope.selectedRange.dateEnd) {
                if(!$scope.isEqualDate()){
                    $scope.sc.fromDate = $scope.selectedRange.dateStart;
                    $scope.sc.toDate = $scope.selectedRange.dateEnd;
                }
                else {
                    $scope.sc.date = $scope.selectedRange.dateStart;
                }
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

        $scope.isEqualDate = function() {
            if($scope.selectedRange.dateStart && $scope.selectedRange.dateEnd){
                return $scope.selectedRange.dateStart.getTime() === $scope.selectedRange.dateEnd.getTime();
            }
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

        $scope.reset = function () {
            $scope.selectedRange.dateStart = null;
            $scope.selectedRange.dateEnd = null;
            $scope.sc = angular.copy(DEFAULT_SC);
        	$scope.search();
        };

        $scope.getClassName = function(fullName) {
    		var parts = fullName.split(".");
    		return parts[parts.length - 1];
    	};

        /**
         DataRangePicker functionality
         */

        var tmpToday = new Date();
        $scope.selectedRange = {
            selectedTemplate: null,
            selectedTemplateName: null,
            dateStart: null,
            dateEnd: null,
            showTemplate: false,
            fullscreen: false
        };

        $scope.onSelect = function(scope) {
            console.log($scope.selectedRange.selectedTemplateName);
            return $scope.selectedRange.selectedTemplateName;
        };

        $scope.pick = function($event, showTemplate) {
            $scope.selectedRange.showTemplate = showTemplate;
            $mdDateRangePicker.show({
                targetEvent: $event,
                model: $scope.selectedRange
            }).then(function(result) {
                if (result) $scope.selectedRange = result;
            })
        };

        $scope.clear = function() {
            $scope.selectedRange.selectedTemplate = null;
            $scope.selectedRange.selectedTemplateName = null;
            $scope.selectedRange.dateStart = null;
            $scope.selectedRange.dateEnd = null;
        };

        $scope.isFuture = function($date) {
            return $date.getTime() < new Date().getTime();
        };

        (function initController() {
			$scope.search(1);
		})();
	}
})();
