(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestService])

    function TestService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTests = searchTests;
        service.markTestAsPassed = markTestAsPassed;
        service.getTestKnownIssues = getTestKnownIssues;
        service.createTestKnownIssue = createTestKnownIssue;
        service.assignTestJiraTask = assignTestJiraTask;
        service.deleteTestKnownIssue = deleteTestKnownIssue;
        service.getJiraIssue = getJiraIssue;
        service.getConnectionToJira = getConnectionToJira;
        service.deleteTestJiraTask = deleteTestJiraTask;

        return service;

        function searchTests(criteria) {
        	return $http.post(API_URL + '/api/tests/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search tests'));
        }

        function markTestAsPassed(id) {
            return $http.post(API_URL + '/api/tests/' + id + '/passed').then(UtilService.handleSuccess, UtilService.handleError('Unable to mark test as passed'));
        }

        function getTestKnownIssues(id) {
            return $http.get(API_URL + '/api/tests/' + id + '/issues').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test known issues list'));
        }

        function createTestKnownIssue(id, workItem) {
            return $http.post(API_URL + '/api/tests/' + id + '/issues', workItem).then(UtilService.handleSuccess, UtilService.handleError('Unable to create test work item'));
        }

        function assignTestJiraTask(id, workItem) {
            return $http.post(API_URL + '/api/tests/' + id + '/task', workItem).then(UtilService.handleSuccess, UtilService.handleError('Unable to create test work item'));
        }

        function deleteTestJiraTask(id, workItemId) {
                    return $http.delete(API_URL + '/api/tests/' + id + '/task/'+ workItemId).then(UtilService.handleSuccess, UtilService.handleError('Unable to unassign test work item'));
                }

        function deleteTestKnownIssue(testId, workItemId) {
            return $http.delete(API_URL + '/api/tests/' + testId + '/issues/' + workItemId).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete test work item'));
        }

        function getJiraIssue(jiraId) {
            return $http.get(API_URL + '/api/tests/jira/' + jiraId).then(UtilService.handleSuccess, UtilService.handleError('Unable to get issue from Jira'));
        }

        function getConnectionToJira() {
            return $http.get(API_URL + '/api/tests/jira/connect').then(UtilService.handleSuccess, UtilService.handleError('Unable to get connection to Jira'));
        }
    }
})();
