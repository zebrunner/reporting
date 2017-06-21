(function () {
    'use strict';

    angular
        .module('app.user')
        .controller('UserProfileController', ['$scope', '$location', '$state', 'UserService', 'UtilService', 'AuthService', UserProfileController])
        .controller('UserListController', ['$scope', '$rootScope', '$location', '$state', '$mdDialog', 'UserService', 'GroupService', 'UtilService', 'DashboardService', UserListController])

    // **************************************************************************
    function UserProfileController($scope, $location, $state, UserService, UtilService, AuthService) {

    	$scope.UtilService = UtilService;

    	$scope.user = {};
    	$scope.changePassword = {};
    	$scope.pefrDashboardId = null;
    	$scope.accessToken = null;

        (function initController() {
        	UserService.getUserProfile()
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.user = rs.data;
        			$scope.changePassword.userId = $scope.user.id;
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
        
        $scope.generateAccessToken = function()
        {
        	AuthService.GenerateAccessToken()
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.accessToken = rs.data.token;
        		}
            });
        };
        
        $scope.copyAccessToken = function (accessToken) {
            var node = document.createElement('pre');
            node.textContent = accessToken;
            document.body.appendChild(node);

            var selection = getSelection();
            selection.removeAllRanges();

            var range = document.createRange();
            range.selectNodeContents(node);
            selection.addRange(range);

            document.execCommand('copy');
            selection.removeAllRanges();
            document.body.removeChild(node);
            
            alertify.success("Access token copied to clipboard");
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
    function UserListController($scope, $rootScope, $location, $state, $mdDialog, UserService, GroupService, UtilService, DashboardService) {

    	var DEFAULT_SC = {page : 1, pageSize : 20};

    	$scope.UtilService = UtilService;
    	$scope.DashboardService = DashboardService;

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
                    $scope.changePassword = {'userId' : user.id};
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
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel(false);
                    };
                },
                templateUrl: 'app/_users/password_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
            .then(function(answer) {
            	if(answer)
            	{
            		$state.reload();
            	}
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
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel(false);
                    };
                },
                templateUrl: 'app/_users/edit_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                	if(answer)
                	{
                		$state.reload();
                	}
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
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel(false);
                    };
                },
                templateUrl: 'app/_users/create_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                	if(answer)
                	{
                		$state.reload();
                	}
                }, function() {
                });
        };

        $scope.showGroupDialog = function(event) {
            $mdDialog.show({
                controller: GroupController,
                templateUrl: 'app/_users/group_modal.html',
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
//			 DashboardService.GetDashboards("USER_PERFORMANCE").then(function(rs) {
//                if(rs.success && rs.data.length > 0)
//                {
//                	$scope.pefrDashboardId = rs.data[0].id;
//                }
//            });
		})();
	}

    // **************************************************************************
    function GroupController($scope, $mdDialog, UserService, GroupService, UtilService) {
        $scope.UtilService = UtilService;
        $scope.group = {};
        $scope.groups = [];
        $scope.roles = [];
        $scope.group.users = [];
        $scope.showGroups = false;
        $scope.getRoles = function() {
            GroupService.getRoles().then(function(rs) {
                if(rs.success)
                {
                    $scope.roles = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.getGroupsCount = function() {
            GroupService.getGroupsCount().then(function(rs) {
                if(rs.success)
                {
                    $scope.count = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.getAllGroups = function() {
            GroupService.getAllGroups().then(function(rs) {
                if(rs.success)
                {
                    $scope.groups = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.createGroup = function(group) {
            GroupService.createGroup(group).then(function(rs) {
                if(rs.success)
                {
                    $scope.group = {};
                    $scope.getAllGroups();
                    alertify.success('Group "' + group.name + '" was created');
                    $scope.count ++;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.getGroup = function(id) {
            GroupService.getGroup(id).then(function(rs) {
                if(rs.success)
                {
                    $scope.group = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.updateGroup = function(group) {
            GroupService.updateGroup(group).then(function(rs) {
                if(rs.success)
                {
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.deleteGroup = function(group) {
            GroupService.deleteGroup(group.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.getAllGroups();
                    $scope.count --;
                    alertify.success('Group "' + group.name + '" was deleted');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.addUserToGroup = function(user, group) {
            UserService.addUserToGroup(user, group.id).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('User "' + user.username + '" was added to group "' + group.name + '"');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.deleteUserFromGroup = function(user, group) {
            UserService.deleteUserFromGroup(user.id, group.id).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('User "' + user.username + '" was deleted from group "' + group.name + '"');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.usersSearchCriteria = {};
        $scope.querySearch = querySearch;
        function querySearch (criteria, group) {
            $scope.usersSearchCriteria.username = criteria;
            return UserService.searchUsersWithQuery($scope.usersSearchCriteria).then(function(rs) {
                if(rs.success)
                {
                    console.log(Array.isArray(rs.data.results));
                    return rs.data.results.filter(searchFilter(group));
                }
                else
                {
                    alertify.error(rs.message);
                }
            }).finally(function (rs) {
            });
        }
        function searchFilter(group) {
            return function filterFn(user) {
                var users = group.users;
                for(var i = 0; i < users.length; i++) {
                    if(users[i].id == user.id) {
                        return false;
                    }
                }
                return true;
            };
        }
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        (function initController() {
            $scope.getRoles();
            $scope.getGroupsCount();
            $scope.getAllGroups();
        })();
    }
})();
