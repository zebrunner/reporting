(function() {
    'use strict';

    angular
        .module('app.services')
        .factory('ScmService', ['$http', '$location', 'UtilService', 'API_URL', ScmService])

    function ScmService($http, $location, UtilService, API_URL) {

        var service = {};

        service.invitations = [];

        service.getClientId = getClientId;
        service.exchangeCode = exchangeCode;
        service.getRepositories = getRepositories;
        service.getOrganizations = getOrganizations;

        return service;

        function getClientId() {
            return $http.get(API_URL + '/api/scm/github/client').then(UtilService.handleSuccess, UtilService.handleError('Unable to get client id'));
        }

        function getRepositories(id, org) {
            return $http.get(API_URL + '/api/scm/github/repositories/' + id + '?org=' + org).then(UtilService.handleSuccess, UtilService.handleError('Unable to get client id'));
        }

        function getOrganizations(id) {
            return $http.get(API_URL + '/api/scm/github/organizations/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get client id'));
        }

        function exchangeCode(code) {
            return $http.get(API_URL + '/api/scm/github/exchange?code=' + code).then(UtilService.handleSuccess, UtilService.handleError('Unable to get client id'));
        }

    }
})();
