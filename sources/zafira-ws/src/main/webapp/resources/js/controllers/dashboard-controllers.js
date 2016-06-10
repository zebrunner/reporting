'use strict';

ZafiraApp.controller('DashboardCtrl', [ '$scope', '$rootScope', '$http', 'PubNub', 'UtilService', function($scope, $rootScope, $http, PubNub, UtilService) {

	$scope.UtilService = UtilService;
	
	$scope.showLoading = true;
	
	$scope.totalTestRuns = 0;
	
	$scope.tests = {};
	$scope.testRuns = {};
	$scope.testRunResults = {};
	$scope.testRunsTestIds = {};

	$scope.page = 1;
	$scope.pageSize = 20;
	
	$scope.testRunsToCompare = [];
	$scope.queryString = "";
	
	$scope.initPubNub = function(){
		$http.get('config/pubnub').success(function(config) {
			
			$scope.testRunsChannel = config['testRunsChannel'];
			$scope.testsChannel = config['testsChannel'];
			
			PubNub.init({publish_key:config['publishKey'],subscribe_key:config['subscribeKey'],uuid:config['udid'],ssl:true});
			
			PubNub.ngSubscribe({channel:$scope.testsChannel});
			PubNub.ngHistory({channel:$scope.testsChannel, count:100000});
			$scope.$on(PubNub.ngMsgEv($scope.testsChannel), function(event, payload) {
				$scope.addTest(payload.message.test);
			});
			
			PubNub.ngSubscribe({channel:$scope.testRunsChannel});
			PubNub.ngHistory({channel:$scope.testRunsChannel, count:5});
			$scope.$on(PubNub.ngMsgEv($scope.testRunsChannel), function(event, payload) {
				$scope.addTestRun(payload.message.testRun);
			});
		});
	};
	
	$scope.addTest = function(test) {
		if($scope.tests[test.id] == null)
    	{
    		$scope.tests[test.testRunId] = {};
    	}
		if($scope.testRunsTestIds[test.testRunId] == null)
    	{
			$scope.testRunsTestIds[test.testRunId] = [];
    	}
    	if($scope.testRunsTestIds[test.testRunId].indexOf(test.id) < 0)
    	{
    		$scope.testRunsTestIds[test.testRunId].push(test.id)
    	}
    	// Remove previous result
    	if($scope.tests[test.id] != null)
    	{
    		$scope.updateTestRunResults($scope.tests[test.id], -1);
    	}
    	$scope.tests[test.id] = test;
    	$scope.initTestRunResults(test.testRunId);
    	$scope.updateTestRunResults($scope.tests[test.id], 1);
		$scope.$apply();
	};
	
	$scope.updateTestRunResults = function(test, amount) {
		switch(test.status) {
			case "PASSED":
				$scope.testRunResults[test.testRunId].passed = $scope.testRunResults[test.testRunId].passed + amount;
				break;
			case "FAILED":
				$scope.testRunResults[test.testRunId].failed = $scope.testRunResults[test.testRunId].failed + amount;
				break;
			case "SKIPPED":
				$scope.testRunResults[test.testRunId].skipped = $scope.testRunResults[test.testRunId].skipped + amount;
				break;
			case "IN_PROGRESS":
				$scope.testRunResults[test.testRunId].in_progress = $scope.testRunResults[test.testRunId].in_progress + amount;
				break;
		}
	};
	
	$scope.addTestRun = function(testRun) {
		testRun.showDetails = false;
    	if($scope.testRuns[testRun.id] == null)
    	{
    		testRun.jenkinsURL = testRun.job.jobURL + "/" + testRun.buildNumber;
    		$scope.testRuns[testRun.id] = testRun;
    		$scope.totalTestRuns = $scope.totalTestRuns + 1;
    		$scope.initTestRunResults(testRun.id);
    	}
    	else
    	{
    		$scope.testRuns[testRun.id].status = testRun.status;
    	}
    	$scope.$apply();
	};
	
	$scope.initTestRunResults = function(testRunId) {
		if($scope.testRunResults[testRunId] == null)
    	{
			$scope.testRunResults[testRunId] = {};
			$scope.testRunResults[testRunId].passed = 0;
			$scope.testRunResults[testRunId].failed = 0;
			$scope.testRunResults[testRunId].skipped = 0;
			$scope.testRunResults[testRunId].in_progress = 0;
    	}
	};
	
	$scope.getArgValue = function(xml, key){
		var xmlDoc = new DOMParser().parseFromString(xml,"text/xml");
		var args = xmlDoc.getElementsByTagName("config")[0].childNodes;
		for(var i = 0; i < args.length; i++)
		{
			if(args[i].getElementsByTagName("key")[0].innerHTML == key)
			{
				return args[i].getElementsByTagName("value")[0].innerHTML;
			}
		}
		return null;
	};
	
	$scope.selectTestRun = function(id, isChecked) {		 
		if(isChecked == "true") {
			$scope.testRunsToCompare.push(id);
		} else {
			var idx = $scope.testRunsToCompare.indexOf(id);
			if(idx > -1){
				$scope.testRunsToCompare.splice(idx, 1);
			}
		}
		$scope.queryString = "";
		for(var i = 0; i < $scope.testRunsToCompare.length; i++)
		{
			$scope.queryString = $scope.queryString + $scope.testRunsToCompare[i];
			if(i < $scope.testRunsToCompare.length - 1)
			{
				$scope.queryString = $scope.queryString + "+";
			}
		}
	};
	
	(function init(){
		$scope.initPubNub();
		setTimeout(function() {  
			$scope.$apply(function () {
				$scope.showLoading = false;
			});
		}, 30000);
	})();
} ]);
