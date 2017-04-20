(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('UserService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', UserService])

    function UserService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getUserProfile = getUserProfile;
        service.searchUsers = searchUsers;
        service.updateUserProfile = updateUserProfile;
        service.updateUserPassword = updateUserPassword;
        service.updateUser = updateUser;

        return service;

        function getUserProfile(username, password) {
        	return $http.get(API_URL + '/api/users/profile').then(UtilService.handleSuccess, UtilService.handleError('Unable to get user profile'));
        }

        function searchUsers(criteria) {
        	return $http.post(API_URL + '/api/users/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search users'));
        }

        function updateUserProfile(profile) {
        	return $http.put(API_URL + '/api/users/profile', profile).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user profile'));
        }

        function updateUserPassword(password) {
        	return $http.put(API_URL + '/api/users/password', password).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user password'));
        }

        function updateUser(user){
            return $http.put(API_URL + '/users', user).then(UtilService.handleSuccess, UtilService.handleError('Failed to update user'));
        }
    }
})();
