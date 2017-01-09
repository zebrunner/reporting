'use strict';

ZafiraApp.controller('TestRunsListCtrl', [ '$scope', '$rootScope', '$http' ,'$location','UtilService', 'ProjectProvider', '$modal', 'SettingsService', '$cookieStore', function($scope, $rootScope, $http, $location, UtilService, ProjectProvider, $modal, SettingsService, $cookieStore) {

	var OFFSET = new Date().getTimezoneOffset()*60*1000;
	
	$scope.predicate = 'startTime';
	$scope.reverse = false;
	
	$scope.UtilService = UtilService;
	$scope.testRunId = $location.search().id;
	
	$scope.testRunsToCompare = [];
	$scope.compareQueryString = "";
	
	$scope.testRuns = {};
	$scope.totalResults = 0;
	
	$scope.showRealTimeEvents = false;
	
	$scope.project = ProjectProvider.getProject();
	
	$scope.testRunSearchCriteria = {
		'page' : 1,
		'pageSize' : 20
	};
	
	$scope.testSearchCriteria = {
		'page' : 1,
		'pageSize' : 100000
	};
	
	$scope.initWebsocket = function() 
	{
  	  var sockJS = new SockJS("/zafira-ws/zafira-websocket");
  	  $scope.stomp = Stomp.over(sockJS);
  	  //stomp.debug = null;
  	  $scope.stomp.connect({}, function() {
  		$scope.stomp.subscribe("/topic/tests", function(data) 
  	      {
  	        	$scope.getMessage(data.body);
  	      });
  	  });
   };
   
   $scope.disconnectWebsocket = function() 
   {
	   if($scope.stomp != null)
	   {
		   $scope.stomp.disconnect();
	   }
   };
   
   $scope.getMessage = function(message) {
	 var event = JSON.parse(message.replace(/&quot;/g,'"').replace(/&lt;/g,'<').replace(/&gt;/g,'>'));
	 if(event.type == 'TEST_RUN')
	 {
		if(($scope.testRunId && $scope.testRunId != event.testRun.id) 
		|| ($scope.showRealTimeEvents == false && $scope.testRuns[event.testRun.id] == null)
		|| ($scope.project != null && $scope.project.id != event.testRun.project.id))
		{
			return;
		}
		
		$scope.addTestRun(event.testRun);
		$scope.$apply();
	 }
	 else if(event.type == 'TEST')
	 {
		$scope.addTest(event.test, true);
		$scope.$apply();
	 }
   	 return true;
   };
	
	$scope.addTest = function(test, isEvent) {
		var testRun = $scope.testRuns[test.testRunId];
		if(testRun == null)
		{
			return;
		}
		
		if(isEvent)
		{
			if(testRun.tests[test.id] != null)
			{
				$scope.updateTestRunResults(testRun.id, testRun.tests[test.id].status, -1);
			}
			testRun.tests[test.id] = test;
			$scope.updateTestRunResults(testRun.id, test.status, 1);
		}
		else
		{
			testRun.tests[test.id] = test;
		}
	};
	
	$scope.updateTestRunResults = function(id, status, changeByAmount)
	{
		switch(status) {
		case "PASSED":
			$scope.testRuns[id].passed = $scope.testRuns[id].passed + changeByAmount;
			break;
		case "FAILED":
			$scope.testRuns[id].failed = $scope.testRuns[id].failed + changeByAmount;
			break;
		case "SKIPPED":
			$scope.testRuns[id].skipped = $scope.testRuns[id].skipped + changeByAmount;
			break;
		default:
			break;
		}
	};
	
	$scope.deleteTestRun = function(id){
		if(confirm("Do you really want to delete test run?"))
		{
			$http.delete('tests/runs/' + id).success(function() {
				$scope.loadTestRuns($scope.testRunSearchCriteria.page);
			}).error(function(data, status) {
				alertify.error('Failed to delete test run');
			});
		}
	};
	
	$scope.addTestRun = function(testRun) {
		testRun.showDetails = $scope.testRunId ? true : false;
    	if($scope.testRuns[testRun.id] == null)
    	{
    		testRun.jenkinsURL = testRun.job.jobURL + "/" + testRun.buildNumber;
    		testRun.UID = testRun.testSuite.name + " " + testRun.jenkinsURL
    		testRun.tests = {};
    		$scope.testRuns[testRun.id] = testRun;
    	}
    	else
    	{
    		$scope.testRuns[testRun.id].status = testRun.status;
    	}
	};
	
	$scope.getArgValue = function(xml, key){
		try
		{
			var xmlDoc = new DOMParser().parseFromString(xml,"text/xml");
			var args = xmlDoc.getElementsByTagName("config")[0].childNodes;
			for(var i = 0; i < args.length; i++)
			{
				if(args[i].getElementsByTagName("key")[0].innerHTML == key)
				{
					return args[i].getElementsByTagName("value")[0].innerHTML;
				}
			}
		}
		catch(err)
		{
			console.log("Environment arg not retrieved!");
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
		$scope.compareQueryString = "";
		for(var i = 0; i < $scope.testRunsToCompare.length; i++)
		{
			$scope.compareQueryString = $scope.compareQueryString + $scope.testRunsToCompare[i];
			if(i < $scope.testRunsToCompare.length - 1)
			{
				$scope.compareQueryString = $scope.compareQueryString + "+";
			}
		}
	};
	
	$scope.loadTestRuns = function(page, pageSize){
		
		$scope.testRunSearchCriteria.page = page;
		
		if(pageSize)
		{
			$scope.testRunSearchCriteria.pageSize = pageSize;
		}
		
		if($scope.testRunId)
		{
			$scope.testRunSearchCriteria.id = $scope.testRunId;
		}
		else
		{
			$scope.testRunSearchCriteria = ProjectProvider.initProject($scope.testRunSearchCriteria);
		}
		
		if($scope.startedAt)
		{
			$scope.testRunSearchCriteria.date = new Date(Date.parse($scope.startedAt) + OFFSET);
		}
		
		$http.post('tests/runs/search', $scope.testRunSearchCriteria).success(function(data) {
			
			$scope.testRuns = {};
			
			$scope.testRunSearchCriteria.page = data.page;
			$scope.testRunSearchCriteria.pageSize = data.pageSize;
			$scope.totalResults = data.totalResults;
			
			for(var i = 0; i < data.results.length; i ++)
			{
				$scope.addTestRun(data.results[i]);
			}
			
			if($scope.testRunId)
			{
				$scope.loadTests($scope.testRunId);
			}
		}).error(function() {
			console.error('Failed to search test runs');
		});
	};
	
	$scope.loadTests = function(testRunId){
		$scope.lastTestRunOpened = testRunId;
		$scope.testSearchCriteria.testRunId = testRunId;
		$http.post('tests/search', $scope.testSearchCriteria).success(function(data) {
			$scope.userSearchResult = data;
			$scope.testSearchCriteria.page = data.page;
			$scope.testSearchCriteria.pageSize = data.pageSize;
			
			for(var i = 0; i < data.results.length; i ++)
			{
				$scope.addTest(data.results[i], false);
			}
			
		}).error(function() {
			console.error('Failed to search tests');
		});
	};
	
	// --------------------  Context menu ------------------------
	const OPEN_TEST_RUN = ['Open', function ($itemScope) {
        window.open($location.$$absUrl + "?id=" + $itemScope.testRun.id, '_blank');
    }];
	
	const COPY_TEST_RUN_LINK = ['Copy link', function ($itemScope) {
	  	var node = document.createElement('pre');
  	    node.textContent = $location.$$absUrl + "?id=" + $itemScope.testRun.id;
  	    document.body.appendChild(node);
  	    
  	    var selection = getSelection();
  	    selection.removeAllRanges();

  	    var range = document.createRange();
  	    range.selectNodeContents(node);
  	    selection.addRange(range);

  	    document.execCommand('copy');
  	    selection.removeAllRanges();
  	    document.body.removeChild(node);
	}];
	
	const DELETE_TEST_RUN = ['Delete', function ($itemScope) {
	  	$scope.deleteTestRun($itemScope.testRun.id);
    }];
	
	const SEND_EMAIL = ['Send as email', function ($itemScope) {
	  	$scope.openEmailModal($itemScope.testRun);
    }];
	
	const COMMENT = ['Comment', function ($itemScope) {
	  	$scope.openCommentsModal($itemScope.testRun);
    }];
	
	$scope.adminMenuOptions = [
      OPEN_TEST_RUN,
      COPY_TEST_RUN_LINK,
      COMMENT,
      SEND_EMAIL,
      null,
      DELETE_TEST_RUN
    ];
	
	$scope.userMenuOptions = [
      OPEN_TEST_RUN,
      COPY_TEST_RUN_LINK,
      COMMENT,
      SEND_EMAIL
    ];
	// -----------------------------------------------------------
	
	$scope.showDetails = function(id) {
		var testRun = $scope.testRuns[id];
		testRun.showDetails = !testRun.showDetails;
		if(testRun.showDetails)
		{
			$scope.loadTests(id);
		}
	}
	
	$scope.resetSearchCriteria = function(){
		$location.url($location.path());
		$scope.testRunSearchCriteria = {
			'page' : 1,
			'pageSize' : 25
		};
		$scope.startedAt = null;
	};
	
	$scope.populateSearchQuery = function(){
		if($location.search().testSuite)
		{
			$scope.testRunSearchCriteria.testSuite = $location.search().testSuite;
		}
		if($location.search().platform)
		{
			$scope.testRunSearchCriteria.platform = $location.search().platform;
		}
		if($location.search().environment)
		{
			$scope.testRunSearchCriteria.environment = $location.search().environment;
		}
		if($location.search().page)
		{
			$scope.testRunSearchCriteria.page = $location.search().page;
		}
		if($location.search().pageSize)
		{
			$scope.testRunSearchCriteria.pageSize = $location.search().pageSize;
		}
		if($location.search().fromDate)
		{
			$scope.testRunSearchCriteria.fromDateString = $location.search().fromDate;
		}
		if($location.search().toDate)
		{
			$scope.testRunSearchCriteria.toDateString = $location.search().toDate;
		}
	};
	
	$scope.markTestAsPassed = function(id){
		$http.post('tests/' + id + '/passed').success(function(data) {
		}).error(function() {
			console.error('Failed to mark test as passed!');
		});
	};
	
	$scope.openEmailModal = function(testRun){
		$modal.open({
			templateUrl : 'resources/templates/email-details-modal.jsp',
			resolve : {
				'testRun' : function(){
					return testRun;
				}
			},
			controller : function($scope, $modalInstance, testRun){
				
				$scope.title = testRun.testSuite.name;
				$scope.subjectRequired = false;
				$scope.textRequired = false;
				
				$scope.testRun = testRun;
				$scope.email = {};
				
				$scope.sendEmail = function(id){
					$modalInstance.close(0);
					$http.post('tests/runs/' + $scope.testRun.id + '/email', $scope.email).success(function() {
						alertify.success('Email was successfully sent!');
					}).error(function(data, status) {
						alertify.error('Failed to send email');
					});
				};
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		});
	};
	
	$scope.openCommentsModal = function(testRun){
		$modal.open({
			templateUrl : 'resources/templates/comments-modal.jsp',
			resolve : {
				'testRun' : function(){
					return testRun;
				}
			},
			controller : function($scope, $modalInstance, testRun){
				
				$scope.title = testRun.testSuite.name;
				$scope.testRun = testRun;
				
				$scope.addComment = function(){
					var rq = {};
					rq.comment = $scope.testRun.comments;
					$http.post('tests/runs/' + $scope.testRun.id + '/comment', rq).success(function() {
						$modalInstance.close(0);
					}).error(function(data, status) {
						alertify.error('Failed to add comment!');
					});
				};
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		});
	};
	
	$scope.openKnownIssueModal = function(test){
		var modalInstance = $modal.open({
			templateUrl : 'resources/templates/test-known-issues-modal.jsp',
			resolve : {
				'test' : function(){
					return test;
				}
			},
			controller : function($scope, $modalInstance, test){
				
				$scope.knownIssues = {};
				
				$scope.createKnownIssue = function(){
					$http.post('tests/' + test.id + '/issues', $scope.newKnownIssue).success(function() {
						$scope.initNewKnownIssue();
						$scope.getKnownIssues();
						$modalInstance.close(true);
					}).error(function(data, status) {
						alertify.error('Failed to create new known issue');
					});
				};
				
				$scope.deleteKnownIssue = function(id){
					$http.delete('tests/issues/' + id).success(function() {
						$scope.getKnownIssues();
					}).error(function(data, status) {
						alertify.error('Failed to delete known issue');
					});
				};
				
				$scope.getKnownIssues = function(){
					$http.get('tests/' + test.id + '/issues').success(function(issues) {
						$scope.knownIssues = issues;
					}).error(function(data, status) {
						alertify.error('Failed to load known issues');
					});
				};
				
				$scope.initNewKnownIssue = function()
				{
					$scope.newKnownIssue = {};
					$scope.newKnownIssue.type = "BUG";
					$scope.newKnownIssue.testCaseId = test.testCaseId;
				};
				
				$scope.cancel = function(){
					$modalInstance.close(false);
				};
				
				
				(function init(){
					$scope.initNewKnownIssue();
					$scope.getKnownIssues();
				})();
			}
		});
		
		modalInstance.result.then(function (result) {
		    if(result == true)
		    {
		    	$scope.loadTests($scope.lastTestRunOpened);
		    }
		}, function () {});
	};
	
	$scope.$watch('showRealTimeEvents', function() 
	{
		$cookieStore.put("showRealTimeEvents", $scope.showRealTimeEvents);
    });
	
	(function init(){
		if($cookieStore.get("showRealTimeEvents") != null)
		{
			$scope.showRealTimeEvents = $cookieStore.get("showRealTimeEvents");
		}
		$scope.initWebsocket();
		$scope.loadTestRuns(1);
		$scope.populateSearchQuery();
		SettingsService.getSetting("JIRA_URL").then(function(setting) {
			$scope.jiraURL = setting;
		});
	})();
	
}]);

ZafiraApp.controller('TestRunsCompareCtrl', [ '$scope', '$rootScope', '$http', '$routeParams', function($scope, $rootScope, $http, $routeParams) {
	
	$scope.initCompareMatrix = function(){
		$http.get('tests/runs/' + $routeParams.ids + '/compare').success(function(matrix) {
			$scope.matrix = matrix;
			$scope.testNames = [];
			for(var id in matrix)
			{
				for(var name in matrix[id])
				{
					$scope.testNames.push(name);
				}
				break;
			}
			$scope.testNames.sort()
		});
	};
	
	$scope.substring = function(text, size){
		return text.substring(0, size);
	};
	
	(function init(){
		$scope.initCompareMatrix();
	})();
} ]);