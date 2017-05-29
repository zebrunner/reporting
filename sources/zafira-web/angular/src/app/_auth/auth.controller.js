(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$state', 'AuthService', 'UtilService', AuthController])
 
    function AuthController($scope, $state, AuthService, UtilService) {
 
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
               	 	$state.go('dashboard');
            	}
            	else
            	{
            		$scope.credentials = { valid: false };
            	}
            });
        };
    }
 
})();