(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestService])

    function TestService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTests = searchTests;
        service.updateTest = updateTest;
        service.getTestCaseWorkItemsByType = getTestCaseWorkItemsByType;
        service.createTestWorkItem = createTestWorkItem;
        service.deleteTestWorkItem = deleteTestWorkItem;
        service.getJiraTicket = getJiraTicket;
        service.getConnectionToJira = getConnectionToJira;

        return service;

        function searchTests(criteria) {
        	return $httpMock.post(API_URL + '/api/tests/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search tests'));
        }

        function updateTest(test) {
            return $httpMock.put(API_URL + '/api/tests', test).then(UtilService.handleSuccess, UtilService.handleError('Unable to update test'));
        }

        function getTestCaseWorkItemsByType(id, type) {
            return $httpMock.get(API_URL + '/api/tests/' + id + '/workitem/' + type).then(UtilService.handleSuccess, UtilService.handleError('Unable to get test work items by type'));
        }

        function createTestWorkItem(id, workItem) {
            return $httpMock.post(API_URL + '/api/tests/' + id + '/workitem', workItem).then(UtilService.handleSuccess, UtilService.handleError('Unable to create test work item'));
        }

        function deleteTestWorkItem(testId, workItemId) {
            return $httpMock.delete(API_URL + '/api/tests/' + testId + '/workitem/' + workItemId).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete test work item'));
        }

        function getJiraTicket(jiraId) {
            return $httpMock.get(API_URL + '/api/tests/jira/' + jiraId).then(UtilService.handleSuccess, UtilService.handleError('Unable to get issue from Jira'));
        }

        function getConnectionToJira() {
            return $httpMock.get(API_URL + '/api/tests/jira/connect').then(UtilService.handleSuccess, UtilService.handleError('Unable to get connection to Jira'));
        }

    }
})();
