(function () {
    'use strict';

    angular.module('app').controller('IssuesModalController', [
        '$scope',
        '$mdDialog',
        '$interval',
        'SettingsService',
        'TestService',
        'test',
        'isNewIssue',
        'isConnectedToJira',
        'isJiraEnabled',
        IssuesModalController]);

    function IssuesModalController(
        $scope, $mdDialog, $interval, SettingsService, TestService,
        test, isNewIssue, isConnectedToJira, isJiraEnabled) {

        $scope.selectedIssue = false;

        $scope.jiraId;
        $scope.isConnectedToJira = false;

        $scope.issueJiraIdExists = false;

        $scope.issueTabDisabled = true;

        $scope.isIssueFound = true;

        $scope.isIssueClosed = false;

        $scope.test = angular.copy(test);
        $scope.testCommentText = '';
        $scope.testComments = [];
        $scope.issues = [];
        $scope.currentStatus = $scope.test.status;
        $scope.testStatuses = ['PASSED', 'FAILED', 'SKIPPED', 'ABORTED'];
        $scope.ticketStatuses = [
            'TO DO',
            'OPEN',
            'NOT ASSIGNED',
            'IN PROGRESS',
            'FIXED',
            'REOPENED',
            'DUPLICATE'];

        $scope.issueStatusIsNotRecognized = false;
        $scope.changeStatusIsVisible = false;
        $scope.issueListIsVisible = false;

        /* TEST_STATUS functionality */

        $scope.updateTest = function(test) {
            var message;
            TestService.updateTest(test).then(function(rs) {
                if (rs.success) {
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

        $scope.moveToTab = function(tabIndex) {
            $scope.selectedTabIndex = tabIndex;
        };

        /** UI methods for handling actions with ISSUE */

        /* Updates list of workitems on UI */

        var updateWorkItemList = function(workItem) {
            switch (workItem.type) {
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
            }
            $scope.test.workItems.push(workItem);
        };

        /* Deletes workitem from list of workitems on UI */

        var deleteWorkItemFromList = function(workItem) {
            switch (workItem.type) {
                case 'BUG':
                    var issueToDelete = $scope.issues.filter(
                        function(listWorkItem) {
                            return listWorkItem.jiraId === workItem.jiraId;
                        })[0];
                    var issueIndex = $scope.issues.indexOf(issueToDelete);
                    if (issueIndex !== -1) {
                        $scope.issues.splice(issueIndex, 1);
                    }
                    break;
            }
            deleteWorkItemFromTestWorkItems(workItem);
        };

        /* Deletes workitem from list of workitems in test object */

        var deleteWorkItemFromTestWorkItems = function(workItem) {
            var issueToDelete = $scope.test.workItems.filter(
                function(listWorkItem) {
                    return listWorkItem.jiraId === workItem.jiraId;
                })[0];
            var workItemIndex = $scope.test.workItems.indexOf(issueToDelete);
            if (workItemIndex !== -1) {
                $scope.test.workItems.splice(workItemIndex, 1);
            }
        };

        /***/

        /** ISSUE functionality */

        var issueJiraIdInputIsChanged = false;

        /* Assigns issue to the test */

        $scope.assignIssue = function(issue) {
            if (!issue.testCaseId) {
                issue.testCaseId = test.testCaseId;
            }
            TestService.createTestWorkItem(test.id, issue).then(function(rs) {
                var workItemType = issue.type;
                var jiraId = issue.jiraId;
                var message;
                if (rs.success) {
                    if ($scope.isNewIssue) {
                        message = generateActionResultMessage(workItemType,
                            jiraId, "assign" + "e", true);
                    } else {
                        message = generateActionResultMessage(workItemType,
                            jiraId, "update", true);
                    }
                    addTestEvent(message);
                    $scope.newIssue.id = rs.data.id;
                    updateWorkItemList(rs.data);
                    initAttachedWorkItems();
                    $scope.isNewIssue = !(jiraId ===
                        $scope.attachedIssue.jiraId);
                    alertify.success(message);
                }
                else {
                    if ($scope.isNewIssue) {
                        message = generateActionResultMessage(workItemType,
                            jiraId, "assign", false);
                    } else {
                        message = generateActionResultMessage(workItemType,
                            jiraId, "update", false);
                    }
                    alertify.error(message);
                }
            });
        };

        /* Unassignes issue from the test */

        $scope.unassignIssue = function(workItem) {
            TestService.deleteTestWorkItem(test.id, workItem.id).
                then(function(rs) {
                    var message;
                    if (rs.success) {
                        message = generateActionResultMessage(workItem.type,
                            workItem.jiraId, "unassign" + "e", true);
                        addTestEvent(message);
                        deleteWorkItemFromTestWorkItems(workItem);
                        initAttachedWorkItems();
                        initNewIssue();
                        $scope.selectedIssue = false;
                        alertify.success(message);
                    } else {
                        message = generateActionResultMessage(workItem.type,
                            workItem.jiraId, "unassign", false);
                        alertify.error(message);
                    }
                    $scope.issueJiraIdExists = false;
                });
        };

        /* Starts set in the scope issue search */

        $scope.searchScopeIssue = function(issue) {
            $scope.initIssueSearch();
            initAttachedWorkItems();
            $scope.isNewIssue = !(issue.jiraId === $scope.attachedIssue.jiraId);
            $scope.newIssue.id = issue.id;
            $scope.newIssue.jiraId = issue.jiraId;
            $scope.newIssue.description = issue.description;
            $scope.selectedIssue = true;
        };

        $scope.clearIssue = function() {
            initNewIssue();
            getIssues();
            $scope.issueJiraIdExists = false;
            $scope.selectedIssue = false;
        };

        /* Initializes issue object before search */

        $scope.initIssueSearch = function(isInvalid) {
            if (isInvalid) {
                return;
            }
            $scope.issueJiraIdExists = false;
            issueJiraIdInputIsChanged = true;
            $scope.newIssue.description = '';
            $scope.newIssue.id = null;
            $scope.newIssue.status = null;
            $scope.newIssue.assignee = null;
            $scope.newIssue.reporter = null;
            $scope.isIssueClosed = false;
            $scope.isIssueFound = false;
            $scope.isNewIssue = true;
            var existingIssue = $scope.issues.filter(function(foundIssue) {
                return foundIssue.jiraId === $scope.newIssue.jiraId;
            })[0];
            if (existingIssue) {
                angular.copy(existingIssue, $scope.newIssue);
            }            
        };

        /* Writes all attached to the test workitems into scope variables.
        Used for initialization and reinitialization */

        var initAttachedWorkItems = function() {
            $scope.testComments = [];
            var attachedWorkItem = {};
            attachedWorkItem.jiraId = '';
            $scope.attachedIssue = attachedWorkItem;
            var workItems = $scope.test.workItems;
            for (var i = 0; i < workItems.length; i++) {
                switch (workItems[i].type) {
                    case 'BUG':
                        $scope.attachedIssue = workItems[i];
                        break;
                }
            }
        };

        /* Searches issue in Jira by Jira ID */

        var searchIssue = function(issue) {
            $scope.isIssueFound = false;
            $scope.issueStatusIsNotRecognized = false;
            TestService.getJiraTicket(issue.jiraId).then(function(rs) {
                if (rs.success) {
                    var searchResultIssue = rs.data;
                    $scope.isIssueFound = true;
                    if (searchResultIssue === '') {
                        $scope.isIssueClosed = false;
                        $scope.issueJiraIdExists = false;
                        $scope.issueTabDisabled = false;
                        return;
                    }
                    $scope.issueJiraIdExists = true;
                    $scope.isIssueClosed = $scope.closedStatusName.toUpperCase() ===
                        searchResultIssue.status.name.toUpperCase();
                    $scope.newIssue.description = searchResultIssue.summary;
                    $scope.newIssue.assignee = searchResultIssue.assignee
                        ? searchResultIssue.assignee.name
                        : '';
                    $scope.newIssue.reporter = searchResultIssue.reporter.name;
                    $scope.newIssue.status = searchResultIssue.status.name.toUpperCase();
                    if (!$scope.ticketStatuses.filter(function(status) {
                        return status === $scope.newIssue.status;
                    })[0]) {
                        $scope.issueStatusIsNotRecognized = true;
                    }
                    $scope.isNewIssue = !($scope.newIssue.jiraId ===
                        $scope.attachedIssue.jiraId);
                    $scope.issueTabDisabled = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        /*  Checks whether conditions for issue search in Jira are fulfilled */

        var isIssueSearchAvailable = function(jiraId) {
            if ($scope.isConnectedToJira && jiraId) {
                if ($scope.issueTabDisabled || issueJiraIdInputIsChanged) {
                    issueJiraIdInputIsChanged = false;
                    return true;
                }
            } else {
                $scope.isIssueFound = true;
                return false;
            }
        };


        /* Initializes empty issue */

        var initNewIssue = function(isInit) {
            if (isInit) {
                $scope.isNewIssue = isNewIssue;
            } else {
                $scope.isNewIssue = true;
            }
            $scope.newIssue = {};
            $scope.newIssue.type = "BUG";
            $scope.newIssue.testCaseId = test.testCaseId;
        };

        /* Gets issues attached to the testcase */

        var getIssues = function() {
            TestService.getTestCaseWorkItemsByType(test.id, 'BUG').
                then(function(rs) {
                    if (rs.success) {
                        $scope.issues = rs.data;
                        if (test.workItems.length && !$scope.isNewIssue) {
                            angular.copy($scope.attachedIssue, $scope.newIssue);
                        }
                    } else {
                        alertify.error(rs.message);
                    }
                });
        };

        /* Gets from DB JIRA_CLOSED_STATUS name for the current project*/

        var getJiraClosedStatusName = function() {
            SettingsService.getSetting('JIRA_CLOSED_STATUS').
                then(function successCallback(rs) {
                    if (rs.success) {
                        $scope.closedStatusName = rs.data.toUpperCase();
                    } else {
                        alertify.error(rs.message);
                    }
                });
        };

        /* On Jira ID input change makes search if conditions are fulfilled */

        var workItemSearchInterval = $interval(function() {
            if (issueJiraIdInputIsChanged) {
                if (isIssueSearchAvailable($scope.newIssue.jiraId)) {
                    searchIssue($scope.newIssue);
                }
            }
        }, 2000);

        /* Closes search interval when the modal is closed */

        $scope.$on('$destroy', function() {
            if (workItemSearchInterval)
                $interval.cancel(workItemSearchInterval);
        });

        /* Sends request to Jira for issue additional info after opening modal */

        var issueOnModalOpenSearch = $interval(function() {
            if (angular.element(document.body).
                hasClass('md-dialog-is-showing')) {
                if (!isIssueSearchAvailable($scope.newIssue.jiraId)) {
                    $scope.issueTabDisabled = false;
                } else {
                    searchIssue($scope.newIssue);
                }
                $interval.cancel(issueOnModalOpenSearch);
            }
        }, 500);


        var addTestEvent = function(message) {
            var testEvent = {};
            testEvent.description = message;
            testEvent.jiraId = Math.floor(Math.random() * 90000) + 10000;
            testEvent.testCaseId = test.testCaseId;
            testEvent.type = 'EVENT';
            TestService.createTestWorkItem(test.id, testEvent).
                then(function(rs) {
                    if (rs.success) {
                    } else {
                        alertify.error('Failed to add event test "' + test.id);
                    }
                })
        };

        /* Generates result message for action comment (needed to be stored into DB and added in UI alert) */

        var generateActionResultMessage = function(item, id, action, success) {
            if (success) {
                return item + " " + id + " was " + action + "d";
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
            if (JSON.parse(isJiraEnabled)) {
                $scope.isConnectedToJira = isConnectedToJira;
            }
            getJiraClosedStatusName();
            initAttachedWorkItems();
            initNewIssue();
            getIssues();
        })();
    }

})();
