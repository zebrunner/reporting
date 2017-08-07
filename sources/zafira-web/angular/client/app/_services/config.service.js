(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ConfigService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', ConfigService])

    function ConfigService($http, $cookies, $rootScope, UtilService, API_URL) {

        var service = {};

        service.getConfig = getConfig;

        return service;

        function getConfig(name) {
            return $http.get(API_URL + '/api/config/' + name).then(UtilService.handleSuccess, UtilService.handleError('Unable to get config "' + name + '"'));
        }
    }
})();
