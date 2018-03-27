(function () {
    'use strict';

    angular
        .module('app.testrun')
        .controller('TestRunListController', ['$scope', '$rootScope', '$location', '$window', '$cookieStore', '$mdDialog', '$mdConstant', '$interval', '$timeout', '$stateParams', '$mdDateRangePicker', '$q', 'TestService', 'TestRunService', 'UtilService', 'UserService', 'SettingsService', 'ProjectProvider', 'ConfigService', 'SlackService', 'DownloadService', 'API_URL', 'DEFAULT_SC', 'OFFSET', TestRunListController])
        .config(function ($compileProvider) {
            $compileProvider.preAssignBindingsEnabled(true);
        });

    // **************************************************************************
    function TestRunListController($scope, $rootScope, $location, $window, $cookieStore, $mdDialog, $mdConstant, $interval, $timeout, $stateParams, $mdDateRangePicker, $q, TestService, TestRunService, UtilService, UserService, SettingsService, ProjectProvider, ConfigService, SlackService, DownloadService, API_URL, DEFAULT_SC, OFFSET) {

        $scope.predicate = 'startTime';
        $scope.reverse = false;

        $scope.UtilService = UtilService;

        $scope.testRunId = $stateParams.id;
        $scope.testRuns = {};
        $scope.totalResults = 0;
        $scope.selectedTestRuns = {};
        $scope.expandedTestRuns = [];

        $scope.showRealTimeEvents = true;

        $scope.projects = ProjectProvider.getProjects();

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
            }, function () {
                $timeout(function () {
                    $scope.initWebsocket();
                }, 5000)
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
                    || ($scope.projects && $scope.projects.length && $scope.projects.indexOfField('id', event.testRun.project.id) == -1)
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
            }, function () {
                $timeout(function () {
                    $scope.initTestLogsWebsocket(ciRunId, testId);
                }, 5000)
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
            || ($scope.projects)
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
            if(testRun.tests == null){
                testRun.tests = {};
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

        $scope.areTestRunsFromOneSuite = function () {
            var testSuiteId;
            for(var testRunId in $scope.selectedTestRuns) {
                var selectedTestRun = $scope.selectedTestRuns[testRunId];
                if(! testSuiteId) {
                    testSuiteId = selectedTestRun.testSuite.id;
                }
                if(selectedTestRun.testSuite.id != testSuiteId) {
                    return false;
                }
            }
            return true;
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

        var downloadFromByteArray = function (filename, array, contentType) {
            var blob = new Blob([array.data], {type: contentType ? contentType : array.headers('Content-Type')});
            var link = document.createElement("a");
            document.body.appendChild(link);
            link.style = "display: none";
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            link.click();
        };

        $scope.downloadApplication = function (appVersion) {
            DownloadService.download(appVersion).then(function (rs) {
                if (rs.success) {
                    downloadFromByteArray(appVersion, rs.res)
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.checkFilePresence = function (testRun) {
            if (testRun.appVersionValid == undefined) {
                testRun.appVersionLoading = true;
                DownloadService.check(testRun.appVersion).then(function (rs) {
                    if (rs.success) {
                        testRun.appVersionValid = rs.data;
                    } else {
                        //alertify.error(rs.message);
                    }
                    delete testRun.appVersionLoading;
                    return rs.data;
                })
            }
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

        $scope.batchEmail = function(event)
        {
        		$scope.selectAll = false;
        		var testRuns = [];
	        	for(var id in $scope.testRuns)
	    		{
	        		if($scope.testRuns[id].selected)
	        		{
	        			testRuns.push($scope.testRuns[id]);
	        		}
	    		}
	        	$scope.showEmailDialog(testRuns, event);
        };

        $scope.addTestRun = function (testRun) {

            testRun.expand = $scope.testRunId ? true : false;
            if ($scope.testRuns[testRun.id] == null) {
                testRun.jenkinsURL = testRun.job.jobURL + "/" + testRun.buildNumber;
                testRun.UID = testRun.testSuite.name + " " + testRun.jenkinsURL;
                testRun.tests = null;
                $scope.testRuns[testRun.id] = testRun;
            }
            else {
                $scope.testRuns[testRun.id].status = testRun.status;
                $scope.testRuns[testRun.id].reviewed = testRun.reviewed;
                $scope.testRuns[testRun.id].elapsed = testRun.elapsed;
            }
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
                $scope.sc = ProjectProvider.initProjects($scope.sc);
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
                        testRun.tests = null;
                        $scope.addTestRun(testRun);
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
            return $q(function(resolve, reject) {
                $scope.lastTestRunOpened = testRunId;
                var testSearchCriteria = {
                    'page': 1,
                    'pageSize': 100000,
                    'testRunId': testRunId
                };
                TestService.searchTests(testSearchCriteria).then(function (rs) {
                    if (rs.success) {
                        var data = rs.data;
                        var inProgressTests = 0;
                        var testRun = $scope.testRuns[testRunId];
                        for (var i = 0; i < data.results.length; i++) {
                            var test = data.results[i];
                            $scope.addTest(test);
                        }
                        resolve(angular.copy(data));
                    }
                    else {
                        reject(rs.message);
                        console.error(rs.message);
                    }
                });
            })
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
            var path = $location.$$path;
            var url = $location.$$absUrl.split(path)[0] + path;
            node.textContent = url + "/" + testRun.id;
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
            $scope.showEmailDialog([testRun], event);
        };

        $scope.export = function (testRun) {
            TestRunService.exportTestRunResultsHTML(testRun.id).then(function(rs) {
                if(rs.success)
                {
                    downloadFromByteArray(testRun.testSuite.name.split(' ').join('_') + ".html", rs, 'html');
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
            $scope.showNotifyInSlackOption = ($scope.isSlackAvailable && $scope.slackChannels.indexOf(testRun.job.name) !== -1) && testRun.reviewed != null && testRun.reviewed;
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

        $scope.loadSlackMappings = function() {
            $scope.slackChannels = [];
            SettingsService.getSettingByTool('SLACK').then(function(rs) {
                if (rs.success) {
                    var settings = UtilService.settingsAsMap(rs.data);
                    angular.forEach(settings, function(value, key) {
                        if (key.indexOf('SLACK_NOTIF_CHANNEL_') == 0) {
                            angular.forEach(value.split(';'), function(v) {
                                $scope.slackChannels.push(v);
                            });
                        }
                    });
                    console.log('Slack mappings list: ' + $scope.slackChannels);
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.storeSlackAvailability = function() {
            $scope.isSlackAvailable = false;
            ConfigService.getConfig("slack").then(function successCallback(rs) {
                $scope.isSlackAvailable = rs.data.available;
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

        $scope.showCompareDialog = function (event) {
            $mdDialog.show({
                controller: CompareController,
                templateUrl: 'app/_testruns/compare_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    selectedTestRuns: $scope.selectedTestRuns
                }
            })
                .then(function(answer) {
                }, function() {
                });
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

        $scope.showEmailDialog = function(testRuns, event) {
            $mdDialog.show({
                controller: EmailController,
                templateUrl: 'app/_testruns/email_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRuns: testRuns
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
                    testRun: testRun,
                    isSlackAvailable: $scope.isSlackAvailable,
                    slackChannels: $scope.slackChannels
                }
            })
                .then(function(answer) {
                    testRun.reviewed = answer.reviewed;
                    testRun.comments = answer.comments;
                }, function() {
                });
        };

        $scope.showDetailsDialog = function(test, event) {
            $scope.isNewIssue = true;
            $scope.isNewTask = true;
            setWorkItemIsNewStatus(test.workItems);
            $mdDialog.show({
                controller: DetailsController,
                templateUrl: 'app/_testruns/test_details_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    test: test,
                    isNewIssue: $scope.isNewIssue,
                    isNewTask: $scope.isNewTask,
                    isConnectedToJira: $rootScope.tools['JIRA']
                }
            })
                .then(function(answer) {
                    if (answer == true) {
                        $scope.loadTests($scope.lastTestRunOpened);
                    }
                }, function() {
                });
        };

        var setWorkItemIsNewStatus = function (workItems){
            for (var i = 0; i < workItems.length; i++) {
                switch (workItems[i].type) {
                    case "TASK":
                        $scope.isNewTask = false;
                        break;
                    case "BUG":
                        $scope.isNewIssue = false;
                        break;
                }
            }
        };

        $scope.switchTestRunExpand = function (testRun) {
            if (!testRun.expand) {
                $scope.loadTests(testRun.id);
                testRun.expand = true;
                $scope.expandedTestRuns.push(testRun.id);
                $scope.subscribtions[testRun.id] = $scope.subscribeTestsTopic(testRun.id);
            } else {
                testRun.expand = false;
                testRun.tests = null;
                $scope.expandedTestRuns.splice($scope.expandedTestRuns.indexOf(testRun.id), 1);
                var subscription = $scope.subscribtions[testRun.id];
                if(subscription != null)
                {
                		subscription.unsubscribe();
                }
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
            $scope.loadSlackMappings();
            $scope.storeSlackAvailability();
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

    function EmailController($scope, $mdDialog, $mdConstant, UserService, TestRunService, testRuns) {

        $scope.email = {};
        $scope.email.recipients = [];
        $scope.users = [];
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SPACE, $mdConstant.KEY_CODE.SEMICOLON];

        $scope.sendEmail = function () {
            if($scope.users.length == 0) {
            	alertify.error('Add a recipient!')
                return;
            }
            $scope.hide();
            $scope.email.recipients = $scope.email.recipients.toString();

            testRuns.forEach(function(testRun) {
            		TestRunService.sendTestRunResultsEmail(testRun.id, $scope.email).then(function(rs) {
                    if(rs.success)
                    {
                        alertify.success('Email was successfully sent!');
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
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

    function CommentsController($scope, $mdDialog, TestRunService, SlackService, testRun, isSlackAvailable, slackChannels) {
        $scope.title = testRun.testSuite.name;
        $scope.testRun = angular.copy(testRun);

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
                        $scope.testRun.reviewed = true;
                        $scope.hide($scope.testRun);
                        alertify.success('Test run #' + $scope.testRun.id + ' marked as reviewed');
                        if(isSlackAvailable && slackChannels.indexOf(testRun.job.name) !== -1) {
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
        $scope.hide = function(testRun) {
            $mdDialog.hide(testRun);
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

    function CompareController($scope, $mdDialog, $q, $location, TestService, selectedTestRuns) {

        $scope.hideIdentical = false;
        $scope.allTestsIdentical = true;
        $scope.tr = {};
        angular.copy(selectedTestRuns, $scope.tr);

        var COMPARE_FIELDS = ['status', 'message'];
        var EXIST_FIELDS = {'name': '', 'testGroup': '', 'testClass': ''};

        function aggregateTests(testRuns) {
            return angular.forEach(collectUniqueTests(testRuns), function (test) {
                test.identical = areTestsIdentical(test.referrers, testRuns);
            });
        };

        function collectUniqueTests(testRuns) {
            var uniqueTests = {};
            angular.forEach(testRuns, function(testRun) {
                angular.forEach(testRun.tests, function(test) {
                    var uniqueTestKey = EXIST_FIELDS;
                    uniqueTestKey.name = test.name;
                    uniqueTestKey.testGroup = test.testGroup;
                    uniqueTestKey.testClass = test.testClass;
                    var stringKey = JSON.stringify(uniqueTestKey);
                    if(! uniqueTests[stringKey]) {
                        uniqueTests[stringKey] = test;
                        uniqueTests[stringKey].referrers = {};
                    }
                    if(!uniqueTests[stringKey].referrers[testRun.id]) {
                        uniqueTests[stringKey].referrers[testRun.id] = {};
                    }
                    uniqueTests[stringKey].referrers[testRun.id] = test.id;
                })
            });
            return uniqueTests;
        };

        function areTestsIdentical(referrers, testRuns) {
            var value = {};
            var result = {};
            var identicalCount = 'count';
            result[identicalCount] = Object.size(referrers) == Object.size(testRuns);
            for(var testRunId in referrers) {
                var test = testRuns[testRunId].tests[referrers[testRunId]];
                if(Object.size(value) == 0) {
                    for(var index = 0; index < COMPARE_FIELDS.length; index++) {
                        var field = COMPARE_FIELDS[index];
                        value[field] = test[field];
                        result[field] = true;
                    }
                    result.isIdentical = true;
                    continue;
                }
                for(var index = 0; index < COMPARE_FIELDS.length; index++) {
                    var field = COMPARE_FIELDS[index];
                    result[field] = verifyValueWithRegex(field, test[field], value[field]);
                    if(result[field] == false) {
                        result.isIdentical = false;
                        $scope.allTestsIdentical = false;
                    }
                }
            }
            if(! result[identicalCount]) {
                $scope.allTestsIdentical = false;
            }
            return result;
        };

        function verifyValueWithRegex(field, value1, value2) {
            var val1 = field == 'message' && value1 ? value1
                    .replace(new RegExp("\\d+","gm"), '*')
                    .replace(new RegExp("\\[.*\\]","gm"), '*')
                    .replace(new RegExp("\\{.*\\}","gm"), '*')
                    .replace(new RegExp(".*\\b(Session ID)\\b.*","gm"), '*')
                : value1;
            var val2 = field == 'message' && value2 ? value2
                    .replace(new RegExp("\\d+", "gm"), '*')
                    .replace(new RegExp("\\[.*\\]", "gm"), '*')
                    .replace(new RegExp("\\{.*\\}", "gm"), '*')
                    .replace(new RegExp(".*\\b(Session ID)\\b.*", "gm"), '*')
                : value2;
            return ! isEmpty(value1) && ! isEmpty(value2) ? value1 == value2 : true;
        };

        function isEmpty(value) {
            return ! value || ! value.length;
        };

        $scope.getSize = function (obj) {
            return Object.size(obj);
        };

        $scope.getTest = function (testUnique, testRun) {
            var testId = testUnique.referrers[testRun.id];
            return testRun.tests[testId];
        };

        $scope.initTestRuns = function () {
            return $q(function(resolve, reject) {
                var index = 0;
                var testRunsSize = Object.size($scope.tr);
                angular.forEach($scope.tr, function (testRun, testRunId) {
                    loadTests(testRunId).then(function (sr) {
                        $scope.tr[testRunId].tests = {};
                        sr.results.forEach(function(test) {
                            $scope.tr[testRunId].tests[test.id] = test;
                        });
                        index++;
                        if(index == testRunsSize) {
                            resolve($scope.tr);
                        }
                    });
                });
            })
        };

        function loadTests(testRunId) {
            return $q(function(resolve, reject) {
                var testSearchCriteria = {
                    'page': 1,
                    'pageSize': 100000,
                    'testRunId': testRunId
                };
                TestService.searchTests(testSearchCriteria).then(function (rs) {
                    if (rs.success) {
                        resolve(angular.copy(rs.data));
                    }
                    else {
                        reject(rs.message);
                        console.error(rs.message);
                    }
                });
            })
        };

        $scope.openTestRun = function (testRunId) {
            if ($location.$$path != $location.$$url){
                $location.search({});
            }
            if ($location.$$absUrl.match(new RegExp(testRunId, 'gi')) == null){
                window.open($location.$$absUrl + "/" + testRunId, '_blank');
            }
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        (function initController() {
            $scope.loading = true;
            $scope.initTestRuns().then(function (testRuns) {
                $scope.loading = false;
                $scope.uniqueTests = aggregateTests(testRuns);
            });
        })();
    }

    function DetailsController($scope, $mdDialog, $interval,  SettingsService, TestService, test, isNewIssue, isNewTask, isConnectedToJira) {

        $scope.jiraId;
        $scope.isConnectedToJira = false;

        $scope.issueJiraIdExists = true;
        $scope.taskJiraIdExists = true;

        $scope.issueTabDisabled = true;
        $scope.taskTabDisabled = true;


        $scope.isIssueFound = true;
        $scope.isTaskFound = true;

        $scope.isIssueClosed = false;

        $scope.test = angular.copy(test);
        $scope.testComment = {};
        $scope.testComments = [];
        $scope.knownIssues = [];
        $scope.tasks = [];
        $scope.testStatuses = ["UNKNOWN", "IN_PROGRESS", "PASSED", "FAILED", "SKIPPED", "ABORTED"];
        $scope.selectedTabIndex = 0;

        $scope.changeStatusDropdownIsVisible = false;
        $scope.addCommentTextfieldIsVisible = false;
        $scope.taskListIsVisible = false;
        $scope.issueListIsVisible = false;

        /* TEST_STATUS functionality */

        $scope.updateTest = function (test) {
            TestService.updateTest(test).then(function(rs) {
                if(rs.success) {
                    alertify.success('Test was successfully updated');
                }
                else {
                    console.error(rs.message);
                }
            });
        };

        $scope.moveToTab = function (tabIndex) {
            $scope.selectedTabIndex = tabIndex;
        };
        /* WORKITEM functionality */

        var taskJiraIdInputIsChanged = false;
        var issueJiraIdInputIsChanged = false;

        /* Assigns workitem to the test*/

        $scope.assignIssue = function (issue) {
            TestService.createTestWorkItem(test.id, issue).then(function(rs) {
                var workItemType = issue.type;
                var jiraId = issue.jiraId;
                var message;
                if(rs.success) {
                    if($scope.isNewIssue) {
                        message = generateActionResultMessage(workItemType, jiraId, "assign" + "e", true);
                    } else {
                        message = generateActionResultMessage(workItemType, jiraId, "update", true);
                    }
                    $scope.testComment.description = message;
                    $scope.addTestComment($scope.testComment);
                    updateWorkItemList(rs.data);
                    initAttachedWorkItems();
                    $scope.isNewIssue = !(jiraId === $scope.attachedKnownIssue.jiraId);
                    alertify.success(message);
                }
                else {
                    if($scope.isNewIssue){
                        message = generateActionResultMessage(workItemType, jiraId, "assign", false);
                    } else {
                        message = generateActionResultMessage(workItemType, jiraId, "update", false);
                    }
                    alertify.error(message);
                }
            });
        };

        $scope.assignTask = function (task) {
            TestService.createTestWorkItem(test.id, task).then(function(rs) {
                var workItemType = task.type;
                var jiraId = task.jiraId;
                var message;
                if(rs.success) {
                    if($scope.isNewTask) {
                        message = generateActionResultMessage(workItemType, jiraId, "assign" + "e", true);
                    } else {
                        message = generateActionResultMessage(workItemType, jiraId, "update", true);
                    }
                    $scope.testComment.description = message;
                    $scope.addTestComment($scope.testComment);
                    updateWorkItemList(rs.data);
                    initAttachedWorkItems();
                    $scope.isNewTask = !(jiraId === $scope.attachedTask.jiraId);
                    alertify.success(message);
                }
                else {
                    if($scope.isNewTask){
                        message = generateActionResultMessage(workItemType, jiraId, "assign", false);
                    } else {
                        message = generateActionResultMessage(workItemType, jiraId, "update", false);
                    }
                    alertify.error(message);
                }
            });
        };

        /* Updates list of workitems in scope */

        var updateWorkItemList = function (workItem){
            switch (workItem.type){
                case 'BUG':
                    var issues = $scope.knownIssues;
                    for (var i = 0; i < issues.length; i++) {
                        if (issues[i].jiraId === workItem.jiraId) {
                            deleteWorkItemFromList(issues[i]);
                            break;
                        }
                    }
                    $scope.knownIssues.push(workItem);
                    break;
                case 'TASK':
                    var tasks = $scope.tasks;
                    for (var j = 0; j < tasks.length; j++) {
                        if (tasks[j].jiraId === workItem.jiraId) {
                            deleteWorkItemFromList(tasks[j]);
                            break;
                        }
                    }
                    $scope.tasks.push(workItem);
            }
            $scope.test.workItems.push(workItem);
        };

        /* Deletes workitem from list of workitems in scope */

        var deleteWorkItemFromList = function (workItem){
            switch (workItem.type){
                case 'BUG':
                    var issueToDelete =  $scope.knownIssues.filter(function (listWorkItem) {
                        return listWorkItem.jiraId === workItem.jiraId;
                    })[0];
                    var issueIndex = $scope.knownIssues.indexOf(issueToDelete);
                    if (issueIndex !== -1) {
                        $scope.knownIssues.splice(issueIndex, 1);
                    }
                    break;
                case 'TASK':
                    var taskToDelete =  $scope.tasks.filter(function (listWorkItem) {
                        return listWorkItem.jiraId === workItem.jiraId;
                    })[0];
                    var taskIndex = $scope.tasks.indexOf(taskToDelete);
                    if (taskIndex !== -1) {
                        $scope.tasks.splice(taskIndex, 1);
                    }
                    break;
            }
            deleteWorkItemFromTestWorkItems(workItem);
        };

        /* Deletes workitem from list of workitems in test object */

        var deleteWorkItemFromTestWorkItems = function (workItem) {
            var issueToDelete =  $scope.test.workItems.filter(function (listWorkItem) {
                return listWorkItem.jiraId === workItem.jiraId;
            })[0];
            var workItemIndex = $scope.test.workItems.indexOf(issueToDelete);
            if (workItemIndex !== -1) {
                $scope.test.workItems.splice(workItemIndex, 1);
            }
        };

        /* Gets all workitems of the chosen type attached to the testcase */

        var getKnownIssues = function () {
            TestService.getTestCaseWorkItemsByType(test.id, 'BUG').then(function(rs) {
                if(rs.success) {
                    $scope.knownIssues = rs.data;
                    if(test.workItems.length && !$scope.isNewIssue) {
                        angular.copy($scope.attachedKnownIssue, $scope.newKnownIssue);
                    }
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        var getTasks = function () {
            TestService.getTestCaseWorkItemsByType(test.id, 'TASK').then(function(rs) {
                if(rs.success) {
                    $scope.tasks = rs.data;
                    if(test.workItems.length && !$scope.isNewTask) {
                        angular.copy($scope.attachedTask, $scope.newTask);
                    }
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /* Reattaches workitem from test */

        $scope.deleteIssue = function (workItem) {
            TestService.deleteTestWorkItem(test.id, workItem.id).then(function(rs) {
                var message;
                if(rs.success) {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign" + "e", true);
                    $scope.testComment.description = message;
                    $scope.addTestComment($scope.testComment);
                    deleteWorkItemFromTestWorkItems(workItem);
                    initAttachedWorkItems();
                    initNewKnownIssue();
                    alertify.success(message);
                } else {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign", false);
                    alertify.error(message);
                }
            });
        };

        $scope.deleteTask = function (workItem) {
            TestService.deleteTestWorkItem(test.id, workItem.id).then(function(rs) {
                var message;
                if(rs.success) {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign" + "e", true);
                    $scope.testComment.description = message;
                    $scope.addTestComment($scope.testComment);
                    deleteWorkItemFromTestWorkItems(workItem);
                    initAttachedWorkItems();
                    initNewTask();
                    alertify.success(message);
                } else {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign", false);
                    alertify.error(message);
                }
            });
        };

        /* Removes deleted workitem from UI */

        $scope.selectExistingIssue = function (issue) {
            $scope.onIssueSearchFieldChangeAction();
            initAttachedWorkItems();
            $scope.isNewIssue = !(issue.jiraId === $scope.attachedKnownIssue.jiraId);
            $scope.newKnownIssue.id = issue.id;
            $scope.newKnownIssue.jiraId = issue.jiraId;
            $scope.newKnownIssue.description = issue.description;
        };

        $scope.selectExistingTask = function (task) {
            $scope.onTaskSearchFieldChangeAction();
            initAttachedWorkItems();
            $scope.isNewTask = !(task.jiraId === $scope.attachedTask.jiraId);
            $scope.newTask.id = task.id;
            $scope.newTask.jiraId = task.jiraId;
            $scope.newTask.description = task.description;
        };

        /* Processes workitem before search */

        $scope.onIssueSearchFieldChangeAction = function () {
            issueJiraIdInputIsChanged = true;
            $scope.newKnownIssue.description = '';
            $scope.newKnownIssue.id = null;
            $scope.newKnownIssue.status = null;
            $scope.newKnownIssue.assignee = null;
            $scope.newKnownIssue.reporter = null;
            $scope.issueJiraIdExists = true;
            $scope.isIssueClosed = false;
            $scope.isIssueFound = false;
            var issueExists;
            var existingIssue = $scope.knownIssues.filter(function (foundIssue) {
                issueExists = foundIssue.jiraId === $scope.newKnownIssue.jiraId;
                return issueExists;
            })[0];
            $scope.isNewIssue = !(issueExists);
            if(existingIssue)
                angular.copy(existingIssue, $scope.newKnownIssue);
        };

        $scope.onTaskSearchFieldChangeAction = function () {
            taskJiraIdInputIsChanged = true;
            $scope.newTask = resetSearchFlags($scope.newTask);
            $scope.taskJiraIdExists = true;
            $scope.isTaskFound = false;
            var taskExists;
            var existingTask = $scope.tasks.filter(function (foundTask) {
                taskExists = foundTask.jiraId === $scope.newTask.jiraId;
                return taskExists;
            })[0];
            $scope.isNewTask = !(taskExists);
            if(existingTask)
                angular.copy(existingTask, $scope.newTask);

        };

        var resetSearchFlags = function (workItem) {
            workItem.description = '';
            workItem.id = null;
            workItem.status = null;
            workItem.assignee = null;
            workItem.reporter = null;
            return workItem;
        };

        /* Checks if ticket already attached to the testcase */

        /* Initializes typed empty workitem */

        var initNewKnownIssue = function () {
            $scope.isNewIssue = isNewIssue;
            $scope.newKnownIssue = {};
            $scope.newKnownIssue.type = "BUG";
            $scope.newKnownIssue.testCaseId = test.testCaseId;
        };

        var initNewTask = function () {
            $scope.isNewTask = isNewTask;
            $scope.newTask = {};
            $scope.newTask.type = "TASK";
            $scope.newTask.testCaseId = test.testCaseId;
        };

        /* Gets from DB JIRA_CLOSED_STATUS name for the current project*/

        var getJiraClosedStatusName = function() {
            SettingsService.getSetting('JIRA_CLOSED_STATUS').then(function successCallback(rs) {
                if(rs.success){
                    $scope.closedStatusName = rs.data;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /* Writes all attached to the test workitems into scope variables.
        Used for initialization and reinitialization */

        var initAttachedWorkItems = function () {
            $scope.testComments = [];
            var attachedWorkItem = {};
            attachedWorkItem.jiraId = '';
            $scope.attachedKnownIssue = attachedWorkItem;
            $scope.attachedTask = attachedWorkItem;
            var workItems = $scope.test.workItems;
            for (var i = 0; i < workItems.length; i++){
                switch(workItems[i].type) {
                    case 'BUG':
                        $scope.attachedKnownIssue = workItems[i];
                        break;
                    case 'TASK':
                        $scope.attachedTask = workItems[i];
                        break;
                    case 'COMMENT':
                        $scope.testComments.push(workItems[i]);
                        break;
                    default:
                        console.log("Work item type is not defined");
                        break;
                }
            }
        };

        /* Generates result message for action comment into DB and UI alert */

        var generateActionResultMessage = function (item, id, action, success) {
            if (success) {
                return item + " " +  id +" was " + action + "d";
            } else {
                return "Failed to " + action + " " + item.toLowerCase();
            }
        };

        /* On Jira ID input change makes search if conditions are fulfilled */

        var workItemSearchInterval = $interval(function () {
            if(issueJiraIdInputIsChanged){
                if (isIssueSearchAvailable($scope.newKnownIssue.jiraId)) {
                    searchIssue($scope.newKnownIssue);
                }
            }
            if(taskJiraIdInputIsChanged){
                if (isTaskSearchAvailable($scope.newTask.jiraId)) {
                    searchTask($scope.newTask);
                }
            }
        }, 2000);

        /*  Checks whether conditions for search in Jira are fulfilled */

        var isIssueSearchAvailable = function (jiraId) {
            if ($scope.isConnectedToJira){
                if (jiraId) {
                    if ($scope.issueTabDisabled || issueJiraIdInputIsChanged) {
                        issueJiraIdInputIsChanged = false;
                        return true;
                    }
                } else {
                    $scope.isIssueFound = true;
                    return false;
                }
            }
        };

        var isTaskSearchAvailable = function (jiraId) {
            if ($scope.isConnectedToJira){
                if (jiraId) {
                    if ($scope.taskTabDisabled || taskJiraIdInputIsChanged) {
                        taskJiraIdInputIsChanged = false;
                        return true;
                    }
                } else {
                    $scope.isTaskFound = true;
                    return false;
                }
            }
        };

        /* Searches workitem in Jira by Jira ID */

        var searchIssue = function (issue) {
            $scope.isIssueFound = false;
            TestService.getJiraTicket(issue.jiraId).then(function(rs) {
                if(rs.success) {
                    var searchResultIssue = rs.data;
                    $scope.isIssueFound = true;
                    if (searchResultIssue === '') {
                        $scope.isIssueClosed = false;
                        $scope.issueJiraIdExists = false;
                        $scope.issueTabDisabled = false;
                        return;
                    }
                    $scope.issueJiraIdExists = true;
                    $scope.isIssueClosed = $scope.closedStatusName.toLowerCase() === searchResultIssue.status.name.toLowerCase();
                    $scope.newKnownIssue.description = searchResultIssue.summary;
                    $scope.newKnownIssue.assignee = searchResultIssue.assignee.name;
                    $scope.newKnownIssue.reporter = searchResultIssue.reporter.name;
                    $scope.newKnownIssue.status = searchResultIssue.status.name;
                    $scope.issueTabDisabled = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        var searchTask = function (task) {
            $scope.isTaskFound = false;
            TestService.getJiraTicket(task.jiraId).then(function(rs) {
                if(rs.success) {
                    var searchResultTask = rs.data;
                    $scope.isTaskFound = true;
                    if (searchResultTask === '') {
                        $scope.taskJiraIdExists = false;
                        $scope.taskTabDisabled = false;
                        return;
                    }
                    $scope.taskJiraIdExists = true;
                    $scope.newTask.description = searchResultTask.summary;
                    $scope.newTask.assignee = searchResultTask.assignee.name;
                    $scope.newTask.reporter = searchResultTask.reporter.name;
                    $scope.newTask.status = searchResultTask.status.name;
                    $scope.taskTabDisabled = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /* Closes search interval on modal closing */

        $scope.$on('$destroy', function() {
            if(workItemSearchInterval)
                $interval.cancel(workItemSearchInterval);
        });

        /* Actions after opening modal (requests to Jira for additional info) */

        var issueOnModalOpenSearch = $interval(function(){
            if (angular.element(document.body).hasClass('md-dialog-is-showing')) {
                if (!isIssueSearchAvailable($scope.newKnownIssue.jiraId)) {
                    $scope.issueTabDisabled = false;
                } else {
                    searchIssue($scope.newKnownIssue);
                }
                $interval.cancel(issueOnModalOpenSearch);
            }
        }, 1000);

        var taskOnModalOpenSearch = $interval(function(){
            if (angular.element(document.body).hasClass('md-dialog-is-showing')) {
                if (!isTaskSearchAvailable($scope.newTask.jiraId)) {
                    $scope.taskTabDisabled = false;
                } else {
                    searchTask($scope.newTask);
                }
                $interval.cancel(taskOnModalOpenSearch);
            }
        }, 3000);

        /* COMMENT functionality */

        /* Adds comment to test (either custom or action-related) */

        $scope.addTestComment = function (testComment){
            $scope.testComment.jiraId = Math.floor(Math.random() * 90000) + 10000;
            $scope.testComment.testCaseId = test.testCaseId;
            $scope.testComment.type = 'COMMENT';
            TestService.createTestWorkItem(test.id, testComment).then(function(rs){
                if(rs.success) {
                    $scope.testComments.push(rs.data);
                    $scope.testComment = {};
                    $scope.addCommentTextfieldIsVisible = false;
                } else {
                    $scope.testComment = {};
                    alertify.error('Failed to create comment for test "' + test.id);
                }
            })
        };

        /* MODAL_WINDOW functionality */

        $scope.cancel = function () {
            $scope.hide();
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        (function initController() {
            $scope.isConnectedToJira = isConnectedToJira;
            getJiraClosedStatusName();
            initAttachedWorkItems();
            initNewKnownIssue();
            initNewTask();
            getKnownIssues();
            getTasks();
        })();
    }

})();
