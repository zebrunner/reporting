(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('AuthService', ['$http', '$cookies', '$rootScope', 'API_URL', AuthService])
 
    function AuthService($http, $cookies, $rootScope, API_URL) {
        var service = {};
 
        service.Login = Login;
        service.SetCredentials = SetCredentials;
        service.ClearCredentials = ClearCredentials;
 
        return service;
 
        function Login(username, password) {
        	 return $http.post(API_URL + '/api/auth/login', {'username' : username, 'password': password}).then(handleSuccess, handleError('Invalid credentials'));
        }
 
        function SetCredentials(auth) {
 
            $rootScope.globals = {"auth": auth};
 
            // set default auth header for http requests
            $http.defaults.headers.common['Authorization'] = auth.type + " " + auth.accessToken;
            $cookies.putObject('globals', $rootScope.globals);
        }
 
        function ClearCredentials() {
            $rootScope.globals = {};
            $cookies.remove('globals');
            $http.defaults.headers.common.Authorization = 'Basic';
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