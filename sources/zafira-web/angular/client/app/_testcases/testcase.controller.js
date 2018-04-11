(function () {
    'use strict';

    angular
        .module('app.testcase')
        .controller('TestCaseListController', ['$scope', '$rootScope', '$location', '$mdDateRangePicker', 'TestService', 'TestCaseService', 'UtilService', 'ProjectProvider', TestCaseListController])
        .controller('MetricController', ['$scope', '$stateParams', '$q', 'TestCaseService', MetricController])

       // **************************************************************************
    function TestCaseListController($scope, $rootScope, $location, $mdDateRangePicker, TestService, TestCaseService, UtilService, ProjectProvider) {

    	var DEFAULT_SC = {page : 1, pageSize : 20};

    	// TODO: make percent range for testcase label color configurable
        $scope.range = [0.25, 0.75];

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

    		TestCaseService.searchTestCases(ProjectProvider.initProjects($scope.sc)).then(function(rs) {
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

        $scope.goToPerformanceDashboard = function (testCaseId) {
            $location.path("/tests/cases/" + testCaseId + "/metrics");
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

    function MetricController($scope, $stateParams, $q, TestCaseService) {

        $scope.metrics = {};

        var options = {
            series: [],
            axes: {
                x: {
                    key: 'createdAt',
                    type: 'date'
                }
            }
        };

        function getTestMetricsByTestCaseId() {
            return $q(function(resolve, reject) {
                TestCaseService.getTestMetricsByTestCaseId($stateParams.id).then(function (rs) {
                    if(rs.success) {
                        $scope.metrics = collectByOperation(rs.data);
                        resolve(rs);
                    } else {
                        alertify.error(rs.message);
                        reject(rs.message);
                    }
                })
            });
        };

        function collectByOperation(metricsMap) {
            var result = {};
            angular.forEach(metricsMap, function (metrics, env) {
                metrics.sort(compareByDate);
                metrics.forEach(function(metric) {
                    if(! result[metric.operation]) {
                        result[metric.operation] = {};
                        result[metric.operation].options = angular.copy(options);
                    }
                    if(result[metric.operation] && ! result[metric.operation][env]) {
                        result[metric.operation][env] = [];
                    }
                    metric.createdAt = new Date(metric.createdAt);
                    result[metric.operation][env].push(metric);
                });
            });
            return result;
        };

        function getRandomColor() {
            var letters = '0123456789ABCDEF';
            var color = '#';
            for (var i = 0; i < 6; i++) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        };

        function compareByDate(a,b) {
            if (a.createdAt < b.createdAt)
                return -1;
            if (a.createdAt > b.createdAt)
                return 1;
            return 0;
        }

        (function initController() {
            getTestMetricsByTestCaseId().then(function (rs) {
                if(rs.success) {
                    angular.forEach($scope.metrics, function (env, operation) {
                        angular.forEach(env, function (value, key) {
                            if(key == 'options')
                                return;
                            env.options.series.push({
                                axis: 'y',
                                dataset: key,
                                key: 'elapsed',
                                label: key + ' (ms)',
                                color: getRandomColor(),
                                type: ['line'],
                                id: key
                            });
                        });
                    });
                }
            });
        })();
    }
})();
