(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestCaseService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestCaseService])

    function TestCaseService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTestCases = searchTestCases;
        service.getTestMetricsByTestCaseId = getTestMetricsByTestCaseId;

        return service;

        function searchTestCases(criteria) {
            return $http.post(API_URL + '/api/tests/cases/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search test cases'));
        }

        function getTestMetricsByTestCaseId(id) {
            return $http.get(API_URL + '/api/tests/cases/' + id + '/metrics/').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test metrics'));
        }
    }
})();
