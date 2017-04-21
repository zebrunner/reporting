(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestService])

    function TestService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.searchTests = searchTests;

        return service;

        function searchTests(criteria) {
        	return $http.post(API_URL + '/api/tests/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search test cases'));
        }
    }
})();
