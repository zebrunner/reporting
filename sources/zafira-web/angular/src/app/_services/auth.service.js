(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('AuthService', ['$http', '$cookies', '$rootScope', '$state', 'UtilService', 'API_URL', AuthService])
 
    function AuthService($http, $cookies, $rootScope, $state, UtilService, API_URL) {
        var service = {};
 
        service.Login = Login;
        service.SetCredentials = SetCredentials;
        service.ClearCredentials = ClearCredentials;
        service.RefreshToken = RefreshToken;
        
        return service;
 
        function Login(username, password) {
        	 return $http.post(API_URL + '/api/auth/login', {'username' : username, 'password': password}).then(UtilService.handleSuccess, UtilService.handleError('Invalid credentials'));
        }
        
        function RefreshToken(token) {
	       	 return $http.post(API_URL + '/api/auth/refresh', {'refreshToken' :token}).then(UtilService.handleSuccess, UtilService.handleError('Invalid refresh token'));
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
            $http.defaults.headers.common.Authorization = null;
        }
    }
})();