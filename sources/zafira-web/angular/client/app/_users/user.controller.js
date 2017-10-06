(function () {
    'use strict';

    angular
        .module('app.user')
        .controller('UserProfileController', ['$scope', '$rootScope', '$location', '$state', 'UserService', 'DashboardService', 'UtilService', 'AuthService', UserProfileController])
        .controller('UserListController', ['$scope', '$rootScope', '$location', '$state', '$mdDialog', 'UserService', 'GroupService', 'UtilService', 'DashboardService', UserListController])

    // **************************************************************************
    function UserProfileController($scope, $rootScope, $location, $state, UserService, DashboardService, UtilService, AuthService) {

    	$scope.UtilService = UtilService;

    	$scope.user = {};
    	$scope.changePassword = {};
        $scope.preferences = [];
        $scope.preferenceForm = {};
        $scope.dashboards = [];
    	$scope.pefrDashboardId = null;
    	$scope.accessToken = null;


        $scope.isAdmin = function(){
             return AuthService.UserHasPermission(["ROLE_ADMIN"]);
        };

        $scope.updateUserProfile = function(profile)
        {
            var userPreferences = profile.preferences;
            profile.preferences.push($scope.preferences);
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

        $scope.loadDashboards = function () {

            if ($scope.isAdmin() === true) {
                DashboardService.GetDashboards().then(function (rs) {
                    if (rs.success) {
                        $scope.dashboards = rs.data;
                    }
                });
            }
            else {
                var hidden = true;
                DashboardService.GetDashboards(hidden).then(function (rs) {
                    if (rs.success) {
                        $scope.dashboards = rs.data;
                    }
                });
            }
        };

        $scope.updateUserPassword = function(changePassword) {
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

        $scope.getUserProfile = function(){
            UserService.getUserProfile()
                .then(function (rs) {
                    if(rs.success)
                    {
                        $scope.user = rs.data;
                        $scope.changePassword.userId = $scope.user.id;
                        if($scope.user.preferences.length !== 0){
                            $scope.preferences = $scope.user.preferences;
                        }
                        else {
                            $scope.getDefaultPreferences();
                        }
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
        };

        $scope.getDefaultPreferences = function(){
            UserService.getDefaultPreferences()
                .then(function (rs) {
                    if(rs.success)
                    {
                        $scope.preferences = rs.data;
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
        };

        $scope.updateUserPreferences = function (preferenceForm){
            var preferences = $scope.preferences;
            for (var i = 0; i < preferences.length; i++){
                preferences[i].userId = $scope.user.id;
                if (preferences[i].name === 'DEFAULT_DASHBOARD'){
                    preferences[i].value = preferenceForm.defaultDashboard;
                }
                else if (preferences[i].name === 'REFRESH_INTERVAL'){
                    preferences[i].value = preferenceForm.refreshInterval;
                }
            }
            UserService.updateUserPreferences($scope.user.id, preferences).then(function (rs) {
                if(rs.success){
                    $scope.preferences = rs.data;
                    $rootScope.$broadcast('event:preferencesReset');
                    alertify.success('User preferences are successfully updated');
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.resetPreferences = function(){
            UserService.deleteUserPreferences($scope.user.id).then(function (rs) {
                if(rs.success){
                    $rootScope.$broadcast('event:preferencesReset');
                    alertify.success('Preferences are set to default');
                }
            else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.widgetRefreshIntervals = [0, 30000, 60000, 120000, 300000];

        $scope.selectDashboard = function(dashboard){
            if ($rootScope.defaultDashboard === dashboard.title){
                return true;
            }
        };

        $scope.selectInterval = function(interval){
                if ($rootScope.refreshInterval == interval){
                    return true;
            }
         };

        $scope.convertMillis = function(millis){
            var sec = millis/1000;
            if (millis == 0) {
                return 'Disabled'
            }
            else if(sec < 60){
                return sec + ' sec';
            }
            else {
                return sec/60 + ' min';
            }
        };

        (function initController() {
            $scope.loadDashboards();
            $scope.getUserProfile();
        })();

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
            $scope.sc.date = null;
            $scope.sc.toDate = null;
            $scope.sc.fromDate = null;

            if(page)
            {
                $scope.sc.page = page;
            }

            if ($scope.sc.period == ""){
                $scope.sc.date = $scope.sc.chosenDate;
            }
            else if ($scope.sc.period == "before"){
                $scope.sc.toDate =  $scope.sc.chosenDate;
            }
            else if ($scope.sc.period == "after") {
                $scope.sc.fromDate = $scope.sc.chosenDate;
            }
            else if ($scope.sc.period == "between") {
                $scope.sc.fromDate = $scope.sc.chosenDate;
                $scope.sc.toDate =  $scope.sc.endDate;
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

        $scope.isDateChosen = true;
        $scope.isDateBetween = false;

        $scope.changePeriod = function () {
            if ($scope.sc.period == "between") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = true;
            }
            else if ($scope.sc.period == "before" || $scope.sc.period == "after" || $scope.sc.period == "") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = false;
            }
            else {
                $scope.isDateChosen = false;
                $scope.isDateBetween = false;
            }
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
