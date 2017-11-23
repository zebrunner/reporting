(function () {
    'use strict';

    angular
        .module('app.testrun')
        .controller('TestRunListController', ['$scope', '$rootScope', '$location', '$window', '$cookieStore', '$mdDialog', '$mdConstant', '$interval', '$timeout', '$stateParams', 'TestService', 'TestRunService', 'UtilService', 'UserService', 'SettingsService', 'ProjectProvider', 'ConfigService', 'SlackService', 'API_URL', TestRunListController])
        .config(function ($compileProvider) {
            $compileProvider.preAssignBindingsEnabled(true);
        });

    // **************************************************************************
    function TestRunListController($scope, $rootScope, $location, $window, $cookieStore, $mdDialog, $mdConstant, $interval, $timeout, $stateParams, TestService, TestRunService, UtilService, UserService, SettingsService, ProjectProvider, ConfigService, SlackService, API_URL) {

        var OFFSET = new Date().getTimezoneOffset() * 60 * 1000;

        $scope.predicate = 'startTime';
        $scope.reverse = false;

        $scope.UtilService = UtilService;
        $scope.testRunId = $stateParams.id;

        $scope.testRunsToCompare = [];
        $scope.compareQueryString = "";

        $scope.testRuns = {};
        $scope.totalResults = 0;

        $scope.showRealTimeEvents = true;

        $scope.project = ProjectProvider.getProject();

        $scope.showReset = $scope.testRunId != null;
        $scope.selectAll = false;

        $scope.testsStomps = [];

        var DEFAULT_SC = {
            'page': 1,
            'pageSize': 20,
            'reviewed': null
        };

        $scope.sc = angular.copy(DEFAULT_SC);

        $scope.testSearchCriteria = {
            'page': 1,
            'pageSize': 100000
        };

        ConfigService.getConfig("jenkins").then(function(rs) {
            $scope.jenkinsEnabled = rs.data.connected;
        });

        $scope.initStatisticWebsocket = function () {
             var sockJS = new SockJS(API_URL + "/websockets");
             $scope.statisticsStomp = Stomp.over(sockJS);
             $scope.statisticsStomp.connect({withCredentials: false}, function () {
                 $scope.statisticsStomp.subscribe("/topic/statistics", function (data) {
                     $scope.getMessage(data.body);
                 });
             });
        };

        $scope.initTestRunWebsocket = function () {
            var sockJS = new SockJS(API_URL + "/websockets");
            $scope.testRunsStomp = Stomp.over(sockJS);
            //statisticsStomp.debug = null;
            $scope.testRunsStomp.connect({withCredentials: false}, function () {
                $scope.testRunsStomp.subscribe("/topic/testRuns", function (data) {
                    $scope.getMessage(data.body);
                });
            });
        };

        $scope.initTestWebsocket = function (testRunId) {
            var sockJS = new SockJS(API_URL + "/websockets");
            $scope.testsStomps[testRunId] = Stomp.over(sockJS);
            //statisticsStomp.debug = null;
            $scope.testsStomps[testRunId].connect({withCredentials: false}, function () {
                $scope.testsStomps[testRunId].subscribe("/topic/testRuns/" + testRunId + "/tests", function (data) {
                    $scope.getMessage(data.body);
                });
            });
        };

        $scope.disconnectStatisticWebsocket = function () {
            if ($scope.statisticsStomp && $scope.statisticsStomp.connected) {
                $scope.statisticsStomp.disconnect();
            }
        };

        $scope.disconnectTestRunsWebsocket = function () {
            if ($scope.testRunsStomp && $scope.testRunsStomp.connected) {
                $scope.testRunsStomp.disconnect();
            }
        };

        $scope.disconnectTestsWebsocket = function (testRunId) {
            if ($scope.testsStomps[testRunId] && $scope.testsStomps[testRunId].connected) {
                $scope.testsStomps[testRunId].disconnect();
                delete $scope.testsStomps[testRunId];
            }
        };

        $scope.disconnectStomp = function (stomp) {
            if (stomp && stomp.connected) {
                stomp.disconnect();
            }
        };

        $scope.$on('$destroy', function () {
            $scope.disconnectStatisticWebsocket();
            $scope.disconnectTestRunsWebsocket();
            for(var testRunId in $scope.testsStomps) {
                $scope.disconnectStomp($scope.testsStomps[testRunId]);
            }
        });

        $scope.getMessage = function (message) {
            var event = $scope.getEventFromMessage(message);
            switch(event.type) {
                case 'TEST_RUN':
                    if (($scope.testRunId && $scope.testRunId != event.testRun.id)
                        || ($scope.showRealTimeEvents == false && $scope.testRuns[event.testRun.id] == null)
                        || ($scope.project != null && $scope.project.id != event.testRun.project.id)
                        || !$scope.checkSearchCriteria($scope.sc)) {
                        return;
                    }

                    $scope.addTestRun(event.testRun);
                    break;
                case 'TEST':
                    $scope.addTest(event.test);
                    break;
                case 'TEST_RUN_STATISTICS':
                    if($scope.checkStatisticEvent(event)) {
                        return;
                    }
                    var currentTestRun = $scope.testRuns[event.testRunStatistics.testRunId];
                    if(currentTestRun) {
                        currentTestRun.inProgress = event.testRunStatistics.inProgress;
                        currentTestRun.passed = event.testRunStatistics.passed;
                        currentTestRun.failed = event.testRunStatistics.failed;
                        currentTestRun.failedAsKnown = event.testRunStatistics.failedAsKnown;
                        currentTestRun.failedAsBlocker = event.testRunStatistics.failedAsBlocker;
                        currentTestRun.skipped = event.testRunStatistics.skipped;
                        currentTestRun.reviewed = event.testRunStatistics.reviewed;
                    }
                    break;
                default:
                    break;
            }
            $scope.$apply();
            return true;
        };

        $scope.getEventFromMessage = function (message) {
            return JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
        };

        $scope.checkStatisticEvent = function (event) {
            return ($scope.testRunId && $scope.testRunId != event.testStatistic.testRunId)
            || ($scope.showRealTimeEvents == false && $scope.testRuns[event.testStatistic.testRunId] == null)
            || ($scope.project != null)
            || !$scope.checkSearchCriteria($scope.sc);
        };

        $scope.checkSearchCriteria = function (sc) {
            var isEmpty = true;
            for (var criteria in sc) {
                if ( sc.hasOwnProperty(criteria) && sc[criteria] != null && sc[criteria] != "" && criteria != "page" && criteria != "pageSize") {
                    isEmpty = false;
                    break;
                }
            }
            return isEmpty;
        };


        $scope.addTest = function (test) {

            test.elapsed = test.finishTime != null ? (test.finishTime - test.startTime) : Number.MAX_VALUE;

            var testRun = $scope.testRuns[test.testRunId];
            if (testRun == null) {
                return;
            }

            testRun.tests[test.id] = test;
        };

        $scope.selectedTestRuns = {};

        $scope.getLengthOfSelectedTestRuns = function () {
            var count = 0;
            for(var id in $scope.selectedTestRuns) {
                if($scope.selectedTestRuns.hasOwnProperty(id)) {
                    count++;
                }
            }
            return count;
        };

       $scope.addToSelectedTestRuns = function (testRun) {
           $timeout(function () {
               if(testRun.selected) {
                   $scope.selectedTestRuns[testRun.id] = testRun;
               } else {
                   delete $scope.selectedTestRuns[testRun.id];
               }
           }, 100);
       };

        $scope.addToSelectedTestRunsAll = function () {
            $timeout(function () {
                for(var id in $scope.testRuns) {
                    $scope.addToSelectedTestRuns($scope.testRuns[id]);
                }
            }, 100);
        };

       $scope.followSelectedTestRuns = function () {
           var count = $scope.getLengthOfSelectedTestRuns();
           for(var id in $scope.selectedTestRuns) {
               $scope.selectedTestRuns[id].followed = true;
           }
           var messageText = '';
           switch(count) {
               case 0:
                   messageText = 'Select test runs for follow them';
                   break;
               case 1:
                   messageText = 'Selected test run will be followed';
                   break;
               default:
                   messageText = 'Selected ' + count + ' test runs will be followed';
                   break;
           }
           alertify.warning(messageText);
       };

        $scope.batchRerun = function()
        {
        		$scope.selectAll = false;
            var 	rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
	        	for(var id in $scope.testRuns)
	    		{
	        		if($scope.testRuns[id].selected)
	        		{
	        			$scope.rebuild($scope.testRuns[id], rerunFailures);
	        		}
	    		}
        };

        $scope.batchDelete = function()
        {
        		$scope.selectAll = false;
	        	for(var id in $scope.testRuns)
	    		{
	        		if($scope.testRuns[id].selected)
	        		{
	        			$scope.deleteTestRun(id, true);
	        		}
	    		}
        };

        $scope.deleteTestRun = function (id, confirmation)

         {
        		if(confirmation == null)
        		{
        			confirmation = confirm('Do you really want to delete "' + $scope.testRuns[id].testSuite.name + '" test run?');
        		}
            if (confirmation)
            {
                TestRunService.deleteTestRun(id).then(function(rs) {
                    if(rs.success)
                    {
                        delete $scope.testRuns[id];
                        alertify.success('Test run #' + id + ' removed');
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
        };

        $scope.addTestRun = function (testRun) {
            testRun.expand = $scope.testRunId ? true : false;
            if ($scope.testRuns[testRun.id] == null) {
                testRun.jenkinsURL = testRun.job.jobURL + "/" + testRun.buildNumber;
                testRun.UID = testRun.testSuite.name + " " + testRun.jenkinsURL;
                testRun.tests = {};
                $scope.testRuns[testRun.id] = testRun;
            }
            else {
                $scope.testRuns[testRun.id].status = testRun.status;
                $scope.testRuns[testRun.id].reviewed = testRun.reviewed;
            }
            ConfigService.getConfig("slack/" + testRun.id).then(function successCallback(rs){
                $scope.testRuns[testRun.id].isSlackAvailable = rs.available;
            });
        };

        $scope.getArgValue = function (xml, key) {
            try {
                var xmlDoc = new DOMParser().parseFromString(xml, "text/xml");
                var args = xmlDoc.getElementsByTagName("config")[0].childNodes;
                for (var i = 0; i < args.length; i++) {
                    if (args[i].getElementsByTagName("key")[0].innerHTML == key) {
                        return args[i].getElementsByTagName("value")[0].innerHTML;
                    }
                }
            }
            catch (err) {
                //console.log("Environment arg not retrieved!");
            }
            return null;
        };

        $scope.search = function (page, pageSize) {
            $scope.sc.date = null;
            $scope.sc.toDate = null;
            $scope.sc.fromDate = null;
            $scope.selectAll = false;

            $scope.sc.page = page;

            if (pageSize) {
                $scope.sc.pageSize = pageSize;
            }

            if ($scope.testRunId) {
                $scope.sc.id = $scope.testRunId;
            }
            else {
                $scope.sc = ProjectProvider.initProject($scope.sc);
            }

            if ($scope.startedAt) {
                $scope.sc.date = new Date(Date.parse($scope.startedAt) + OFFSET);
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

            TestRunService.searchTestRuns($scope.sc).then(function(rs) {
                if(rs.success)
                {
                    var data = rs.data;

                    $scope.sr = rs.data;

                    $scope.testRuns = {};
                    $scope.selectedTestRuns = {};

                    $scope.sc.page = data.page;
                    $scope.sc.pageSize = data.pageSize;
                    $scope.totalResults = data.totalResults;

                    for (var i = 0; i < data.results.length; i++) {
                        var testRun = data.results[i];
                        var browserVersion = $scope.splitPlatform(data.results[i].platform);
                        testRun.browserVersion = browserVersion;
                        $scope.addTestRun(testRun);
                        if (testRun.status == 'IN_PROGRESS') {
                            $scope.loadTests(testRun.id);
                        }
                    }
                    if ($scope.testRunId) {
                        $scope.loadTests($scope.testRunId);
                    }
                }
                else
                {
                    console.error(rs.message);
                }
            });
        };


        $scope.loadTests = function (testRunId) {
            $scope.lastTestRunOpened = testRunId;
            $scope.testSearchCriteria.testRunId = testRunId;
            TestService.searchTests($scope.testSearchCriteria).then(function(rs) {
                if(rs.success)
                {
                    var data = rs.data;
                    $scope.userSearchResult = data;
                    $scope.testSearchCriteria.page = data.page;
                    $scope.testSearchCriteria.pageSize = data.pageSize;
                    var inProgressTests = 0;
                    var testRun = $scope.testRuns[testRunId];
                    for (var i = 0; i < data.results.length; i++) {
                        var test = data.results[i];
                        if (test.status == 'IN_PROGRESS') {
                            inProgressTests++;
                        }
                        $scope.addTest(test);
                    }
                    testRun.inProgress = inProgressTests;
                }
                else
                {
                    console.error(rs.message);
                }
            });
        };

        // --------------------  Context menu ------------------------

        $scope.openTestRun = function (testRun) {
            if ($location.$$path != $location.$$url){
                $location.search({});
            }
            if ($location.$$absUrl.match(new RegExp(testRun.id, 'gi')) == null){
                window.open($location.$$absUrl + "/" + testRun.id, '_blank');
            }
        };

        $scope.copyLink = function (testRun) {
            var node = document.createElement('pre');
            node.textContent = $location.$$absUrl + "/" + testRun.id;
            document.body.appendChild(node);

            var selection = getSelection();
            selection.removeAllRanges();

            var range = document.createRange();
            range.selectNodeContents(node);
            selection.addRange(range);

            document.execCommand('copy');
            selection.removeAllRanges();
            document.body.removeChild(node);
        };

        $scope.markAsReviewed = function (testRun) {
            $scope.showCommentsDialog(testRun);
        };

        $scope.sendAsEmail = function (testRun, event) {
            $scope.showEmailDialog(testRun, event);
        };

        $scope.export = function (testRun) {
            TestRunService.exportTestRunResultsHTML(testRun.id).then(function(rs) {
                if(rs.success)
                {
                    var html = new Blob([rs.data], {type: 'html'});
                    var link = document.createElement("a");
                    document.body.appendChild(link);
                    link.style = "display: none";
                    var url = window.URL.createObjectURL(html);
                    link.href = url;
                    link.download = testRun.testSuite.name.split(' ').join('_') + ".html";
                    link.click();
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.notifyInSlack = function (testRun) {
            SlackService.triggerReviewNotif(testRun.id);
        };

        $scope.buildNow = function (testRun, event) {
            $scope.showBuildNowDialog(testRun, event);
        };

        $scope.rerun = function (testRun, event) {
            $scope.showRerunDialog(testRun, event);
        };

        $scope.$watch('selectAll', function(newValue, oldValue) {
        		for(var id in $scope.testRuns)
        		{
        			$scope.testRuns[id].selected = newValue;
        		}
        	});

        $scope.abort = function (testRun) {
            if($scope.jenkinsEnabled) {
                TestRunService.abortCIJob(testRun.id).then(function (rs) {
                    if(rs.success)
                    {
                        TestRunService.abortTestRun(testRun.id, testRun.ciRunId).then(function(rs) {
                            if(rs.success){
                                testRun.status = 'ABORTED';
                                alertify.success("Testrun " + testRun.testSuite.name + " is aborted");
                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
            } else {
                alertify.error('Unable connect to jenkins');
            }
        };

        $scope.abortSelectedTestRuns = function () {
            if($scope.jenkinsEnabled) {
                for (var id in $scope.selectedTestRuns) {
                    if ($scope.selectedTestRuns[id].status == 'IN_PROGRESS') {
                        $scope.abort($scope.selectedTestRuns[id]);
                    }
                }
            } else {
                alertify.error('Unable connect to jenkins');
            }
        };

        $scope.rebuild = function (testRun, rerunFailures) {
            if ($scope.jenkinsEnabled) {
            		if(rerunFailures == null)
            		{
            			rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
            		}
                TestRunService.rerunTestRun(testRun.id, rerunFailures).then(function(rs) {
                    if(rs.success)
                    {
                    		testRun.status = 'IN_PROGRESS';
                    		alertify.success("Rebuild triggered in CI service");
                    }
                    else
                    {
                         alertify.error(rs.message);
                    }
                });
            }
            else {
                window.open(testRun.jenkinsURL + '/rebuild/parameterized', '_blank');
            }
        };

        $scope.deleteTestRunAction = function (testRun) {
            $scope.deleteTestRun(testRun.id);
        };

        $scope.initMenuRights = function (testRun) {
            $scope.showNotifyInSlackOption = testRun.isSlackAvailable && testRun.reviewed != null && testRun.reviewed;
            $scope.showBuildNowOption = $scope.isConnectedToJenkins;
            $scope.showDeleteTestRunOption = true/*$rootScope.currentRole == 'ROLE_ADMIN'*/;
        };

        // -----------------------------------------------------------

        $scope.isConnectedToJenkins = false;
        $scope.getJenkinsConnection = function() {
            ConfigService.getConfig("jenkins").then(function (rs) {
                $scope.isConnectedToJenkins = rs.data.connected;
            });
        };

        $scope.isUrlContainsJenkinsHost = function(url, testRun) {
            if(url && testRun.job && url.includes(testRun.job.jenkinsHost)) {
                return true;
            }
            return false;
        };

        $scope.showDetails = function (id) {
            var testRun = $scope.testRuns[id];
            testRun.showDetails = !testRun.showDetails;
            if (testRun.showDetails) {
                $scope.loadTests(id);
            }
        };

        $scope.loadEnvironments = function () {
            TestRunService.getEnvironments().then(function(rs) {
                if(rs.success)
                {
                    $scope.environments = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.loadPlatforms = function () {
            TestRunService.getPlatforms().then(function(rs) {
                if(rs.success)
                {
                    $scope.platforms = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.resetSearchCriteria = function () {
            $location.url($location.path());
            $scope.sc = {
                'page': 1,
                'pageSize': 20
            };
            $scope.startedAt = null;
            $scope.showReset = false;
            $scope.selectAll = false;
        };

        $scope.populateSearchQuery = function () {
            if ($location.search().testSuite) {
                $scope.sc.testSuite = $location.search().testSuite;
            }
            if ($location.search().platform) {
                $scope.sc.platform = $location.search().platform;
            }
            if ($location.search().environment) {
                $scope.sc.environment = $location.search().environment;
            }
            if ($location.search().page) {
                $scope.sc.page = $location.search().page;
            }
            if ($location.search().pageSize) {
                $scope.sc.pageSize = $location.search().pageSize;
            }
            if ($location.search().fromDate) {
                $scope.sc.fromDateString = $location.search().fromDate;
            }
            if ($location.search().toDate) {
                $scope.sc.toDateString = $location.search().toDate;
            }
        };

        $scope.markTestAsPassed = function (id) {
            TestService.markTestAsPassed(id).then(function(rs) {
                if(rs.success)
                {
                }
                else
                {
                    console.error(rs.message);
                }
            });
        };

        $scope.showCodeDialog = function(event, testRun) {
            $mdDialog.show({
                controller: CodeController,
                templateUrl: 'app/_testruns/code_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRun: testRun
                }
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showBuildNowDialog = function(testRun, event) {
            $mdDialog.show({
                controller: BuildNowController,
                templateUrl: 'app/_testruns/build_now_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRun: testRun
                }
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showRerunDialog = function (testRun, event) {
            $mdDialog.show({
                controller: TestRunRerunController,
                templateUrl: 'app/_testruns/testrun_rerun_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    testRun: testRun
                }
            })
                .then(function (answer) {
                }, function () {
                });
        };

        $scope.showEmailDialog = function(testRun, event) {
            $mdDialog.show({
                controller: EmailController,
                templateUrl: 'app/_testruns/email_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRun: testRun
                }
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showCommentsDialog = function(testRun, event) {
            $mdDialog.show({
                controller: CommentsController,
                templateUrl: 'app/_testruns/comments_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRun: testRun
                }
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showKnownIssueDialog = function(test, isNew, event) {
            $mdDialog.show({
                controller: KnownIssueController,
                templateUrl: 'app/_testruns/known_issue_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    test: test,
                    isNew: isNew
                }
            })
                .then(function(answer) {
                    if (answer == true) {
                        $scope.loadTests($scope.lastTestRunOpened);
                    }
                }, function() {
                });
        };



        $scope.showAssignJiraTaskDialog = function(testTask, isNewTask, event) {
                    $mdDialog.show({
                        controller: TaskController,
                        templateUrl: 'app/_testruns/task_assign_modal.html',
                        parent: angular.element(document.body),
                        targetEvent: event,
                        clickOutsideToClose:true,
                        fullscreen: true,
                        locals: {
                            testTask: testTask,
                            isNewTask: isNewTask
                        }
                    })
                        .then(function(answer) {
                            if (answer == true) {
                                $scope.loadTests($scope.lastTestRunOpened);
                            }
                        }, function() {
                        });
                };



//        $scope.$watch('showRealTimeEvents', function () {
//            $cookieStore.put("showRealTimeEvents", $scope.showRealTimeEvents);
//        });


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

        $scope.switchTestRunExpand = function (testRun, fromTestRun) {
            if(hasRightsToExpand(!testRun.expand, fromTestRun)) {
                if (!testRun.expand) {
                    if ((!testRun.tests || getJSONLength(testRun.tests) === 0) || testRun.status === 'IN_PROGRESS') {
                        $scope.loadTests(testRun.id);
                    }
                    $scope.initTestWebsocket(testRun.id);
                    testRun.expand = true;
                } else {
                    testRun.expand = false;
                    $scope.disconnectTestsWebsocket(testRun.id);
                }
            }
        };

        var hasRightsToExpand = function (forceTrue, fromTestRun) {
            if(!fromTestRun) {
                var selectedText = window.getSelection().toString();
                var unexpectedTokens = ['\n', '\t', ''];
                if (forceTrue) return true;
                for (var i = 0; i < unexpectedTokens.length; i++) {
                    if (unexpectedTokens[i] === selectedText.trim()) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        };

        var getJSONLength = function(jsonObj) {
            var count = 0;
            for(var id in jsonObj) {
                count++;
            }
            return count;
        };

        $scope.splitPlatform = function (string) {
	    if (string == null) {
		return null;
	    }
            var array = string.split(' ');
            var version = "v." + array[1];
            if (array.length == 2) {
                return version;
            }
            else {
                return null;
            }
        };

        $scope.reset = function () {
            $scope.sc = angular.copy(DEFAULT_SC);
            $location.search({});
            $scope.search();
        };

        var toSc = function (qParams) {
            $scope.sc = qParams;
        };

        $scope.onChangeCriteria = function () {
            for(var criteria in $scope.sc) {
                if(!$scope.sc[criteria] || !$scope.sc[criteria].length) {
                    delete $scope.sc[criteria];
                }
            }
            $location.search($scope.sc);
        };

        (function init() {

            toSc($location.search());
            $scope.initStatisticWebsocket();
            $scope.initTestRunWebsocket();
            $scope.search(1);
            $scope.populateSearchQuery();
            $scope.loadEnvironments();
            $scope.loadPlatforms();
            $scope.getJenkinsConnection();

            SettingsService.getSettingByName("JIRA_URL").then(function(rs) {
                if(rs.success)
                {
                	 $scope.jiraURL = rs.data;
                }
            });

        })();
    }

    // *** Modals Controllers ***
    function BuildNowController($scope, $mdDialog, TestRunService, testRun) {
        $scope.title = testRun.testSuite.name;
        $scope.textRequired = false;

        $scope.testRun = testRun;

        $scope.buildNow = function () {
            $scope.hide();
            var jobParametersMap = {};
            for (var i = 0; i < $scope.jobParameters.length; i++){
                jobParametersMap[$scope.jobParameters[i].name] = $scope.jobParameters[i].value;
            }
            TestRunService.buildTestRun($scope.testRun.id, jobParametersMap).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('CI job is building, it may take some time before status is updated');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.jobParameters = [];
        $scope.isJobParametersLoaded = false;
        $scope.noValidJob = false;
        $scope.getJobParameters = function () {
            TestRunService.getJobParameters($scope.testRun.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.jobParameters = rs.data;
                    for (var i = 0; i < $scope.jobParameters.length; i++){
                        if ($scope.jobParameters[i].parameterClass == 'BOOLEAN'){
                            if ($scope.jobParameters[i].value == 'true'){
                                $scope.jobParameters[i].value = true;
                            }
                            else if ($scope.jobParameters[i].value == 'false') {
                                $scope.jobParameters[i].value = false;
                            }
                        }
                    }
                    $scope.isJobParametersLoaded = true;
                    $scope.noValidJob = $scope.jobParameters == '';
                }
                else
                {
                    $scope.hide();
                    alertify.error(rs.message);
                }
            });
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {
            $scope.getJobParameters();
        })();
    }

    function EmailController($scope, $mdDialog, $mdConstant, UserService, TestRunService, testRun) {
        $scope.title = testRun.testSuite.name;
        $scope.subjectRequired = false;
        $scope.textRequired = false;

        $scope.testRun = testRun;
        $scope.email = {};
        $scope.email.recipients = [];
        $scope.users = [];
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SPACE, $mdConstant.KEY_CODE.SEMICOLON];

        $scope.sendEmail = function (id) {
            if($scope.users.length == 0) {
            	alertify.error('Add a recipient!')
                return;
            }
            $scope.hide();
            $scope.email.recipients = $scope.email.recipients.toString();
            TestRunService.sendTestRunResultsEmail($scope.testRun.id, $scope.email).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('Email was successfully sent!');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.users_all = [];
        var currentText;

        $scope.usersSearchCriteria = {};
        $scope.asyncContacts = [];
        $scope.filterSelected = true;

        $scope.querySearch = querySearch;
        var stopCriteria = '########';
        function querySearch (criteria, user) {
            $scope.usersSearchCriteria.email = criteria;
            currentText = criteria;
            if(!criteria.includes(stopCriteria)) {
                stopCriteria = '########';
                return UserService.searchUsersWithQuery($scope.usersSearchCriteria, criteria).then(function(rs) {
                    if(rs.success)
                    {
                        if (! rs.data.results.length) {
                            stopCriteria = criteria;
                        }
                        return rs.data.results.filter(searchFilter(user));
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
            return "";
        }

        function searchFilter(u) {
            return function filterFn(user) {
                var users = u;
                for(var i = 0; i < users.length; i++) {
                    if(users[i].id == user.id) {
                        return false;
                    }
                }
                return true;
            };
        }

        $scope.checkAndTransformRecipient = function (currentUser) {
            var user = {};
            if (currentUser.username) {
                user = currentUser;
                $scope.email.recipients.push(currentUser.email);
                $scope.users.push(user);
            } else {
                user.email = currentUser;
                $scope.email.recipients.push(user.email);
                $scope.users.push(user);
            }
            return user;
        };
        $scope.removeRecipient = function (user) {
            var index = $scope.email.recipients.indexOf(user.email);
            if (index >= 0) {
                $scope.email.recipients.splice(index, 1);
            }
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {

        })();
    }

    function CommentsController($scope, $mdDialog, TestRunService, testRun) {
        $scope.title = testRun.testSuite.name;
        $scope.testRun = testRun;

        $scope.markReviewed = function(){
            var rq = {};
            rq.comment = $scope.testRun.comments;
            if((rq.comment == null || rq.comment == "") && ((testRun.failed > 0 && testRun.failed > testRun.failedAsKnown) || testRun.skipped > 0))
            {
                alertify.error('Unable to mark as Reviewed test run with failed/skipped tests without leaving some comment!');
            }
            else
            {
                TestRunService.markTestRunAsReviewed($scope.testRun.id, rq).then(function(rs) {
                    if(rs.success)
                    {
                        $scope.hide();
                        alertify.success('Test run #' + $scope.testRun.id + ' marked as reviewed');
                        if ($scope.testRun.isSlackAvailable)
                        {
                            if(confirm("Would you like to post latest test run status to slack?"))
                            {
                                SlackService.triggerReviewNotif($scope.testRun.id);
                            }
                        }
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {

        })();
    }

    function TestRunRerunController($scope, $mdDialog, TestRunService, testRun, ConfigService) {

        $scope.rerunFailures = true;
        $scope.testRun = testRun;

        $scope.rebuild = function (testRun, rerunFailures) {
            if ($scope.jenkinsEnabled) {
                TestRunService.rerunTestRun(testRun.id, rerunFailures).then(function(rs) {
                    if(rs.success)
                    {
                        testRun.status = 'IN_PROGRESS';
                        alertify.success("Rebuild triggered in CI service");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
            else {
                window.open(testRun.jenkinsURL + '/rebuild/parameterized', '_blank');
            }
            $scope.hide(true);
         };

        ConfigService.getConfig("jenkins").then(function(rs) {
            $scope.jenkinsEnabled = rs.data.connected;
        });

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        (function initController() {
        })();
    }


    function KnownIssueController($scope, $mdDialog, $interval, SettingsService, TestService, ConfigService, test, isNew) {
        $scope.jiraId;
        $scope.isConnectedToJira = false;
        $scope.isIssueFound = true;
        $scope.isDataLoaded = false;
        $scope.isFieldsDisabled = true;
        $scope.isJiraIdExists = true;
        $scope.isJiraIdClosed = false;

        $scope.createKnownIssue = function () {
            var knownIssue = $scope.newKnownIssue;
            TestService.createTestKnownIssue(test.id, knownIssue).then(function(rs) {
                if(rs.success)
                {
                    $scope.getKnownIssues();
                    $scope.hide();
                    if(isNew)
                        alertify.success('A new known issue "' + knownIssue.jiraId + '" was created');
                    else
                        alertify.success('A known issue "' + knownIssue.jiraId + '" was updated');
                    $scope.initNewKnownIssue();
                }
                else
                {
                    if(isNew)
                        alertify.error('Failed to create new known issue');
                    else
                        alertify.error('Failed to update known issue');
                }
            });
        };


        $scope.checkKnowIssue = function () {
            $scope.getRightToSearch();
            if ($scope.isRightToSearch && $scope.isConnectedToJira) {
                $scope.isIssueFound = false;
                TestService.getJiraIssue($scope.newKnownIssue.jiraId).then(function(rs) {
                    if(rs.success)
                    {
                        var issue = rs.data;
                        $scope.isIssueFound = true;
                        checkIssueStatus(issue);
                        if ($scope.isJiraIdExists) {
                            $scope.newKnownIssue.description = issue.summary;
                            $scope.newKnownIssue.assigneeMessage = 'Assigned to ' + issue.assignee.name + ' by ' + issue.reporter.name;
                            $scope.newKnownIssue.status = issue.status.name;
                        }
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
        };

        var checkIssueStatus = function (issue) {
            if (issue == '') {
                $scope.isJiraIdClosed = false;
                $scope.isJiraIdExists = false;
                return;
            }
            $scope.checkStatusAsClosed(issue.status.name);
            if($scope.isJiraIdClosed) {
                $scope.isJiraIdExists = true;
            }
            else {
                // Reset flags
                $scope.isJiraIdExists = true;
            }
        };

        $scope.isRightToSearch = false;


        $scope.getRightToSearch = function () {
            if ($scope.newKnownIssue.jiraId == null || !fieldIsChanged) {
                $scope.isRightToSearch = false;
                $scope.isIssueFound = true;
            } else {
                $scope.isRightToSearch = true;
                fieldIsChanged = false;
            }
        };

        var fieldIsChanged = false;

        $scope.onChangeAction = function () {
            fieldIsChanged = true;
            // Reset flags
            $scope.newKnownIssue.description = '';
            $scope.newKnownIssue.id = null;
            $scope.newKnownIssue.status = null;
            $scope.newKnownIssue.assigneeMessage = null;
            $scope.isJiraIdExists = true;
            $scope.isJiraIdClosed = false;
            $scope.isIssueFound = false;

            var existIssue =  $scope.knownIssues.filter(function (knownIssue) {
                var issueExists = knownIssue.jiraId == $scope.newKnownIssue.jiraId;
                $scope.isNew = ! (issueExists);
                return issueExists;
            })[0];
            if(existIssue)
                angular.copy(existIssue, $scope.newKnownIssue);
        };

        $scope.selectCurrentIssue = function(issue) {
            checkTestHasIssues();
            $scope.isNew = ! (issue.jiraId == $scope.testBugIssue.jiraId);
            $scope.newKnownIssue.id = issue.id;
            $scope.newKnownIssue.jiraId = issue.jiraId;
            $scope.newKnownIssue.description = issue.description;
            $scope.newKnownIssue.status = issue.status.name;
        };

        $interval(function () {
            $scope.checkKnowIssue();
        }, 2000);

        $scope.deleteKnownIssue = function (id) {
            TestService.deleteTestKnownIssue(test.id, id).then(function(rs) {
                if(rs.success)
                {
                    $scope.initNewKnownIssue();
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.getKnownIssues = function () {
            TestService.getTestKnownIssues(test.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.knownIssues = rs.data;
                    if(test.workItems.length && ! isNew)
                        angular.copy($scope.testBugIssue, $scope.newKnownIssue);

                        if(test.workItems.length == 0) test.has ==false;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        function checkTestHasIssues() {
            $scope.testHasKnownIssues = test.workItems.filter(function (item) {
                return item.type == 'BUG';
            }).length;
        }

        function getTestBugIssue() {
            $scope.testBugIssue = {};
            $scope.testBugIssue.jiraId = '';
            if($scope.testHasKnownIssues)
                $scope.testBugIssue = test.workItems.filter(function (item) {
                    return item.type == 'BUG';
                })[0];
        }

        $scope.initNewKnownIssue = function () {
            $scope.isNew = isNew;
            $scope.newKnownIssue = {};
            $scope.newKnownIssue.type = "BUG";
            $scope.newKnownIssue.testCaseId = test.testCaseId;
        };

        $scope.cancel = function () {
            $scope.hide();
        };

        $scope.getJiraStatusesAsClosed = function() {
            SettingsService.getSetting('JIRA_CLOSED_STATUS').then(function successCallback(rs) {
                $scope.jiraStatusesAsClosed = rs.data.split(';');
            }, function errorCallback(data) {
                console.error(data);
            });
        };

        $scope.checkStatusAsClosed = function (status) {
            var newAr = $scope.jiraStatusesAsClosed.filter(function (jiraClosedStatus) {
                return jiraClosedStatus.toLowerCase() == status.toLowerCase();
            });
            $scope.isJiraIdClosed = newAr.length != 0;
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {
            ConfigService.getConfig("jira").then(function(rs) {
                $scope.isConnectedToJira = rs.data.connected;
                $scope.isDataLoaded = true;
                $scope.isFieldsDisabled = false;
            });
            checkTestHasIssues();
            getTestBugIssue();
            $scope.initNewKnownIssue();
            $scope.getKnownIssues();
            $scope.getJiraStatusesAsClosed();
        })();
    }


 function TaskController($scope, $mdDialog, $interval, SettingsService, TestService, ConfigService, testTask, isNewTask) {
        $scope.jiraId;
        $scope.isConnectedToJira = false;
        $scope.isIssueFound = true;
        $scope.isDataLoaded = false;
        $scope.isFieldsDisabled = true;
        $scope.isJiraIdExists = true;
        $scope.isJiraIdClosed = false;

        $scope.assignWorkItem = function () {
                    var taskIssue = $scope.newTaskIssue;
                    TestService.assignTestJiraTask(testTask.id, taskIssue).then(function(rs) {
                        if(rs.success)
                        {
                            $scope.hide();
                            if(isNewTask)
                                alertify.success('A new work item "' + taskIssue.jiraId + '" was assigned');
                            else{
                               var index = testTask.workItems.indexOf(taskIssue);
                                testTask.workItems.splice(index, 1);
                                alertify.success('A new work item "' + taskIssue.jiraId + '" was updated');
                                }
                          testTask.workItems.push(rs.data);
                        }
                        else
                        {
                            if(isNewTask)
                                alertify.error('Failed to assign "' + taskIssue.jiraId + '" new work item');
                            else
                                alertify.error('Failed to update "' + taskIssue.jiraId + '" work item');
                        }
                    });
                };


                $scope.deleteWorkItem = function () {
                                    var taskIssue = $scope.newTaskIssue;
                                    TestService.deleteTestJiraTask(testTask.id, taskIssue.id).then(function(rs) {
                                        if(rs.success)
                                        {
                                        var index = testTask.workItems.indexOf(taskIssue);
                                        testTask.workItems.splice(index, 1);
                                        $scope.hide();
                                        alertify.success('A work item "' + taskIssue.jiraId + '" was unassigned');
                                        }
                                        else
                                        alertify.error('Failed to unassign "' + taskIssue.jiraId + '" work item');

                                    });
                                };


        $scope.checkTaskIssue = function () {
            $scope.getRightToSearch();
            if ($scope.isRightToSearch && $scope.isConnectedToJira) {
                $scope.isIssueFound = false;
                TestService.getJiraIssue($scope.newTaskIssue.jiraId).then(function(rs) {
                    if(rs.success)
                    {
                        var issue = rs.data;
                        $scope.isIssueFound = true;
                        checkIssueStatus(issue);
                        if ($scope.isJiraIdExists) {
                            $scope.newTaskIssue.description = issue.summary;
                            $scope.newTaskIssue.assigneeMessage = 'Assigned to ' + issue.assignee.name + ' by ' + issue.reporter.name;
                            $scope.newTaskIssue.status = issue.status.name;
                        }
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
        };

        var checkIssueStatus = function (issue) {
            if (issue == '') {
                $scope.isJiraIdClosed = false;
                $scope.isJiraIdExists = false;
                return;
            }
            $scope.checkStatusAsClosed(issue.status.name);
            if($scope.isJiraIdClosed) {
                $scope.isJiraIdExists = true;
            }
            else {
                // Reset flags
                $scope.isJiraIdExists = true;
            }
        };

        $scope.isRightToSearch = false;


        $scope.getRightToSearch = function () {
            if ($scope.newTaskIssue.jiraId == null || !fieldIsChanged) {
                $scope.isRightToSearch = false;
                $scope.isIssueFound = true;
            } else {
                $scope.isRightToSearch = true;
                fieldIsChanged = false;
            }
        };

        var fieldIsChanged = false;

        $scope.onTaskChangeAction = function () {
            fieldIsChanged = true;
            // Reset flags
            $scope.newTaskIssue.description = '';
            $scope.newTaskIssue.id = null;
            $scope.newTaskIssue.status = null;
            $scope.newTaskIssue.assigneeMessage = null;
            $scope.isJiraIdExists = true;
            $scope.isJiraIdClosed = false;
            $scope.isIssueFound = false;

        };

        $scope.selectCurrentIssue = function(issue) {
            checkTestHasIssues();
            $scope.isNewTask = ! (issue.jiraId == $scope.testTaskIssue.jiraId);
            $scope.newTaskIssue.id = issue.id;
            $scope.newTaskIssue.jiraId = issue.jiraId;
            $scope.newTaskIssue.description = issue.description;
            $scope.newTaskIssue.status = issue.status.name;
        };

        $interval(function () {
            $scope.checkTaskIssue();
        }, 2000);



        function checkTestHasIssues() {
            $scope.testHasTaskIssues = testTask.workItems.filter(function (item) {
                return item.type == 'TASK';
            }).length;
        }

        function getTestTaskIssue() {
            $scope.testTaskIssue = {};
            $scope.testTaskIssue.jiraId = '';
            if($scope.testHasTaskIssues)
                $scope.testTaskIssue = testTask.workItems.filter(function (item) {
                    return item.type == 'TASK';
                })[0];
        }

        $scope.initNewTaskIssue = function () {
            $scope.isNewTask = isNewTask;
            $scope.newTaskIssue = {};
            $scope.newTaskIssue.type = "TASK";
            $scope.newTaskIssue.testCaseId = testTask.testCaseId;
        };

        $scope.cancel = function () {
            $scope.hide();
        };

        $scope.getJiraStatusesAsClosed = function() {
            SettingsService.getSetting('JIRA_CLOSED_STATUS').then(function successCallback(rs) {
                $scope.jiraStatusesAsClosed = rs.data.split(';');
            }, function errorCallback(data) {
                console.error(data);
            });
        };

        $scope.checkStatusAsClosed = function (status) {
            var newAr = $scope.jiraStatusesAsClosed.filter(function (jiraClosedStatus) {
                return jiraClosedStatus.toLowerCase() == status.toLowerCase();
            });
            $scope.isJiraIdClosed = newAr.length != 0;
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {
            ConfigService.getConfig("jira").then(function(rs) {
                $scope.isConnectedToJira = rs.data.connected;
                $scope.isDataLoaded = true;
                $scope.isFieldsDisabled = false;
            });
            checkTestHasIssues();
            getTestTaskIssue();
            $scope.initNewTaskIssue();
            $scope.getJiraStatusesAsClosed();
            angular.copy(testTask.workItems.filter(function(workItem) {
                return workItem.type === 'TASK';
            })[0], $scope.newTaskIssue);
        })();
    }


    function CodeController($scope, $mdDialog, $interval, testRun, TestRunService) {

        $scope.content = '';
        $scope.count = 100;
        $scope.fullCount = 0;

        $scope.getContent = function () {
            TestRunService.getConsoleOutput(testRun.id, $scope.count, $scope.fullCount).then(function(rs) {
                if(rs.success)
                {
                    for(var fullCount in rs.data) {
                        if(fullCount === '-1')
                        {
                            stopInterval();
                            break;
                        }
                        $scope.content = $scope.content.concat(rs.data[fullCount]);
                        $scope.fullCount = fullCount;
                    }
                }
                else
                {
                    alertify.error(rs.message);
                    $scope.hide();
                }
            });
        };

        var interval;

        var startInterval = function () {
            interval = $interval(function() {
                $scope.getContent();
            }, 8000);
        };

        var stopInterval = function () {
            $interval.cancel(interval);
            interval = undefined;
        };
        $scope.$on('$destroy', function() {
            stopInterval();
        });

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {
            $scope.getContent();
            startInterval();
        })();
    }
})();
