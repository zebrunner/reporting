(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$location', 'AuthService', 'UtilService', AuthController])
 
    function AuthController($scope, $location, AuthService, UtilService) {
 
    	$scope.UtilService = UtilService;
    	
        $scope.credentials = { valid: true };
 
        (function initController() {
            // reset login status
        	AuthService.ClearCredentials();
        })();
 
        $scope.signin = function (credentials) {
            
        	AuthService.Login(credentials.username, credentials.password)
            .then(
            function (rs) {
            	if(rs.success)
            	{
            		AuthService.SetCredentials(rs.data);
               	 	$location.url('/');
            	}
            	else
            	{
            		$scope.credentials = { valid: false };
            	}
            });
        };
    }
 
})();