(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('CertificationService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', CertificationService])

    function CertificationService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.loadCertificationDetails = loadCertificationDetails;

        return service;

        function loadCertificationDetails(upstreamJobId, upstreamJobBuildNumber) {
        	return $http.get(API_URL + '/api/certification/details', {params:{'upstreamJobId': upstreamJobId, 'upstreamJobBuildNumber': upstreamJobBuildNumber}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to load certification details'));
        }
    }
})();
