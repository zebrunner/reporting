(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestRunService])

    function TestRunService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTestRuns = searchTestRuns;
        service.abortTestRun = abortTestRun;
        service.abortCIJob = abortCIJob;
        service.abortDebug = abortDebug;
        service.getTestRun = getTestRun;
        service.getTestRunByCiRunId = getTestRunByCiRunId;
        service.getTestRunResults = getTestRunResults;
        service.createCompareMatrix = createCompareMatrix;
        service.deleteTestRun = deleteTestRun;
        service.sendTestRunResultsEmail = sendTestRunResultsEmail;
        service.createTestRunResultsSpreadsheet = createTestRunResultsSpreadsheet;
        service.exportTestRunResultsHTML = exportTestRunResultsHTML;
        service.markTestRunAsReviewed = markTestRunAsReviewed;
        service.rerunTestRun = rerunTestRun;
        service.debugTestRun = debugTestRun;
        service.buildTestRun = buildTestRun;
        service.getJobParameters = getJobParameters;
        service.getEnvironments = getEnvironments;
        service.getPlatforms = getPlatforms;
        service.getConsoleOutput = getConsoleOutput;

        return service;

        function searchTestRuns(criteria, filterQuery) {
            var endpoint = filterQuery ? '/api/tests/runs/search' + filterQuery : '/api/tests/runs/search';
            return $httpMock.post(API_URL + endpoint, criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search test runs'));
        }

        function abortTestRun(id, ciRunId, comment) {
            return $httpMock.post(API_URL + '/api/tests/runs/abort', comment, {params:{'id': id, 'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to abort test run'));
        }

        function getTestRun(id) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run by id'));
        }

        function getTestRunByCiRunId(ciRunId) {
            return $httpMock.get(API_URL + '/api/tests/runs', {params:{'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run by ci run id'));
        }

        function getTestRunResults(id) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + id + '/results').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run results'));
        }

        function createCompareMatrix(ids) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + ids + '/compare').then(UtilService.handleSuccess, UtilService.handleError('Unable to create compare matrix'));
        }

        function deleteTestRun(id) {
            return $httpMock.delete(API_URL + '/api/tests/runs/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete test run'));
        }

        function sendTestRunResultsEmail(id, email, filter, showStacktrace) {
            return $httpMock.post(API_URL + '/api/tests/runs/' + id + '/email', email, {params:{'filter': filter, 'showStacktrace': showStacktrace}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to send test run results email'));
        }

        function createTestRunResultsSpreadsheet(id, recipients) {
            return $httpMock.post(API_URL + '/api/tests/runs/' + id + '/spreadsheet', recipients).then(UtilService.handleSuccess, UtilService.handleError('Unable to create test run spreadsheet'));
        }

        function exportTestRunResultsHTML(id) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + id + '/export').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test run results HTML'));
        }

        function markTestRunAsReviewed(id, comment) {
            return $httpMock.post(API_URL + '/api/tests/runs/' + id + '/markReviewed', comment).then(UtilService.handleSuccess, UtilService.handleError('Unable to mark test run as reviewed'));
        }

        function rerunTestRun(id, rerunFailures) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + id + '/rerun', {params:{'rerunFailures': rerunFailures}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to rerun test run'));
        }

        function debugTestRun(id) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + id + '/debug').then(UtilService.handleSuccess, UtilService.handleError('Unable to start debug'));
        }

        function buildTestRun(id, jobParameters, buildWithParameters) {
            return $httpMock.post(API_URL + '/api/tests/runs/' + id + '/build', jobParameters).then(UtilService.handleSuccess, UtilService.handleError('Unable to build test run'));
        }

        function abortCIJob(id, ciRunId) {
            return $httpMock.get(API_URL + '/api/tests/runs/abort/ci', {params:{'id': id, 'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to abort CI Job'));
        }

        function abortDebug(id, ciRunId) {
            return $httpMock.get(API_URL + '/api/tests/runs/abort/debug', {params:{'id': id, 'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to abort debug'));
        }

        function getJobParameters(id) {
            return $httpMock.get(API_URL + '/api/tests/runs/' + id + '/jobParameters').then(UtilService.handleSuccess, UtilService.handleError('Unable to get job parameters'));
        }

        function getEnvironments() {
            return $httpMock.get(API_URL + '/api/tests/runs/environments').then(UtilService.handleSuccess, UtilService.handleError('Unable to get environments'));
        }

        function getPlatforms() {
            return $httpMock.get(API_URL + '/api/tests/runs/platforms').then(UtilService.handleSuccess, UtilService.handleError('Unable to get platforms'));
        }

        function getConsoleOutput(id, ciRunId, count, fullCount) {
            return $httpMock.get(API_URL + '/api/tests/runs/jobConsoleOutput/' + count + '/' + fullCount, {params:{'id': id, 'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to get console output'));
        }
    }
})();
