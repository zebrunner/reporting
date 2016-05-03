'use strict';

ZafiraApp.controller('DashboardCtrl', [ '$scope', '$rootScope', '$http', 'PubNub', function($scope, $rootScope, $http, PubNub) {

	$scope.testRuns = {};
	$scope.totalTestRuns = 0;
	$scope.testRunResults = {};
	
	$scope.tests = {};
	$scope.totalTests = 0;

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
			PubNub.ngHistory({channel:$scope.testsChannel, count:300000});
			$rootScope.$on(PubNub.ngMsgEv($scope.testsChannel), function(event, payload) {
				var message = payload.message;
				console.log(message);
				$scope.$apply(function () {
					if($scope.tests[message.test.testRunId] == null)
			    	{
			    		$scope.tests[message.test.testRunId] = [];
			    	}
			    	$scope.tests[message.test.testRunId].push(message.test);
			    	$scope.initTestRunResults(message.test.testRunId);
		    		switch(message.test.status) {
			    		case "PASSED":
			    			$scope.testRunResults[message.test.testRunId].passed = $scope.testRunResults[message.test.testRunId].passed + 1;
			    			break;
			    		case "FAILED":
			    			$scope.testRunResults[message.test.testRunId].failed = $scope.testRunResults[message.test.testRunId].failed + 1;
			    			break;
			    		case "SKIPPED":
			    			$scope.testRunResults[message.test.testRunId].skipped = $scope.testRunResults[message.test.testRunId].skipped + 1;
			    			break;
			    	}
		    		$scope.totalTests = $scope.totalTests + 1;
				});
			});
			
			PubNub.ngSubscribe({channel:$scope.testRunsChannel});
			PubNub.ngHistory({channel:$scope.testRunsChannel, count:1000});
			$rootScope.$on(PubNub.ngMsgEv($scope.testRunsChannel), function(event, payload) {
				var message = payload.message;
				console.log(message);
				$scope.$apply(function () {
					message.testRun.showDetails = false;
			    	if($scope.testRuns[message.testRun.id] == null)
			    	{
			    		message.testRun.jenkinsURL = message.testRun.job.jobURL + "/" + message.testRun.buildNumber;
			    		$scope.testRuns[message.testRun.id] = message.testRun;
			    		$scope.totalTestRuns = $scope.totalTestRuns + 1;
			    		$scope.initTestRunResults(message.testRun.id);
			    	}
			    	else
			    	{
			    		$scope.testRuns[message.testRun.id].status = message.testRun.status;
			    	}
				});
			});
			
			
		});
	};
	
	$scope.initTestRunResults = function(testRunId) {
		if($scope.testRunResults[testRunId] == null)
    	{
			$scope.testRunResults[testRunId] = {};
			$scope.testRunResults[testRunId].passed = 0;
			$scope.testRunResults[testRunId].failed = 0;
			$scope.testRunResults[testRunId].skipped = 0;
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
	
	$scope.showMore = function() {
		$scope.page = $scope.page + 1;
	};
	
	$scope.compareTestRunResults = function() {
		$scope.page = $scope.page + 1;
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
	})();
} ]);
