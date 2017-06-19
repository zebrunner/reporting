(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestRunService])

    function TestRunService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTestRuns = searchTestRuns;
        service.startTestRun = startTestRun;
        service.updateTestRun = updateTestRun;
        service.finishTestRun = finishTestRun;
        service.abortTestRun = abortTestRun;
        service.getTestRun = getTestRun;
        service.getTestRunByCiRunId = getTestRunByCiRunId;
        service.getTestRunResults = getTestRunResults;
        service.createCompareMatrix = createCompareMatrix;
        service.deleteTestRun = deleteTestRun;
        service.sendTestRunResultsEmail = sendTestRunResultsEmail;
        service.exportTestRunResultsHTML = exportTestRunResultsHTML;
        service.markTestRunAsReviewed = markTestRunAsReviewed;
        service.rerunTestRun = rerunTestRun;
        service.buildTestRun = buildTestRun;
        service.getJobParameters = getJobParameters;
        service.getEnvironments = getEnvironments;

        return service;

        function searchTestRuns(criteria) {
            return $http.post(API_URL + '/api/tests/runs/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search test runs'));
        }

        function startTestRun(testRun, project) {
            return $http.post(API_URL + '/api/tests/runs', {headers:{'Project': project}}, testRun).then(UtilService.handleSuccess, UtilService.handleError('Unable to start test run'));
        }

        function updateTestRun(testRun) {
            return $http.put(API_URL + '/api/tests/runs', testRun).then(UtilService.handleSuccess, UtilService.handleError('Unable to update test run'));
        }

        function finishTestRun(id) {
            return $http.post(API_URL + '/api/tests/runs/' + id + '/finish').then(UtilService.handleSuccess, UtilService.handleError('Unable to finish test run'));
        }

        function abortTestRun(id, ciRunId) {
            return $http.get(API_URL + '/api/tests/runs/abort', {params:{'id': id, 'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to abort test run'));
        }

        function getTestRun(id) {
            return $http.get(API_URL + '/api/tests/runs/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run by id'));
        }

        function getTestRunByCiRunId(ciRunId) {
            return $http.get(API_URL + '/api/tests/runs', {params:{'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run by ci run id'));
        }

        function getTestRunResults(id) {
            return $http.get(API_URL + '/api/tests/runs/' + id + '/results').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run results'));
        }

        function createCompareMatrix(ids) {
            return $http.get(API_URL + '/api/tests/runs/' + ids + '/compare').then(UtilService.handleSuccess, UtilService.handleError('Unable to create compare matrix'));
        }

        function deleteTestRun(id) {
            return $http.delete(API_URL + '/api/tests/runs/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete test run'));
        }

        function sendTestRunResultsEmail(id, email, filter, showStacktrace) {
            return $http.post(API_URL + '/api/tests/runs/' + id + '/email', {params:{'filter': filter, 'showStacktrace': showStacktrace}}, email).then(UtilService.handleSuccess, UtilService.handleError('Unable to send test run results email'));
        }

        function exportTestRunResultsHTML(id) {
            return $http.get(API_URL + '/api/tests/runs/' + id + '/export').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run results HTML'));
        }

        function markTestRunAsReviewed(id, comment) {
            return $http.post(API_URL + '/api/tests/runs/' + id + '/markReviewed', comment).then(UtilService.handleSuccess, UtilService.handleError('Unable to mark test run as reviewed'));
        }

        function rerunTestRun(id, rerunFailures) {
            return $http.get(API_URL + '/api/tests/runs/' + id + '/rerun', {params:{'rerunFailures': rerunFailures}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to rerun test run'));
        }

        function buildTestRun(id, jobParameters, buildWithParameters) {
            return $http.post(API_URL + '/api/tests/runs/' + id + '/build', {params:{'buildWithParameters': buildWithParameters}}, jobParameters).then(UtilService.handleSuccess, UtilService.handleError('Unable to build test run'));
        }

        function getJobParameters(id) {
            return $http.get(API_URL + '/api/tests/runs/' + id + '/jobParameters').then(UtilService.handleSuccess, UtilService.handleError('Unable to get job parameters'));
        }

        function getEnvironments() {
            return $http.get(API_URL + '/api/tests/runs/environments').then(UtilService.handleSuccess, UtilService.handleError('Unable to get environments'));
        }
    }
})();
