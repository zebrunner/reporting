(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ConfigService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', ConfigService])

    function ConfigService($httpMock, $cookies, $rootScope, UtilService, API_URL) {

        var service = {};

        service.getConfig = getConfig;

        return service;

        function getConfig(name) {
            return $httpMock.get(API_URL + '/api/config/' + name).then(UtilService.handleSuccess, UtilService.handleError('Unable to get config "' + name + '"'));
        }
    }
})();
