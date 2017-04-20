(function () {
    'use strict';

    angular
        .module('app.auth')
        .controller('UserProfileController', ['$scope', '$location', 'UserService', 'UtilService', UserProfileController])
        .controller('UserListController', ['$scope', '$location', '$mdDialog', 'UserService', 'UtilService', UserListController])

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
    function UserListController($scope, $location, $mdDialog, UserService, UtilService) {

    	$scope.sc = {page : 1, pageSize : 20};
    	$scope.users = [];
        $scope.order = 'username';

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

        $scope.showChangePasswordDialog = function($event, user) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog) {
                    $scope.user = user;
                    $scope.updateUserPassword = function(changePassword)
                    {
                        UserService.updateUserPassword(changePassword)
                            .then(function (rs) {
                                if(rs.success)
                                {
                                    $scope.changePassword = {};
                                    $scope.hide();
                                    alertify.success("Password changed");
                                }
                                else
                                {
                                    alertify.error(rs.message);
                                }
                            });
                    };
                    $scope.hide = function() {
                        $mdDialog.hide();
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                },
                templateUrl: 'app/_users/password_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showEditProfileDialog = function(event, user) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog) {
                    $scope.user = angular.copy(user);
                    $scope.updateUser = function() {
                        UserService.updateUser($scope.user).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                            }
                            else
                            {
                                alertify.error(rs.message);
                            }
                        });
                    };
                    $scope.hide = function() {
                        $mdDialog.hide($scope.user);
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                },
                templateUrl: 'app/_users/profile_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                    angular.copy(answer, user);
                }, function() {
                });
        };

		(function initController() {
			$scope.openPage(1);
		})();
	}
})();
