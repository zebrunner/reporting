(function () {
    'use strict';

    angular
        .module('app.testrun')
        .controller('TestRunListController', ['$scope', '$rootScope', '$location', '$window', '$cookieStore', '$mdDialog', '$mdConstant', '$interval', '$timeout', '$stateParams', '$mdDateRangePicker', 'TestService', 'TestRunService', 'UtilService', 'UserService', 'SettingsService', 'ProjectProvider', 'ConfigService', 'SlackService', 'API_URL', 'DEFAULT_SC', 'OFFSET', TestRunListController])
        .config(function ($compileProvider) {
            $compileProvider.preAssignBindingsEnabled(true);
        });

    // **************************************************************************
    function TestRunListController($scope, $rootScope, $location, $window, $cookieStore, $mdDialog, $mdConstant, $interval, $timeout, $stateParams, $mdDateRangePicker, TestService, TestRunService, UtilService, UserService, SettingsService, ProjectProvider, ConfigService, SlackService, API_URL, DEFAULT_SC, OFFSET) {

        $scope.predicate = 'startTime';
        $scope.reverse = false;

        $scope.UtilService = UtilService;

        $scope.testRunId = $stateParams.id;
        $scope.testRuns = {};
        $scope.totalResults = 0;
        $scope.selectedTestRuns = {};
        $scope.expandedTestRuns = [];

        $scope.showRealTimeEvents = true;

        $scope.project = ProjectProvider.getProject();

        $scope.showReset = $scope.testRunId != null;
        $scope.selectAll = false;

        $scope.logs = [];

        $scope.sc = angular.copy(DEFAULT_SC);

        // ************** Integrations **************

        $scope.rabbitmq = $rootScope.rabbitmq;
        $scope.jira     = $rootScope.jira;
        $scope.jenkins  = $rootScope.jenkins;


        // ************** Websockets **************

        $scope.subscribtions = {};

        $scope.initWebsocket = function () {
            $scope.zafiraWebsocket = Stomp.over(new SockJS(API_URL + "/websockets"));
            $scope.zafiraWebsocket.debug = null;
            $scope.zafiraWebsocket.connect({withCredentials: false}, function () {
                $scope.subscribtions['statistics'] = $scope.subscribeStatisticsTopic();
                $scope.subscribtions['testRuns'] = $scope.subscribeTestRunsTopic();
            });
        };

        $scope.subscribeStatisticsTopic = function () {
            return $scope.zafiraWebsocket.subscribe("/topic/statistics", function (data) {
                var event = $scope.getEventFromMessage(data.body);
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
                $scope.$apply();
            });
        };

        $scope.subscribeTestRunsTopic = function () {
            return $scope.zafiraWebsocket.subscribe("/topic/testRuns", function (data) {
                var event = $scope.getEventFromMessage(data.body);
                if (($scope.testRunId && $scope.testRunId != event.testRun.id)
                    || ($scope.showRealTimeEvents == false && $scope.testRuns[event.testRun.id] == null)
                    || ($scope.project != null && $scope.project.id != event.testRun.project.id)
                    || !$scope.checkSearchCriteria($scope.sc)) {
                    return;
                }
                $scope.addTestRun(event.testRun);
                $scope.$apply();
            });
        };

        $scope.subscribeTestsTopic = function (testRunId) {
            if($scope.zafiraWebsocket.connected) {
                return $scope.zafiraWebsocket.subscribe("/topic/testRuns/" + testRunId + "/tests", function (data) {
                    var event = $scope.getEventFromMessage(data.body);
                    $scope.addTest(event.test);
                    $scope.$apply();
                });
            }
        };

        $scope.initTestLogsWebsocket = function (ciRunId, testId) {
            var logsWebsocket = Stomp.over(new SockJS($scope.rabbitmq.ws));
            logsWebsocket.debug = null;
            logsWebsocket.connect($scope.user, $scope.pass, function () {
                logsWebsocket.subscribe("/queue/" + ciRunId, function (data) {
                    var event = $scope.getEventFromMessage(data.body);
                    $scope.$apply();
                });
            });
        };

        $scope.$on('$destroy', function () {
            if($scope.zafiraWebsocket && $scope.zafiraWebsocket.connected) {
            		$scope.zafiraWebsocket.disconnect();
            }
        });

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
                $scope.testRuns[testRun.id].elapsed = testRun.elapsed;
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

            if ($scope.selectedRange.dateStart && $scope.selectedRange.dateEnd) {
                if(!$scope.isEqualDate()){
                    $scope.sc.fromDate = $scope.selectedRange.dateStart;
                    $scope.sc.toDate = $scope.selectedRange.dateEnd;
                }
                else {
                    $scope.sc.date = $scope.selectedRange.dateStart;
                }
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

        $scope.isEqualDate = function() {
            if($scope.selectedRange.dateStart && $scope.selectedRange.dateEnd){
                return $scope.selectedRange.dateStart.getTime() === $scope.selectedRange.dateEnd.getTime();
            }
        };

        $scope.loadTests = function (testRunId) {
            $scope.lastTestRunOpened = testRunId;
            var testSearchCriteria = {
                    'page': 1,
                    'pageSize': 100000,
                    'testRunId': testRunId
            }
            TestService.searchTests(testSearchCriteria).then(function(rs) {
                if(rs.success)
                {
                    var data = rs.data;
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
            if($scope.jenkins.enabled) {
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
            if($scope.jenkins.enabled) {
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
            if ($scope.jenkins.enabled) {
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
            $scope.showBuildNowOption = $scope.jenkins.enabled;
            $scope.showDeleteTestRunOption = true;
        };

        // -----------------------------------------------------------

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

        $scope.showLogsDialog = function(testRun, test, event) {
            $mdDialog.show({
                controller: LogsController,
                templateUrl: 'app/_testruns/logs_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRun: testRun,
                    test: test,
                    rabbitmq: $scope.rabbitmq
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
                    testRun: testRun,
                    jenkins: $scope.jenkins
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



        $scope.switchTestRunExpand = function (testRun) {
            if (!testRun.expand) {
                $scope.loadTests(testRun.id);
                testRun.expand = true;
                $scope.expandedTestRuns.push(testRun.id);
                $scope.subscribtions[testRun.id] = $scope.subscribeTestsTopic(testRun.id);
            } else {
                testRun.expand = false;
                testRun.tests = [];
                $scope.expandedTestRuns.splice($scope.expandedTestRuns.indexOf(testRun.id), 1);
                $scope.subscribtions[testRun.id].unsubscribe();
                delete $scope.subscribtions[testRun.id];
            }
        };
        
        // Control that only 1 test run expanded at a time
        $scope.$watch('expandedTestRuns.length', function() {
            if($scope.expandedTestRuns.length > 1)
            {
            		$scope.switchTestRunExpand($scope.testRuns[$scope.expandedTestRuns[0]]);
            }
        });

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
            $scope.selectedRange.dateStart = null;
            $scope.selectedRange.dateEnd = null;
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


        (function init() {
            toSc($location.search());
            $scope.initWebsocket();
            $scope.search(1);
            $scope.populateSearchQuery();
            $scope.loadEnvironments();
            $scope.loadPlatforms();
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

    function TestRunRerunController($scope, $mdDialog, TestRunService, testRun, jenkins) {

        $scope.rerunFailures = true;
        $scope.testRun = testRun;

        $scope.rebuild = function (testRun, rerunFailures) {
            if (jenkins.enabled) {
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
                $scope.isJiraIdClosed = true;
                TestService.getJiraIssue($scope.newKnownIssue.jiraId).then(function(rs) {
                    if(rs.success)
                    {
                        var issue = rs.data;
                        checkIssueStatus(issue);
                        if ($scope.isJiraIdExists) {
                            $scope.newKnownIssue.description = issue.summary;
                            $scope.newKnownIssue.assigneeMessage = 'Assigned to ' + issue.assignee.name + ' by ' + issue.reporter.name;
                            $scope.newKnownIssue.status = issue.status.name;
                        }
                        $scope.isIssueFound = true;
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
            $scope.onChangeAction();
            checkTestHasIssues();
            $scope.isNew = ! (issue.jiraId == $scope.testBugIssue.jiraId);
            $scope.newKnownIssue.id = issue.id;
            $scope.newKnownIssue.jiraId = issue.jiraId;
            $scope.newKnownIssue.description = issue.description;
            $scope.newKnownIssue.status = issue.status.name;
        };

        var issueCheckInterval = $interval(function () {
            $scope.checkKnowIssue();
        }, 2000);

        $scope.$on('$destroy', function() {
            if(issueCheckInterval)
                $interval.cancel(issueCheckInterval);
        });

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

	 function LogsController($scope, $mdDialog, $interval, rabbitmq, testRun, test) {

		 $scope.testLogsStomp = null;
		 $scope.logs = [];

	     $scope.$on('$destroy', function() {
	    	 	$scope.testLogsStomp.disconnect();
	    	 	$scope.logs = [];
	     });

	     $scope.hide = function() {
	         $mdDialog.hide();
	     };
	     $scope.cancel = function() {
	         $mdDialog.cancel();
	     };

	     (function initController() {
	    	 	 if(rabbitmq.enabled)
	    	 	 {
	    	 		$scope.testLogsStomp = Stomp.over(new SockJS(rabbitmq.ws));
		    	 	 $scope.testLogsStomp.debug = null;
		    	 	 $scope.testLogsStomp.connect(rabbitmq.user, rabbitmq.pass, function () {
	             		$scope.logs = [];

	             		$scope.$watch('logs', function (logs) {
	             			var scroll = document.getElementsByTagName("md-dialog-content")[0];
	             			scroll.scrollTop = scroll.scrollHeight;
	             		}, true);

	             		$scope.testLogsStomp.subscribe("/exchange/logs/" + testRun.ciRunId, function (data) {
	                 	   if((test != null && (testRun.ciRunId + "/" + test.id) == data.headers['correlation-id'])
	                 	   || (test == null && data.headers['correlation-id'].startsWith(testRun.ciRunId)))
	                 	   {
	                 		   var log = JSON.parse(data.body.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
	                    	   	   $scope.logs.push({'level': log.level, 'message': log.message, 'timestamp': log.timestamp});
	               	   	  	   $scope.$apply();
	                 	   }
	             		});
	             });
	    	 	 }
	     })();
	 }

})();
