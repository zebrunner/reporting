(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestRunService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestCaseService])

    function TestCaseService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTestCases = searchTestCases;

        return service;

        function searchTestCases(criteria) {
            return $http.post(API_URL + '/api/tests/cases/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search test cases'));
        }
    }
})();
