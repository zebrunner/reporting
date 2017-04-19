(function () {
    'use strict';
 
    angular
        .module('app.auth')
        .controller('UserProfileController', ['$scope', '$location', 'UserService', 'UtilService', UserProfileController])
        .controller('UserListController', ['$scope', '$location', 'UserService', 'UtilService', UserListController])
 
    // **************************************************************************
    function UserProfileController($scope, $location, UserService, UtilService) {
 
    	$scope.UtilService = UtilService;
    	
    	$scope.user = {};
    	$scope.changePassword = {};
 
        (function initController() {
        	UserService.getUserProfile()
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.user = rs.data;
        		}
        		else
        		{
        			alertify.error(rs.message);
        		}
            });
        })();
        
        $scope.updateUserProfile = function(profile)
        {
        	UserService.updateUserProfile(profile)
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.user = rs.data;
        			alertify.success("Profile updated");
        		}
        		else
        		{
        			alertify.error(rs.message);
        		}
            });
        };
        
        $scope.updateUserPassword = function(changePassword)
        {
        	UserService.updateUserPassword(changePassword)
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.changePassword = {};
        			alertify.success("Password changed");
        		}
        		else
        		{
        			alertify.error(rs.message);
        		}
            });
        };
    }

    // **************************************************************************
    function UserListController($scope, $location, UserService, UtilService) {

    	$scope.sc = {page : 1, pageSize : 20};
    	$scope.users = [];
    	
    	$scope.openPage = function (page) {
			$scope.sc.page = page;
			UserService.searchUsers($scope.sc).then(function(rs) {
				if(rs.success)
        		{
        			$scope.sr = rs.data;
        		}
        		else
        		{
        			alertify.error(rs.message);
        		}
			});
        };
    	
		(function initController() {
			$scope.openPage(1);
		})();
	}
})();