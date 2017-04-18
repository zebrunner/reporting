(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('UsersController', ['$scope', '$location', 'UserService', UsersController])
 
    function UsersController($scope, $location, UserService) {
 
    	$scope.user = {};
 
        (function initController() {
        	UserService.GetUserProfile()
	        	.then(function (rs) {
	        		if(rs.success)
	        		{
	        			$scope.user = rs.data;
	        		}
	            });
        })();
 
    }
 
})();