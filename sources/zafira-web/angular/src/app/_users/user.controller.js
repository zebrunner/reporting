(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('UsersController', ['$scope', '$location', 'UserService', UsersController])
 
    function UsersController($scope, $location, UserService) {
 
    	$scope.user = {};
    	
    	$scope.password = '';
 
        (function initController() {
        	UserService.GetUserProfile()
        	.then(function (rs) {
        		if(rs.success)
        			$scope.user = rs.data;
        		else
        			alertify.error(rs.message);
            });
        })();
        
        $scope.updateUserProfile = function(profile)
        {
        	UserService.UpdateUserProfile(profile)
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.user = rs.data;
        			alertify.success("User profile updated");
        		}
        		else
        			alertify.error(rs.message);
            });
        };
        
        $scope.updateUserPassword = function(password, confirmPassword)
        {
        	UserService.UpdateUserPassword({'password' : password, 'confirmPassword' : confirmPassword})
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.user = rs.data;
        			alertify.success("User password updated");
        		}
        		else
        			alertify.error(rs.message);
            });
        };
    }
 
})();