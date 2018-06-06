(function () {
    'use strict';

    angular
        .module('app.testrun')
        .controller('TestRunListController', ['$scope', '$rootScope', '$mdToast', '$mdMenu', '$location', '$window', '$cookieStore', '$mdDialog', '$mdConstant', '$interval', '$timeout', '$stateParams', '$mdDateRangePicker', '$q', 'FilterService', 'ProjectService', 'TestService', 'TestRunService', 'UtilService', 'UserService', 'SettingsService', 'ProjectProvider', 'ConfigService', 'SlackService', 'DownloadService', 'API_URL', 'DEFAULT_SC', 'OFFSET', TestRunListController])
        .config(function ($compileProvider) {
            $compileProvider.preAssignBindingsEnabled(true);
        });

    // **************************************************************************
    function TestRunListController($scope, $rootScope, $mdToast, $mdMenu, $location, $window, $cookieStore, $mdDialog, $mdConstant, $interval, $timeout, $stateParams, $mdDateRangePicker, $q, FilterService, ProjectService, TestService, TestRunService, UtilService, UserService, SettingsService, ProjectProvider, ConfigService, SlackService, DownloadService, API_URL, DEFAULT_SC, OFFSET) {

        $scope.predicate = 'startTime';
        $scope.reverse = false;

        var FAST_SEARCH_TEMPLATE = {currentModel: 'testSuite'};

        $scope.fastSearch = angular.copy(FAST_SEARCH_TEMPLATE);

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

        $scope.STATUSES = ['PASSED', 'FAILED', 'SKIPPED', 'ABORTED', 'IN_PROGRESS', 'QUEUED', 'UNKNOWN'];

        $scope.searchFormIsEmpty = true;

        /*
            Filters
         */
        var subjectName = 'TEST_RUN';

        $scope.SYMBOLS = {
            EQUALS: " == ",
            NOT_EQUALS: " != ",
            CONTAINS: " cnt ",
            NOT_CONTAINS: " !cnt ",
            MORE: " > ",
            LESS: " < ",
            BEFORE: " <= ",
            AFTER: " >= ",
            LAST_24_HOURS: " last 24 hours",
            LAST_7_DAYS: " last 7 days",
            LAST_14_DAYS: " last 14 days",
            LAST_30_DAYS: " last 30 days"
        };

        var CURRENT_CRITERIA = {
            name: 'CRITERIA',
            value: null,
            type: []
        };

        var CURRENT_OPERATOR = {
            name: 'OPERATOR',
            value: null,
            type: []
        };

        var CURRENT_VALUE = {
            name: 'VALUE',
            value: null
        };

        $scope.currentCriteria = angular.copy(CURRENT_CRITERIA);
        $scope.currentOperator = angular.copy(CURRENT_OPERATOR);
        $scope.currentValue = angular.copy(CURRENT_VALUE);

        function getMode() {
            var mode = [];
            $scope.search_filter;
            if($scope.filterBlockExpand && $scope.collapseFilter) {
                if($scope.filter.id) {
                    mode.push('UPDATE');
                } else {
                    mode.push('CREATE');
                }
            }
            if ($scope.selectedFilterId) {
                mode.push('APPLY');
            }
            if (!$scope.searchFormIsEmpty) {
                mode.push('SEARCH');
            }
            return mode;
        };

        $scope.matchMode = function(modes) {
            return getMode().filter(function (m) {
                return modes.indexOf(m) >= 0;
            }).length > 0;
        };

        $scope.DATE_CRITERIAS = ['DATE'];
        var SELECT_CRITERIAS = ['ENV', 'PLATFORM', 'PROJECT', 'STATUS'];
        $scope.DATE_CRITERIAS_PICKER_OPERATORS = ['EQUALS', 'NOT_EQUALS', 'BEFORE', 'AFTER'];

        $scope.isSelectCriteria = function(criteria) {
            return criteria ? SELECT_CRITERIAS.indexOf(criteria.name) >= 0 : false;
        };

        $scope.isDateCriteria = function (criteria) {
            return criteria ? $scope.DATE_CRITERIAS.indexOf(criteria.name) >= 0 : false;
        };

        $scope.isDatePickerOperator = function(operator) {
            return operator ? $scope.DATE_CRITERIAS_PICKER_OPERATORS.indexOf(operator) >= 0 : false;
        };

        $scope.subjectBuilder = {};
        $scope.filters = [];
        var DEFAULT_FILTER_VALUE = {
            subject: {
                name: subjectName,
                criterias: [],
                publicAccess: false
            }
        };
        $scope.filter = angular.copy(DEFAULT_FILTER_VALUE);

        $scope.selectedFilterRange = {
            selectedTemplate: null,
            selectedTemplateName: null,
            dateStart: null,
            dateEnd: null,
            showTemplate: false,
            onePanel: true
        };

        $scope.$watchGroup(['fastSearch.testSuite', 'fastSearch.executionURL', 'fastSearch.appVersion', 'sc.status',
            'sc.environment', 'sc.platform', 'sc.reviewed', 'selectedRange.dateStart', 'selectedRange.dateEnd'], function (fastSearchArray) {
            var notEmptyValues = fastSearchArray.filter(function(value) {return value != undefined && (value.length > 0
                || new Date(value) ||  value.$$hashKey || value === true);});
            $scope.searchFormIsEmpty = notEmptyValues.length == 0;
        });

        function onSelect(dates) {
            return $scope.selectedFilterRange.selectedTemplateName;
        };

        $scope.$watch('selectedFilterRange.dateStart', function (oldValue, newVal) {
            if(oldValue) {
                $scope.currentValue.value = angular.copy($scope.selectedFilterRange.dateStart);
                $scope.clearPickFilter();
                closeDatePickerMenu();
            }
        });

        function closeDatePickerMenu() {
            var menu = angular.element('#filter-editor md-menu *[aria-owns]').scope();
            if(menu.$mdMenuIsOpen) {
                menu.$mdMenu.close();
            }
        }

        $scope.pickFilter = function($event, showTemplate) {
            $scope.selectedFilterRange.showTemplate = showTemplate;
            $mdDateRangePicker.show({
                targetEvent: $event,
                model: $scope.selectedFilterRange,
                autoConfirm: true
            }).then(function(result) {
                if (result) $scope.selectedFilterRange = result;
            })
        };

        $scope.clearPickFilter = function() {
            $scope.selectedFilterRange.selectedTemplate = null;
            $scope.selectedFilterRange.selectedTemplateName = null;
            $scope.selectedFilterRange.dateStart = null;
            $scope.selectedFilterRange.dateEnd = null;
        };

        function loadSubjectBuilder() {
            FilterService.getSubjectBuilder(subjectName).then(function (rs) {
                if(rs.success) {
                    $scope.subjectBuilder = rs.data;
                    $scope.subjectBuilder.criterias.forEach(function(criteria) {
                        if($scope.isSelectCriteria(criteria)) {
                            switch(criteria.name) {
                                case 'ENV':
                                    criteria.values = $scope.environments;
                                    break;
                                case 'PLATFORM':
                                    criteria.values = $scope.platforms;
                                    break;
                                case 'PROJECT':
                                    criteria.values = $scope.allProjects;
                                    break;
                                case 'STATUS':
                                    criteria.values = $scope.STATUSES;
                                    break;
                            }
                        }
                    });
                }
            })
        };

        function loadPublicFilters() {
            FilterService.getAllPublicFilters().then(function (rs) {
                if(rs.success) {
                    $scope.filters = rs.data;
                }
            })
        };

        $scope.addChip = function () {
            $scope.filter.subject.criterias.push({
                name: $scope.currentCriteria.value.name,
                operator: $scope.currentOperator.value,
                value: $scope.currentValue.value && $scope.currentValue.value.value ? $scope.currentValue.value.value : $scope.currentValue.value
            });
            clearFilterSlice();
        };

        $scope.changeChip = function (chip, index) {
            console.log('in edit');
            var criteria = $scope.filter.subject.criterias[index];
            criteria.value = chip.value;
            return criteria;
        };

        $scope.chooseFilter = function (filter) {
            $scope.collapseFilter = true;
            $scope.filter = angular.copy(filter);
        };

        $scope.createFilter = function () {
            FilterService.createFilter($scope.filter).then(function (rs) {
                if (rs.success) {
                    alertify.success('Filter was created');
                    $scope.filters.push(rs.data);
                    $scope.clearFilter();
                    $scope.collapseFilter = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.updateFilter = function () {
            FilterService.updateFilter($scope.filter).then(function (rs) {
                if (rs.success) {
                    alertify.success('Filter was updated');
                    $scope.filters[$scope.filters.indexOfField('id', rs.data.id)] = rs.data;
                    $scope.clearAndOpenFilterBlock(false);
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.deleteFilter = function (id) {
            FilterService.deleteFilter(id).then(function (rs) {
                if (rs.success) {
                    alertify.success('Filter was deleted');
                    $scope.filters.splice($scope.filters.indexOfField('id', id), 1);
                    $scope.clearFilter();
                    $scope.collapseFilter = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.onFilterRemove = function (filter) {
            if (confirm('Do you really want to delete \'' + filter.name + '\' filter?')) {
                $scope.deleteFilter(filter.id);
            } else {
            }
        };

        function clearFilterCriterias(slice) {
            switch(slice) {
                case 'CRITERIA':
                    $scope.currentOperator = angular.copy(CURRENT_OPERATOR);
                    $scope.currentCriteria.type = [];
                case 'OPERATOR':
                    $scope.currentValue = angular.copy(CURRENT_VALUE);
                    $scope.currentOperator.type = [];
                case 'VALUE':
                default:
                    break;
            };
        };

        $scope.onFilterSliceUpdate = function(slice) {
            clearFilterCriterias(slice);
            switch(slice) {
                case 'CRITERIA':
                    if($scope.isSelectCriteria($scope.currentCriteria.value)) {
                        $scope.currentCriteria.type.push('SELECT');
                    }
                    if($scope.isDateCriteria($scope.currentCriteria.value)) {
                        $scope.currentCriteria.type.push('DATE');
                    }
                    break;
                case 'OPERATOR':
                    if($scope.isDateCriteria($scope.currentCriteria.value) && $scope.isDatePickerOperator($scope.currentOperator.value)) {
                        $scope.currentOperator.type.push('DATE');
                    }
                    break;
                case 'VALUE':
                    break;
                default:
                    break;
            };
        };

        function clearFilterSlice() {
            $scope.currentCriteria = angular.copy(CURRENT_CRITERIA);
            $scope.currentOperator = angular.copy(CURRENT_OPERATOR);
            $scope.currentValue = angular.copy(CURRENT_VALUE);
        };

        $scope.clearFilter = function() {
            $scope.filter = angular.copy(DEFAULT_FILTER_VALUE);
            clearFilterSlice();
        };

        $scope.resetFilter = function() {
            $scope.filter.subject = {};
            $scope.filter.publicAccess = false;
            clearFilterSlice();
        };

        $scope.clearAndOpenFilterBlock = function (value) {
            $scope.clearFilter();
            $scope.collapseFilter = value;
        };


        // ************** Integrations **************

        $scope.rabbitmq = $rootScope.rabbitmq;
        $scope.jira     = $rootScope.jira;
        $scope.jenkins  = $rootScope.jenkins;


        // ************** Websockets **************

        $scope.subscribtions = {};

        $scope.initWebsocket = function () {
            var wsName = 'zafira';
            $scope.zafiraWebsocket = Stomp.over(new SockJS(API_URL + "/websockets"));
            $scope.zafiraWebsocket.debug = null;
            $scope.zafiraWebsocket.connect({withCredentials: false}, function () {
                $scope.subscribtions['statistics'] = $scope.subscribeStatisticsTopic();
                $scope.subscribtions['testRuns'] = $scope.subscribeTestRunsTopic();
                if($scope.testRunId){
                    $scope.subscribtions[$scope.testRunId] = $scope.subscribeTestsTopic($scope.testRunId);
                }
                UtilService.websocketConnected(wsName);
            }, function () {
                UtilService.reconnectWebsocket(wsName, $scope.initWebsocket);
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
                    currentTestRun.aborted = event.testRunStatistics.aborted;
                    currentTestRun.queued = event.testRunStatistics.queued;
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

        $scope.$on('$destroy', function () {
            if($scope.zafiraWebsocket && $scope.zafiraWebsocket.connected) {
                $scope.zafiraWebsocket.disconnect();
                UtilService.websocketConnected('zafira');
            }
        });

        $scope.getEventFromMessage = function (message) {
            return JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
        };

        $scope.checkStatisticEvent = function (event) {
            return ($scope.testRunId && $scope.testRunId != event.testRunStatistics.testRunId)
            || ($scope.showRealTimeEvents == false && $scope.testRuns[event.testRunStatistics.testRunId] == null)
            || ($scope.projects)
            || !$scope.checkSearchCriteria($scope.sc);
        };

        $scope.checkSearchCriteria = function (sc) {
            var isEmpty = true;
            for (var criteria in sc) {
                if ( sc.hasOwnProperty(criteria) && sc[criteria] != null && sc[criteria] != "" && criteria != "page" && criteria != "pageSize" && criteria != "id") {
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
                $scope.testRuns[testRun.id].platform = testRun.platform;
                $scope.testRuns[testRun.id].env = testRun.env;
                $scope.testRuns[testRun.id].comments = testRun.comments;
                $scope.testRuns[testRun.id].reviewed = testRun.reviewed;
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

        function fillDateSc(selectedRange) {
            if (selectedRange.dateStart && selectedRange.dateEnd) {
                if(!$scope.isEqualDate()){
                    $scope.sc.fromDate = selectedRange.dateStart;
                    $scope.sc.toDate = selectedRange.dateEnd;
                }
                else {
                    $scope.sc.date = selectedRange.dateStart;
                }
            }
        };

        function fillFastSearchSc() {
            angular.forEach($scope.fastSearch, function (val, model) {
                if(model != 'currentModel')
                    $scope.sc[model] = val;
            });
        };

        $scope.searchByFilter = function(filter, chipsCtrl) {
            $scope.selectedFilterId = filter.id;
            $scope.chipsCtrl = chipsCtrl;
            $scope.search();
        };

        $scope.search = function (page, pageSize) {
            $scope.sc.date = null;
            $scope.sc.toDate = null;
            $scope.sc.fromDate = null;
            $scope.selectAll = false;

            $scope.sc.page = page;

            $scope.expandedTestRuns = [];

            if (pageSize) {
                $scope.sc.pageSize = pageSize;
            }

            if ($scope.testRunId) {
                $scope.sc.id = $scope.testRunId;
            }
            else {
                $scope.sc = ProjectProvider.initProjects($scope.sc);
            }

            fillFastSearchSc();

            fillDateSc($scope.selectedRange);

           var filterQuery = $scope.selectedFilterId ? '?filterId=' + $scope.selectedFilterId : undefined;

            TestRunService.searchTestRuns($scope.sc, filterQuery).then(function(rs) {
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
            var path = $location.$$path.split('/' + $stateParams.id)[0];
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

        $scope.createSpreadsheet = function (testRun, event) {
            $scope.showCreateSpreadsheetDialog([testRun], event);
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

        $scope.startDebug = function (testRun, event) {
            $scope.testRunInDebugMode = angular.copy(testRun);
            debug($scope.testRunInDebugMode);
        };

        $scope.testRunInDebugMode = {};
        $scope.debugHost = null;
        $scope.debugPort = null;

        function debug(testRun) {
            TestRunService.getJobParameters(testRun.id).then(function(rs) {
                if(rs.success) {
                    var jobParameters = rs.data;
                    if (jobParameters === '') {
                        alertify.error("Job parameters are not loaded");
                    } else {
                        var jobParametersMap = {};
                        for (var i = 0; i < jobParameters.length; i++){
                            if (jobParameters[i].name === 'debug'){
                                jobParameters[i].value = true;
                            }
                            if (jobParameters[i].name === 'rerun_failures'){
                                jobParameters[i].value = true;
                            }
                            if (jobParameters[i].name === 'thread_count'){
                                jobParameters[i].value = 1;
                            }
                            if (jobParameters[i].name === 'ci_run_id'){
                                testRun.ciRunId = jobParameters[i].value;
                            }
                            jobParametersMap[jobParameters[i].name] = jobParameters[i].value;
                        }
                        TestRunService.buildTestRun(testRun.id, jobParametersMap).then(function(rs) {
                            if(rs.success) {
                                alertify.success('Debug mode is starting, debug status will appear soon');
                                testRun.id = null;
                                var debugLog = '';
                                var parseLogsInterval = $interval(function(){
                                    TestRunService.getConsoleOutput(testRun.id, testRun.ciRunId, 200, 50).then(function(rs) {
                                        if(rs.success) {
                                            var map = rs.data;
                                            var value;
                                            Object.keys(map).forEach(function(key) {
                                                value = map[key];
                                                if(value.includes("Listening for transport dt_socket at address:")){
                                                    if(debugLog === ''){
                                                        $scope.debugPort = getPortFromLog(value);
                                                        $scope.debugHost = new URL($rootScope.jenkins.url).hostname;
                                                        showDebugToast();
                                                    }
                                                    $timeout.cancel(connectDebugTimeout);

                                                    if(debugLog === ''){
                                                        debugLog = value;
                                                    }

                                                    if(debugLog !== value){
                                                        $timeout.cancel(disconnectDebugTimeout);
                                                        $interval.cancel(parseLogsInterval);
                                                        closeToast();
                                                    }
                                                }
                                            });
                                        } else {
                                            stopConnectingDebug();
                                            $timeout.cancel(connectDebugTimeout);
                                            alertify.error(rs.message);
                                        }
                                    });
                                }, 10000);

                                var connectDebugTimeout = $timeout(function(){
                                    alertify.error("Problems with starting debug mode occurred, disabling");
                                    $scope.stopConnectingDebug();
                                }, 60000);

                                var disconnectDebugTimeout = $timeout(function(){
                                    $scope.stopDebugMode();
                                }, 600500);

                                $scope.stopConnectingDebug = function (){
                                    $timeout.cancel(connectDebugTimeout);
                                    $interval.cancel(parseLogsInterval);
                                };

                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    }
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.stopDebugMode = function(){
            $scope.stopConnectingDebug();
            if($scope.testRunInDebugMode){
                $scope.abort($scope.testRunInDebugMode);
                $scope.testRunInDebugMode = {};
            }
            alertify.warning("Debug mode is disabled");
        };

        function getPortFromLog(log){
            if(log){
                var portLine = log.slice(log.indexOf('address:'));
                return portLine.split(" ")[1];
            }
        }

        function closeToast() {
            $mdToast
                .hide()
                .then(function() {
                });
        }

        function showDebugToast() {
            $mdToast.show({
                hideDelay: 600000,
                position: 'bottom right',
                scope: $scope,
                preserveScope: true,
                controller : function ($scope, $mdToast) {
                    $scope.stopDebug = function() {
                        $scope.stopDebugMode();
                        $mdToast
                            .hide()
                            .then(function() {
                            });
                    };
                },
                templateUrl : 'app/_testruns/debug-mode_toast.html'
            });
        }

        $scope.$watch('selectAll', function(newValue, oldValue) {
        		for(var id in $scope.testRuns)
        		{
        			$scope.testRuns[id].selected = newValue;
        		}
        	});

        $scope.abort = function (testRun) {
            if($scope.jenkins.enabled) {
                TestRunService.abortCIJob(testRun.id, testRun.ciRunId).then(function (rs) {
                    if(rs.success) {
                        var abortCause = {};
                        if(testRun.id == null){
                            abortCause.comment = "Debug mode was disconnected";
                        } else {
                            abortCause.comment = "Aborted by " + $rootScope.currentUser.username;
                        }
                        TestRunService.abortTestRun(testRun.id, testRun.ciRunId, abortCause).then(function(rs) {
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
            return $q(function(resolve, reject) {
                TestRunService.getEnvironments().then(function(rs) {
                    if(rs.success)
                    {
                        $scope.environments = rs.data.filter(function (env) {
                            return env && env != null;
                        });
                        resolve($scope.environments);
                    }
                    else
                    {
                        alertify.error(rs.message);
                        reject(rs.message);
                    }
                });
            });
        };

        $scope.loadPlatforms = function () {
            return $q(function(resolve, reject) {
                TestRunService.getPlatforms().then(function (rs) {
                    if (rs.success) {
                        $scope.platforms = rs.data;
                        resolve($scope.platforms);
                    }
                    else {
                        alertify.error(rs.message);
                        reject(rs.message);
                    }
                });
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

        $scope.showDemoDialog = function(event, wsURL, testRun, test, isLive) {
            $mdDialog.show({
                controller: DemoController,
                templateUrl: 'app/_testruns/demo_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    wsURL: wsURL,
                    testRun: testRun,
                    test: test,
                    rabbitmq: $scope.rabbitmq,
                    isLive: isLive
                }
            })
                .then(function(answer) {
                }, function() {
                });
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

        $scope.changeTestStatus = function (test, status) {
            if(test.status != status && confirm('Do you really want mark test as ' + status + '?')) {
                test.status = status;
                TestService.updateTest(test).then(function (rs) {
                    if (rs.success) {
                        alertify.success('Test was marked as ' + status);
                    }
                    else {
                        console.error(rs.message);
                    }
                });
            }
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

        $scope.showCreateSpreadsheetDialog = function(testRuns, event) {
            $mdDialog.show({
                controller: SpreadsheetController,
                templateUrl: 'app/_testruns/spreadsheet_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRuns: testRuns
                }
            })
                .then(function(answer) {
                }, function(links) {
                    if(links.length) {
                        $mdToast.show({
                            hideDelay: 0,
                            position: 'bottom right',
                            locals: {
                                links: links
                            },
                            controller: function ($scope, $mdToast, links) {

                                $scope.links = links;

                                $scope.closeToast = function () {
                                    $mdToast
                                        .hide()
                                        .then(function () {
                                        });
                                };
                            },
                            templateUrl: 'app/_testruns/links_toast.html'
                        });
                    }
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
                    isConnectedToJira: $rootScope.tools['JIRA'],
                    isJiraEnabled: $rootScope.jira.enabled
                }
            }).then(function() {
                }, function(response) {
                    if (response) {
                        var testRun = $scope.testRuns[test.testRunId];
                        testRun.tests[test.id] = angular.copy(response);
                    }
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
                if(subscription != null) {
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
            $scope.fastSearch = angular.copy(FAST_SEARCH_TEMPLATE);
            delete $scope.selectedFilterId;
            $location.search({});
            $scope.search();
            if($scope.chipsCtrl)
                delete $scope.chipsCtrl.selectedChip;
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

        function loadProjects() {
            return $q(function(resolve, reject) {
                ProjectService.getAllProjects().then(function (rs) {
                    if (rs.success) {
                        $scope.allProjects = rs.data.map(function(proj) {
                            return proj.name;
                        });
                        resolve(rs.data);
                    } else {
                        reject(rs.message);
                    }
                });
            });
        };

        (function init() {
            toSc($location.search());
            $scope.initWebsocket();
            $scope.search(1);
            $scope.populateSearchQuery();
            var loadFilterDataPromises = [];
            loadFilterDataPromises.push($scope.loadEnvironments());
            loadFilterDataPromises.push($scope.loadPlatforms());
            loadFilterDataPromises.push(loadProjects());
            $q.all(loadFilterDataPromises).then(function(values) {
                loadSubjectBuilder();
            });
            $scope.loadSlackMappings();
            $scope.storeSlackAvailability();
            loadPublicFilters();
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

    function SpreadsheetController($scope, $mdDialog, $mdConstant, UserService, TestRunService, testRuns) {

        $scope.recipients = [];
        $scope.users = [];
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SPACE, $mdConstant.KEY_CODE.SEMICOLON];

        $scope.createSpreadsheet = function () {
            $scope.recipients = $scope.recipients.length ? $scope.recipients.toString() : [];
            $scope.links = [];

            testRuns.forEach(function(testRun) {
                TestRunService.createTestRunResultsSpreadsheet(testRun.id, $scope.recipients).then(function(rs) {
                    if(rs.success)
                    {
                        $scope.links.push(rs.data);
                        $scope.cancel($scope.links);
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
                $scope.recipients.push(currentUser.email);
                $scope.users.push(user);
            } else {
                user.email = currentUser;
                $scope.recipients.push(user.email);
                $scope.users.push(user);
            }
            return user;
        };
        $scope.removeRecipient = function (user) {
            var index = $scope.recipients.indexOf(user.email);
            if (index >= 0) {
                $scope.recipients.splice(index, 1);
            }
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function(links) {
            $mdDialog.cancel(links);
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

    function DemoController($scope, $mdDialog, $timeout, $window, UtilService, wsURL, rabbitmq, testRun, test, isLive) {

        var rfb;
        var display;
        var ratio;

        $scope.loading = true;

        $scope.initVNCWebsocket = function() {
            rfb = new RFB(angular.element('#vnc')[0], wsURL, { shared: true, credentials: { password: 'selenoid' } });
            //rfb._viewOnly = true;
            rfb.addEventListener("connect",  connected);
            rfb.addEventListener("disconnect",  disconnected);
            rfb.scaleViewport = true;
            rfb.resizeSession = true;
            display = rfb._display;
            display._scale = 1;
            angular.element($window).bind('resize', function(){
                autoscale(display, ratio, angular.element($window)[0]);
            });
        };

        function connected(e) {
            $scope.loading = false;
            var canvas = document.getElementsByTagName("canvas")[0];
            ratio = canvas.width / canvas.height;
            autoscale(display, ratio, angular.element($window)[0]);

        };

        function disconnected(e) {
            $scope.hide();
        };

        function autoscale(display, ratio, window) {
	        	var size = calculateSize(window, ratio);
	    		display.autoscale(size.width, size.height, false);
        };

        function calculateSize(window, ratio) {
            var width = window.innerWidth * 0.9;
            var height = ratio > 1 ?  width / ratio : width * ratio;
            if(height > window.innerHeight * 0.9)
            {
                height = window.innerHeight - 100;
                width = ratio < 1 ? height / ratio : height * ratio;
            }
            return {height: height, width: width};
        };

        $scope.$on('$destroy', function () {
            if(rfb && rfb._connected) {
                rfb.disconnect();
            }
            $scope.testLogsStomp.disconnect();
            $scope.logs = [];
            UtilService.websocketConnected('logs');
        });

        $scope.hide = function() {
            $mdDialog.hide();
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        $scope.testLogsStomp = null;
        $scope.logs = [];

        $scope.initLogsWebsocket = function () {
            if(rabbitmq.enabled)
            {
                var wsName = 'logs';
                $scope.testLogsStomp = Stomp.over(new SockJS(rabbitmq.ws));
                $scope.testLogsStomp.debug = null;
                $scope.testLogsStomp.connect(rabbitmq.user, rabbitmq.pass, function () {
                    $scope.logs = [];

                    UtilService.websocketConnected(wsName);

                    $scope.$watch('logs', function (logs) {
                        var scroll = document.getElementsByClassName("log-demo")[0];
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
                }, function () {
                    UtilService.reconnectWebsocket(wsName, $scope.initLogsWebsocket);
                });
            }
        };

        (function initController() {
            if(isLive) {
                $scope.title = 'Live video';
                $timeout(function () {
                    $scope.initVNCWebsocket();
                }, 200);
                $scope.initLogsWebsocket();
            } else {
                $scope.wsURL = wsURL;
                $scope.title = 'Video';
                $timeout(function () {
                    var container = document.getElementsByTagName('video')[0];
                    var containerRectangle = container.getBoundingClientRect();
                    var ratio = containerRectangle.width / (containerRectangle.height + 20);
                    $scope.videoWidth = calculateSize(angular.element($window)[0], ratio).width;
                    angular.element($window).bind('resize', function() {
                        $scope.videoWidth = calculateSize(angular.element($window)[0], ratio).width;
                    });
                }, 200);
            }
        })();
    };

 	 function LogsController($scope, $mdDialog, $interval, UtilService, rabbitmq, testRun, test) {

		 $scope.testLogsStomp = null;
		 $scope.logs = [];
		 $scope.loading = true;

	     $scope.$on('$destroy', function() {
             if($scope.testLogsStomp && $scope.testLogsStomp.connected) {
                 $scope.testLogsStomp.disconnect();
                 $scope.logs = [];
                 UtilService.websocketConnected('logs');
             }
	     });

	     $scope.hide = function() {
	         $mdDialog.hide();
	     };
	     $scope.cancel = function() {
	         $mdDialog.cancel();
	     };

	     $scope.initLogsWebsocket = function () {
             if(rabbitmq.enabled)
             {
                 var wsName = 'logs';
                 $scope.testLogsStomp = Stomp.over(new SockJS(rabbitmq.ws));
                 $scope.testLogsStomp.debug = null;
                 $scope.testLogsStomp.connect(rabbitmq.user, rabbitmq.pass, function () {
                     $scope.logs = [];

                     UtilService.websocketConnected(wsName);

                     $scope.$watch('logs', function (logs) {
                         var scroll = document.getElementsByTagName("md-dialog-content")[0];
                         scroll.scrollTop = scroll.scrollHeight;
                     }, true);

                     $scope.testLogsStomp.subscribe("/exchange/logs/" + testRun.ciRunId, function (data) {
                         if((test != null && (testRun.ciRunId + "/" + test.id) == data.headers['correlation-id'])
                             || (test == null && data.headers['correlation-id'].startsWith(testRun.ciRunId)))
                         {
                             $scope.loading = false;
                             var log = JSON.parse(data.body.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
                             $scope.logs.push({'level': log.level, 'message': log.message, 'timestamp': log.timestamp});
                             $scope.$apply();
                         }
                     });
                 }, function () {
                     UtilService.reconnectWebsocket(wsName, $scope.initLogsWebsocket);
                 });
             }
         };

	     (function initController() {
	         $scope.initLogsWebsocket();
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

    function DetailsController($scope, $rootScope, $mdDialog, $interval,  SettingsService, TestService, test, isNewIssue, isNewTask, isConnectedToJira, isJiraEnabled) {

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
        $scope.testCommentText = '';
        $scope.testComments = [];
        $scope.issues = [];
        $scope.tasks = [];
        $scope.currentStatus = $scope.test.status;
        $scope.testStatuses = ['PASSED', 'FAILED', 'SKIPPED', 'ABORTED'];
        $scope.ticketStatuses = ['TO DO', 'OPEN', 'NOT ASSIGNED', 'IN PROGRESS', 'FIXED', 'REOPENED', 'DUPLICATE'];

        $scope.selectedTabIndex = 0;

        $scope.issueStatusIsNotRecognized = false;
        $scope.changeStatusIsVisible = false;
        $scope.taskListIsVisible = false;
        $scope.issueListIsVisible = false;

        /* TEST_STATUS functionality */

        $scope.updateTest = function (test) {
            var message;
            TestService.updateTest(test).then(function(rs) {
                if(rs.success) {
                    $scope.changeStatusIsVisible = false;
                    message = 'Test was marked as ' + test.status;
                    addTestEvent(message);
                    alertify.success(message);
                }
                else {
                    console.error(rs.message);
                }
            });
        };

        $scope.moveToTab = function (tabIndex) {
            $scope.selectedTabIndex = tabIndex;
        };

        /** UI methods for handling actions with ISSUE / TASK */

        /* Updates list of workitems on UI */

        var updateWorkItemList = function (workItem){
            switch (workItem.type){
                case 'BUG':
                    var issues = $scope.issues;
                    for (var i = 0; i < issues.length; i++) {
                        if (issues[i].jiraId === workItem.jiraId) {
                            deleteWorkItemFromList(issues[i]);
                            break;
                        }
                    }
                    $scope.issues.push(workItem);
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

        /* Deletes workitem from list of workitems on UI */

        var deleteWorkItemFromList = function (workItem){
            switch (workItem.type){
                case 'BUG':
                    var issueToDelete =  $scope.issues.filter(function (listWorkItem) {
                        return listWorkItem.jiraId === workItem.jiraId;
                    })[0];
                    var issueIndex = $scope.issues.indexOf(issueToDelete);
                    if (issueIndex !== -1) {
                        $scope.issues.splice(issueIndex, 1);
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

        /***/

        /** ISSUE / TASK functionality */

        var taskJiraIdInputIsChanged = false;
        var issueJiraIdInputIsChanged = false;

        /* Assigns issue to the test */

        $scope.assignIssue = function (issue) {
            if(!issue.testCaseId){
                issue.testCaseId = test.testCaseId;
            }
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
                    addTestEvent(message);
                    $scope.newIssue.id = rs.data.id;
                    updateWorkItemList(rs.data);
                    initAttachedWorkItems();
                    $scope.isNewIssue = !(jiraId === $scope.attachedIssue.jiraId);
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

        /* Assigns task to the test */

        $scope.assignTask = function (task) {
            if(!task.testCaseId){
                task.testCaseId = test.testCaseId;
            }
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
                    addTestEvent(message);
                    $scope.newTask.id = rs.data.id;
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

        /* Unassignes issue from the test */

        $scope.unassignIssue = function (workItem) {
            TestService.deleteTestWorkItem(test.id, workItem.id).then(function(rs) {
                var message;
                if(rs.success) {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign" + "e", true);
                    addTestEvent(message);
                    deleteWorkItemFromTestWorkItems(workItem);
                    initAttachedWorkItems();
                    initNewIssue();
                    alertify.success(message);
                } else {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign", false);
                    alertify.error(message);
                }
            });
        };

        /* Unassignes task from the test */

        $scope.unassignTask = function (workItem) {
            TestService.deleteTestWorkItem(test.id, workItem.id).then(function(rs) {
                var message;
                if(rs.success) {
                    message = generateActionResultMessage(workItem.type, workItem.jiraId, "unassign" + "e", true);
                    addTestEvent(message);
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

        /* Starts set in the scope issue search */

        $scope.searchScopeIssue = function (issue) {
            $scope.initIssueSearch();
            initAttachedWorkItems();
            $scope.isNewIssue = !(issue.jiraId === $scope.attachedIssue.jiraId);
            $scope.newIssue.id = issue.id;
            $scope.newIssue.jiraId = issue.jiraId;
            $scope.newIssue.description = issue.description;
        };

        /* Starts set in the scope task search */

        $scope.searchScopeTask = function (task) {
            $scope.initTaskSearch();
            initAttachedWorkItems();
            $scope.isNewTask = !(task.jiraId === $scope.attachedTask.jiraId);
            $scope.newTask.id = task.id;
            $scope.newTask.jiraId = task.jiraId;
            $scope.newTask.description = task.description;
        };

        /* Initializes issue object before search */

        $scope.initIssueSearch = function () {
            issueJiraIdInputIsChanged = true;
            $scope.newIssue.description = '';
            $scope.newIssue.id = null;
            $scope.newIssue.status = null;
            $scope.newIssue.assignee = null;
            $scope.newIssue.reporter = null;
            $scope.issueJiraIdExists = true;
            $scope.isIssueClosed = false;
            $scope.isIssueFound = false;
            $scope.isNewIssue = true;
            var existingIssue = $scope.issues.filter(function (foundIssue) {
                return foundIssue.jiraId === $scope.newIssue.jiraId;
            })[0];
            if(existingIssue){
                angular.copy(existingIssue, $scope.newIssue);
            }
        };

        /* Initializes task object before search */

        $scope.initTaskSearch = function () {
            taskJiraIdInputIsChanged = true;
            $scope.newTask.description = '';
            $scope.newTask.id = null;
            $scope.newTask.status = null;
            $scope.newTask.assignee = null;
            $scope.newTask.reporter = null;
            $scope.taskJiraIdExists = true;
            $scope.isTaskFound = false;
            $scope.isNewTask = true;
            var existingTask = $scope.tasks.filter(function (foundTask) {
                return foundTask.jiraId === $scope.newTask.jiraId;
            })[0];
            if(existingTask)
                angular.copy(existingTask, $scope.newTask);
        };

        /* Writes all attached to the test workitems into scope variables.
        Used for initialization and reinitialization */

        var initAttachedWorkItems = function () {
            $scope.testComments = [];
            var attachedWorkItem = {};
            attachedWorkItem.jiraId = '';
            $scope.attachedIssue = attachedWorkItem;
            $scope.attachedTask = attachedWorkItem;
            var workItems = $scope.test.workItems;
            for (var i = 0; i < workItems.length; i++){
                switch(workItems[i].type) {
                    case 'BUG':
                        $scope.attachedIssue = workItems[i];
                        break;
                    case 'TASK':
                        $scope.attachedTask = workItems[i];
                        break;
                    case 'COMMENT':
                        $scope.testComments.push(workItems[i]);
                        break;
                }
            }
        };

        /* Searches issue in Jira by Jira ID */

        var searchIssue = function (issue) {
            $scope.isIssueFound = false;
            $scope.issueStatusIsNotRecognized = false;
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
                    $scope.isIssueClosed = $scope.closedStatusName.toUpperCase() === searchResultIssue.status.name.toUpperCase();
                    $scope.newIssue.description = searchResultIssue.summary;
                    $scope.newIssue.assignee = searchResultIssue.assignee ? searchResultIssue.assignee.name : '';
                    $scope.newIssue.reporter = searchResultIssue.reporter.name;
                    $scope.newIssue.status = searchResultIssue.status.name.toUpperCase();
                    if(!$scope.ticketStatuses.filter(function (status) {
                            return status === $scope.newIssue.status;
                        })[0]){
                        $scope.issueStatusIsNotRecognized = true;
                    }
                    $scope.isNewIssue = !($scope.newIssue.jiraId === $scope.attachedIssue.jiraId);
                    $scope.issueTabDisabled = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /* Searches task in Jira by Jira ID */

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
                    $scope.newTask.status = searchResultTask.status.name.toUpperCase();
                    $scope.isNewTask = !($scope.newTask.jiraId === $scope.attachedTask.jiraId);
                    $scope.taskTabDisabled = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /*  Checks whether conditions for issue search in Jira are fulfilled */

        var isIssueSearchAvailable = function (jiraId) {
            if ($scope.isConnectedToJira && jiraId){
                if ($scope.issueTabDisabled || issueJiraIdInputIsChanged) {
                    issueJiraIdInputIsChanged = false;
                    return true;
                }
            } else {
                $scope.isIssueFound = true;
                return false;
            }
        };

        /*  Checks whether conditions for task search in Jira are fulfilled */

        var isTaskSearchAvailable = function (jiraId) {
            if ($scope.isConnectedToJira && jiraId){
                if ($scope.taskTabDisabled || taskJiraIdInputIsChanged) {
                    taskJiraIdInputIsChanged = false;
                    return true;
                }
            } else {
                $scope.isTaskFound = true;
                return false;
            }
        };

        /* Initializes empty issue */

        var initNewIssue = function (isInit) {
            if(isInit){
                $scope.isNewIssue = isNewIssue;
            } else {
                $scope.isNewIssue = true;
            }
            $scope.newIssue = {};
            $scope.newIssue.type = "BUG";
            $scope.newIssue.testCaseId = test.testCaseId;
        };

        /* Initializes empty task */

        var initNewTask = function (isInit) {
            if(isInit){
                $scope.isNewTask = isNewTask;
            } else {
                $scope.isNewTask = true;
            }
            $scope.newTask = {};
            $scope.newTask.type = "TASK";
            $scope.newTask.testCaseId = test.testCaseId;
        };

        /* Gets issues attached to the testcase */

        var getIssues = function () {
            TestService.getTestCaseWorkItemsByType(test.id, 'BUG').then(function(rs) {
                if(rs.success) {
                    $scope.issues = rs.data;
                    if(test.workItems.length && !$scope.isNewIssue) {
                        angular.copy($scope.attachedIssue, $scope.newIssue);
                    }
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /* Gets tasks attached to the testcase */

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

        /* Gets from DB JIRA_CLOSED_STATUS name for the current project*/

        var getJiraClosedStatusName = function() {
            SettingsService.getSetting('JIRA_CLOSED_STATUS').then(function successCallback(rs) {
                if(rs.success){
                    $scope.closedStatusName = rs.data.toUpperCase();
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /* On Jira ID input change makes search if conditions are fulfilled */

        var workItemSearchInterval = $interval(function () {
            if(issueJiraIdInputIsChanged){
                if (isIssueSearchAvailable($scope.newIssue.jiraId)) {
                    searchIssue($scope.newIssue);
                }
            }
            if(taskJiraIdInputIsChanged){
                if (isTaskSearchAvailable($scope.newTask.jiraId)) {
                    searchTask($scope.newTask);
                }
            }
        }, 2000);

        /* Closes search interval when the modal is closed */

        $scope.$on('$destroy', function() {
            if(workItemSearchInterval)
                $interval.cancel(workItemSearchInterval);
        });

        /* Sends request to Jira for issue additional info after opening modal */

        var issueOnModalOpenSearch = $interval(function(){
            if (angular.element(document.body).hasClass('md-dialog-is-showing')) {
                if (!isIssueSearchAvailable($scope.newIssue.jiraId)) {
                    $scope.issueTabDisabled = false;
                } else {
                    searchIssue($scope.newIssue);
                }
                $interval.cancel(issueOnModalOpenSearch);
            }
        }, 500);

        /* Sends request to Jira for task additional info after opening modal */

        var taskOnModalOpenSearch = $interval(function(){
            if (angular.element(document.body).hasClass('md-dialog-is-showing')) {
                if (!isTaskSearchAvailable($scope.newTask.jiraId)) {
                    $scope.taskTabDisabled = false;
                } else {
                    searchTask($scope.newTask);
                }
                $interval.cancel(taskOnModalOpenSearch);
            }
        }, 2500);

        /* COMMENT functionality */

        /* Adds comment to test (either custom or action-related) */

        $scope.addTestComment = function (message){
            var testComment = {};
            testComment.description = message;
            testComment.jiraId = Math.floor(Math.random() * 90000) + 10000;
            testComment.testCaseId = test.testCaseId;
            testComment.type = 'COMMENT';
            var eventMessage = '';
            TestService.createTestWorkItem(test.id, testComment).then(function(rs){
                if(rs.success) {
                    $scope.testComments.push(rs.data);
                    eventMessage = generateActionResultMessage(testComment.type, '', 'create', true);
                    addTestEvent(eventMessage);
                    alertify.success(eventMessage);
                } else {
                    eventMessage = generateActionResultMessage(testComment.type, '', 'create', false);
                    alertify.error('Failed to create comment for test "' + test.id);
                }
                $scope.testCommentText = '';
            })
        };

        var addTestEvent = function (message){
            var testEvent = {};
            testEvent.description = message;
            testEvent.jiraId = Math.floor(Math.random() * 90000) + 10000;
            testEvent.testCaseId = test.testCaseId;
            testEvent.type = 'EVENT';
            TestService.createTestWorkItem(test.id, testEvent).then(function(rs){
                if(rs.success) {
                } else {
                    alertify.error('Failed to add event test "' + test.id);
                }
            })
        };

        /* Generates result message for action comment (needed to be stored into DB and added in UI alert) */

        var generateActionResultMessage = function (item, id, action, success) {
            if (success) {
                return item + " " +  id +" was " + action + "d";
            } else {
                return "Failed to " + action + " " + item.toLowerCase();
            }
        };

        /* MODAL_WINDOW functionality */

        $scope.hide = function() {
            $mdDialog.hide(test);
        };

        $scope.cancel = function() {
            $mdDialog.cancel($scope.test);
        };

        (function initController() {
            if(JSON.parse(isJiraEnabled)){
                $scope.isConnectedToJira = isConnectedToJira;
            }
            getJiraClosedStatusName();
            initAttachedWorkItems();
            initNewIssue(true);
            initNewTask(true);
            getIssues();
            getTasks();
        })();
    }

})();
