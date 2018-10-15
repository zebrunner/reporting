(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TenancyService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', TenancyService])

    function TenancyService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.createTenancy = createTenancy;
        service.getAllTenancies = getAllTenancies;
        service.updateTenancy = updateTenancy;

        return service;

        function createTenancy(tenancy) {
            return $httpMock.post(API_URL + '/api/mng/tenancies', tenancy).then(UtilService.handleSuccess, UtilService.handleError('Unable to create tenancy'));
        }

        function getAllTenancies() {
            return $httpMock.get(API_URL + '/api/mng/tenancies/all').then(UtilService.handleSuccess, UtilService.handleError('Unable to get all tenancies'));
        }

        function updateTenancy(tenancy) {
            return $httpMock.put(API_URL + '/api/mng/tenancies', tenancy).then(UtilService.handleSuccess, UtilService.handleError('Unable to update tenancy'));
        }
    }
})();
