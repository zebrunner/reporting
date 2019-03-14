(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestService])

    function TestService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        const local = {
            tests: null
        }
        const service = {
            searchTests: searchTests,
            updateTest: updateTest,
            getTestCaseWorkItemsByType: getTestCaseWorkItemsByType,
            createTestWorkItem: createTestWorkItem,
            deleteTestWorkItem: deleteTestWorkItem,
            getJiraTicket: getJiraTicket,
            getConnectionToJira: getConnectionToJira,
            get getTests() {
                return local.tests;
            },
            set setTests(tests) {
                local.tests = tests;
            },
            getTest: getTest,
            clearDataCache: clearDataCache,
        }

        return service;

        function getTest(id) {
            return local.tests.find(function(test) {
                return test.id == id;
            })
        }

        function clearDataCache() {
            local.tests = null;
        }

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
