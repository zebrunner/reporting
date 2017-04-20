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

    	var DEFAULT_SC = {page : 1, pageSize : 20};
    	
    	$scope.UtilService = UtilService;
    	
    	$scope.sc = angular.copy(DEFAULT_SC);
    	$scope.users = [];
        $scope.order = 'username';

    	$scope.search = function (page) {
    		if(page)
    		{
    			$scope.sc.page = page;
    		}
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
        
        $scope.reset = function () {
        	$scope.sc = angular.copy(DEFAULT_SC);
        	$scope.search();
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
                                    alertify.success('Password changed');
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
                        UserService.createOrUpdateUser($scope.user).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('Profile changed');
                            }
                            else
                            {
                                alertify.error(rs.message);
                            }
                        });
                    };
                    $scope.deleteUser = function() {
                        UserService.deleteUser($scope.user.id).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('User deleted');
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
                }, function() {
                });
        };

        $scope.showCreateUserDialog = function(event) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog) {
                    $scope.createUser = function() {
                        UserService.createOrUpdateUser($scope.user).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('User created');
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
                templateUrl: 'app/_users/new_user_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                }, function() {
                });
        };

		(function initController() {
			$scope.search(1);
		})();
	}
})();
