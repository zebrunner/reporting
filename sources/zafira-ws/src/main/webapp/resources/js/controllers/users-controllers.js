'use strict';

ZafiraApp.controller('UsersListCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$modal', '$route', 'DashboardService', '$q', '$timeout', function($scope, $rootScope, $http, $location, $modal, $route, DashboardService, $q, $timeout) {

	const DEFAULT_SC = {
			'page' : 1,
			'pageSize' : 20
	};
	
	$scope.usersSearchCriteria = angular.copy(DEFAULT_SC);
	
	$scope.totalResults = 0;
	$scope.users = [];
	$scope.pefrDashboardId = null;
	
	$scope.loadUsers = function(page, pageSize){
		
		$scope.usersSearchCriteria.page = page;
		if(pageSize)
		{
			$scope.usersSearchCriteria.pageSize = pageSize;
		}
		
		$http.post('users/search', $scope.usersSearchCriteria).then(function successCallback(data) {
			var data = data.data;
			$scope.usersSearchCriteria.page = data.page;
			$scope.usersSearchCriteria.pageSize = data.pageSize;
			$scope.users = data.results;
			$scope.totalResults = data.totalResults;
		}, function errorCallback(data) {
			console.error('Failed to search users');
		});
	};
	
	$scope.openUserDetailsModal = function(id){
		if(id)
		{
			$http.get('users/' + id).then(function successCallback(user) {
				var user = user.data;
				$modal.open({
					templateUrl : 'resources/templates/user-details-modal.jsp',
					resolve : {
						'id' : function(){
							return id;
						},
						'user' : function(){
							return user;
						}
					},
					controller : function($scope, $modalInstance, id, user){
						
						$scope.id = id;
						$scope.user = user;
		
						$scope.updateUser = function(user){
							$http.put('users', user).then(function successCallback(data) {
								$route.reload();
							}, function errorCallback(data) {
								alertify.error('Failed to update user');
							});
							$modalInstance.close(0);
						};
						
						$scope.deleteUser = function(user){
							$http.delete('users/' + user.id).then(function successCallback(data) {
								$route.reload();
							}, function errorCallback(data) {
								alertify.error('Failed to delete user');
							});
							$modalInstance.close(0);
						};
						
						$scope.cancel = function(){
							$modalInstance.close(0);
						};
					}
				});
			}, function errorCallback(data) {
				console.error('Failed to load user');
			}).result.then(function(data) {
            }, function () {
            });
		}
		else
		{
			$modal.open({
				templateUrl : 'resources/templates/user-details-modal.jsp',
				controller : function($scope, $modalInstance){
					
					$scope.user = {};
	
					$scope.updateUser = function(user){
						$http.put('users', user).then(function successCallback(data) {
							$route.reload();
						}, function errorCallback(data) {
							alertify.error('Failed to update user');
						});
						$modalInstance.close(0);
					};
					
					$scope.cancel = function(){
						$modalInstance.close(0);
					};
				}
			}).result.then(function(data) {
            }, function () {
            });
		}
	};

    $scope.openGroupDetailsModal = function(){
		$modal.open({
			templateUrl : 'resources/templates/group-details-modal.jsp',
			controller : function($scope, $modalInstance){

				$scope.group = {};
				$scope.groups = [];
				$scope.group.users = [];
				loadRoles();
				getGroupsCount();
				getAllGroups();

				$scope.showGroups = false;

                $scope.createGroup = function(group){
                    $http.post('groups', group).then(function successCallback(data) {
                    	getAllGroups();
                    	$scope.group.name = "";
                    	$scope.group.role = "";
                    	$scope.count ++;
                    	alertify.success("Group " + group.name + " was created");
                    }, function errorCallback(data) {
                        alertify.error('Failed to create group');
                    });
                };

                $scope.getGroupById = function(id){
                    $http.get('groups/' + id).then(function successCallback(data) {
                    }, function errorCallback(data) {
                        alertify.error('Failed to get group ' + id);
                    });
                };

                 function getAllGroups(){
                    $http.get('groups/all').then(function successCallback(data) {
                    	$scope.groups = data.data;
                    }, function errorCallback(data) {
                        alertify.error('Failed to load groups');
                    });
                };

                function getGroupsCount() {
                    $http.get('groups/count').then(function successCallback(data) {
                        $scope.count = data.data;
                    }, function errorCallback(data) {
                        alertify.error('Failed to load groups');
                    });
                };

                function loadRoles(){
                    $http.get('groups/roles').then(function successCallback(data) {
                    	$scope.roles = data.data;
                    }, function errorCallback(data) {
                        alertify.error('Failed to load roles');
                    });
                };

				$scope.updateGroup = function(group){
					$http.put('groups', group).then(function successCallback(data) {
					}, function errorCallback(data) {
						alertify.error('Failed to update group');
					});
				};

				$scope.deleteGroup = function(id){
					$http.delete('groups/' + id).then(function successCallback(data) {
                        alertify.success("Group was deleted");
                        getAllGroups();
                        $scope.count--;
					}, function errorCallback(data) {
						alertify.error('Failed to delete group');
					});
				};

                $scope.addUserToGroup = function(user, group){
                    $http.put('users/group/' + group.id, user).then(function successCallback(data) {
                    	alertify.success("User " + user.userName + " was added to group " + group.name);
                    }, function errorCallback(data) {
                        alertify.error('Failed to add user to group');
                    });
                };

                $scope.deleteUserFromGroup = function(user, group){
                    $http.delete('users/group/' + group.id + "/" + user.id).then(function successCallback(data) {
                        alertify.success("User was deleted from group");
                    }, function errorCallback(data) {
                        alertify.error('Failed to delete user from group');
                    });
                };

                $scope.users_all = [];

                $scope.usersSearchCriteria = {};
                $scope.asyncContacts = [];
                $scope.filterSelected = true;

                $scope.querySearch = querySearch;

                function querySearch (criteria) {
                    $scope.usersSearchCriteria.userName = criteria;
                    return $http.post('users/search', $scope.usersSearchCriteria, {params: {q: criteria}})
                        .then(function(response){
                        	return response.data.results;
                        });
                }

				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		}).result.then(function(data) {
        }, function () {
        });
    };

	
	$scope.resetSearchCriteria = function(){
		$scope.usersSearchCriteria = angular.copy(DEFAULT_SC);
	};
	
	(function init(){
		$scope.loadUsers(1);
		DashboardService.getUserPerformanceDashboardId().then(function(dashboardId) {
			$scope.pefrDashboardId = dashboardId;
		});
	})();
	
}]);

ZafiraApp.controller('UsersProfileCtrl', [ '$scope', '$rootScope', '$http' ,'$location', '$modal', '$route', 'UserService', function($scope, $rootScope, $http, $location, $modal, $route, UserService) {

	$scope.user = {};
	
	$scope.updateUser = function(user){
		$http.put('users', user).then(function successCallback(data) {
			$route.reload();
		}, function errorCallback(data) {
			alertify.error('Failed to update user');
		});
	};
	
	$scope.updatePassword = function(newPassword, confirmPassword){
		if(newPassword == confirmPassword)
		{
			$scope.user.password = newPassword;
			$scope.updateUser($scope.user);
		}
		else
		{
			alertify.error("Passwords does not match!");
		}
	};
	
	(function init(){
		UserService.getCurrentUser().then(function(user) {
			$http.get('users/' + user.id).then(function successCallback(data) {
				$scope.user = data.data;
				$scope.user.password = null;
			}, function errorCallback(data) {
				alertify.error('Failed to load user');
			});
		});
	})();
	
}]);