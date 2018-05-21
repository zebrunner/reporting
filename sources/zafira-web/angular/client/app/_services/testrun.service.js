(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestRunService])

    function TestRunService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTestRuns = searchTestRuns;
        service.abortTestRun = abortTestRun;
        service.abortCIJob = abortCIJob;
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
        service.getPlatforms = getPlatforms;
        service.getConsoleOutput = getConsoleOutput;
        service.getDebugConsoleOutput = getDebugConsoleOutput;

        return service;

        function searchTestRuns(criteria, filterQuery) {
            var endpoint = filterQuery ? '/api/tests/runs/search' + filterQuery : '/api/tests/runs/search';
            return $http.post(API_URL + endpoint, criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search test runs'));
        }

        function abortTestRun(id, ciRunId, comment) {
            return $http.post(API_URL + '/api/tests/runs/abort', comment, {params:{'id': id, 'ciRunId': ciRunId}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to abort test run'));
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
            return $http.post(API_URL + '/api/tests/runs/' + id + '/email', email, {params:{'filter': filter, 'showStacktrace': showStacktrace}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to send test run results email'));
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
            return $http.post(API_URL + '/api/tests/runs/' + id + '/build', jobParameters).then(UtilService.handleSuccess, UtilService.handleError('Unable to build test run'));
        }

        function abortCIJob(id, debug) {
            return $http.get(API_URL + '/api/tests/runs/' + id + '/abort', {params:{'debug': debug}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to abort CI Job'));
        }

        function getJobParameters(id) {
            return $http.get(API_URL + '/api/tests/runs/' + id + '/jobParameters').then(UtilService.handleSuccess, UtilService.handleError('Unable to get job parameters'));
        }

        function getEnvironments() {
            return $http.get(API_URL + '/api/tests/runs/environments').then(UtilService.handleSuccess, UtilService.handleError('Unable to get environments'));
        }

        function getPlatforms() {
            return $http.get(API_URL + '/api/tests/runs/platforms').then(UtilService.handleSuccess, UtilService.handleError('Unable to get platforms'));
        }

        function getConsoleOutput(testRunId, count, fullCount) {
            return $http.get(API_URL + '/api/tests/runs/' + testRunId + '/jobConsoleOutput/' + count + '/' + fullCount).then(UtilService.handleSuccess, UtilService.handleError('Unable to get console output'));
        }

        function getDebugConsoleOutput(testRunId) {
            return $http.get(API_URL + '/api/tests/runs/' + testRunId + '/debug').then(UtilService.handleSuccess, UtilService.handleError('Unable to start debug'));
        }

    }
})();
