(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$location', 'AuthService', AuthController])
 
    function AuthController($scope, $location, AuthService) {
 
        $scope.credentials = { valid: true };
 
        (function initController() {
            // reset login status
        	AuthService.ClearCredentials();
        })();
 
        $scope.signin = function () {
            
        	AuthService.Login($scope.credentials.username, $scope.credentials.password)
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