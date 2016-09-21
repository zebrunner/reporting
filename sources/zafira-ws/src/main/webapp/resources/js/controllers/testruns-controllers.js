'use strict';

ZafiraApp.controller('TestRunsListCtrl', [ '$scope', '$rootScope', '$http' ,'$location','UtilService', 'ProjectProvider', function($scope, $rootScope, $http, $location, UtilService, ProjectProvider) {

	$scope.UtilService = UtilService;
	$scope.testRunId = $location.search().id;
	
	$scope.testRunsToCompare = [];
	$scope.compareQueryString = "";
	
	$scope.testRuns = {};
	$scope.totalResults = 0;
	
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
  	  var sockJS = new SockJS("/zafira-ws/gs-guide-websocket");
  	  var stomp = Stomp.over(sockJS);
  	  //stomp.debug = null;
  	  stomp.connect({}, function() {
  	      stomp.subscribe("/topic/tests", function(data) 
  	      {
  	        	$scope.getMessage(data.body);
  	      });
  	  });
   };
   
   $scope.getMessage = function(message) {
	 var event = JSON.parse(message.replace(/&quot;/g,'"').replace(/&lt;/g,'<').replace(/&gt;/g,'>'));
	 if(event.type == 'TEST_RUN')
	 {
		if($scope.testRunId && $scope.testRunId != event.testRun.id)
		{
			return;
		}
		$scope.addTestRun(event.testRun);
		$scope.$apply();
	 }
	 else if(event.type == 'TEST')
	 {
		$scope.addTest(event.test, true);
		if($scope.testRuns[event.test.testRunId].showDetails)
		{
			$scope.$apply();
		}
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
	}

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
		
		$http.post('tests/runs/search', ProjectProvider.initProject($scope.testRunSearchCriteria)).success(function(data) {
			
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
	
	$scope.menuOptions = [
      ['Open', function ($itemScope) {
          window.open($location.$$absUrl + "?id=" + $itemScope.testRun.id, '_blank');
      }],
      null,
      ['Copy link', function ($itemScope) {
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
      }]
    ];
	
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
	
	(function init(){
		$scope.initWebsocket();
		$scope.loadTestRuns(1);
		$scope.populateSearchQuery();
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