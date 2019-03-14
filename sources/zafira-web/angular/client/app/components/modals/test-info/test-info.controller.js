(function () {
    'use strict';

    angular.module('app').controller('TestInfoController', [
        '$scope',
        '$rootScope',
        '$mdDialog',
        '$interval',
        'SettingsService',
        'TestService',
        'test',
        'isNewIssue',
        'isNewTask',
        'isConnectedToJira',
        'isJiraEnabled',
        TestInfoController]);

    function TestInfoController($scope, $rootScope, $mdDialog, $interval,  SettingsService,
                                TestService, test, isNewIssue, isNewTask, isConnectedToJira,
                                isJiraEnabled) {

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
