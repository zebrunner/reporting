(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('MngUserService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', MngUserService])

    function MngUserService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getExtendedUserProfile = getExtendedUserProfile;

        return service;

        function getExtendedUserProfile() {
            return $http.get(API_URL + '/api/mng/users/profile').then(UtilService.handleSuccess, UtilService.handleError('Unable to get user profile'));
        }
    }
})();
