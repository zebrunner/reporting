(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('TestArtifactService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', TestArtifactService])

    function TestArtifactService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getTestArtifacts = getTestArtifacts;

        return service;

        function getTestArtifacts(id) {
            return $http.get(API_URL + '/api/tests/' + id + '/artifacts').then(UtilService.handleSuccess, UtilService.handleError('Unable to get test artifact results'));
        }
    }
})();
