(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('UserService', ['$http', '$cookies', '$rootScope', 'API_URL', UserService])
 
    function UserService($http, $cookies, $rootScope, API_URL) {
        var service = {};
 
        service.GetUserProfile = GetUserProfile;
        service.UpdateUserProfile = UpdateUserProfile;
        service.UpdateUserPassword = UpdateUserPassword;
 
        return service;
 
        function GetUserProfile(username, password) {
        	return $http.get(API_URL + '/api/users/profile').then(handleSuccess, handleError('Unable to get user profile'));
        }
        
        function UpdateUserProfile(profile) {
        	return $http.put(API_URL + '/api/users/profile', profile).then(handleSuccess, handleError('Unable to update user profile'));
        }
        
        function UpdateUserPassword(password) {
        	return $http.put(API_URL + '/api/users/password', password).then(handleSuccess, handleError('Unable to update user password'));
        }
        
        // private functions
        
        function handleSuccess(res) {
            return { success: true, data: res.data };
        }
 
        function handleError(error) {
            return function () {
                return { success: false, message: error };
            };
        }
    }
})();