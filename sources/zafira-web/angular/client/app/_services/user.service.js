(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('UserService', ['$http', '$cookies', '$rootScope', 'API_URL', UserService])
 
    function UserService($http, $cookies, $rootScope, API_URL) {
        var service = {};
 
        service.GetUserProfile = GetUserProfile;
 
        return service;
 
        function GetUserProfile(username, password) {
        	console.log($http.defaults.headers.common); 
        	return $http.get(API_URL + '/api/users/profile').then(handleSuccess, handleError('Unable to get user profile'));
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