'use strict';

ZafiraApp.controller('DashboardCtrl', [ '$scope', '$rootScope', '$http', 'PubNub', function($scope, $rootScope, $http, PubNub) {

	$scope.testRuns = {};
	$scope.tests = {};
	
	$scope.initPubNub = function(){
		$http.get('config/pubnub').success(function(config) {
			$scope.channel = config['channel'];
			PubNub.init({publish_key:config['publishKey'],subscribe_key:config['subscribeKey'],uuid:config['udid'],ssl:true});
			PubNub.ngSubscribe({channel:$scope.channel});
			PubNub.ngHistory({channel:$scope.channel, count:2500});
			$rootScope.$on(PubNub.ngMsgEv($scope.channel), function(event, payload) {
				var message = payload.message;
				console.log(message);
				$scope.$apply(function () {
					switch(message.type) {
					    case "TEST_RUN":
					    	message.testRun.showDetails = false;
					    	if($scope.testRuns[message.testRun.id] == null)
					    	{
					    		message.testRun.passed = 0;
					    		message.testRun.failed = 0;
					    		message.testRun.skipped = 0;
					    		message.testRun.jenkinsURL = message.testRun.job.jobURL + "/" + message.testRun.buildNumber;
					    		$scope.testRuns[message.testRun.id] = message.testRun;
					    	}
					    	else
					    	{
					    		$scope.testRuns[message.testRun.id].status = message.testRun.status;
					    	}
					        break;
					    case "TEST":
					    	if($scope.tests[message.test.testRunId] == null)
					    	{
					    		$scope.tests[message.test.testRunId] = [];
					    	}
					    	$scope.tests[message.test.testRunId].push(message.test);
					    	if($scope.testRuns[message.test.testRunId] != null)
					    	{
					    		switch(message.test.status) {
						    		case "PASSED":
						    			$scope.testRuns[message.test.testRunId].passed = $scope.testRuns[message.test.testRunId].passed + 1;
						    			break;
						    		case "FAILED":
						    			$scope.testRuns[message.test.testRunId].failed = $scope.testRuns[message.test.testRunId].failed + 1;
						    			break;
						    		case "SKIPPED":
						    			$scope.testRuns[message.test.testRunId].skipped = $scope.testRuns[message.test.testRunId].skipped + 1;
						    			break;
						    	}
					    	}
					        break;
					    default:
					        colsole.log("Unsupported type: " + message.type);
					}
				});
			});
		});
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
	
	(function init(){
		$scope.initPubNub();
	})();
} ]);
