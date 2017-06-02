(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$rootScope', '$state', '$cookies', 'AuthService', 'UserService', 'UtilService', AuthController])
 
    function AuthController($scope, $rootScope, $state, $cookies, AuthService, UserService, UtilService) {
 
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
            		UserService.getUserProfile()
            		 .then(
            		  function (rs) {
		              if(rs.success)
		              {
		            	  $rootScope.user = rs.data;
		            	  $cookies.putObject('user', $rootScope.user);
		            	  $state.go('dashboard');
		              }
            		});
            	}
            	else
            	{
            		$scope.credentials = { valid: false };
            	}
            });
        };
    }
 
})();