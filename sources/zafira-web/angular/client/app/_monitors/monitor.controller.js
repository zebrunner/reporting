(function () {
    'use strict';

    angular
        .module('app.monitor')
        .controller('MonitorListController', ['$scope', '$rootScope', '$location', '$state', '$mdDialog', 'ЬService', 'GroupService', 'UtilService', 'DashboardService', MonitorListController])

   function MonitorController($scope, $rootScope, $location, $state, $mdDialog, UserService, GroupService, UtilService, DashboardService) {

//    	var DEFAULT_SC = {page : 1, pageSize : 20};
//
//    	$scope.UtilService = UtilService;
//    	$scope.DashboardService = DashboardService;
//
//    	$scope.sc = angular.copy(DEFAULT_SC);
//    	$scope.users = [];
//        $scope.order = 'username';
//
//    	$scope.search = function (page) {
//            $scope.sc.date = null;
//            $scope.sc.toDate = null;
//            $scope.sc.fromDate = null;
//
//            if(page)
//            {
//                $scope.sc.page = page;
//            }
//
//            if ($scope.sc.period == ""){
//                $scope.sc.date = $scope.sc.chosenDate;
//            }
//            else if ($scope.sc.period == "before"){
//                $scope.sc.toDate =  $scope.sc.chosenDate;
//            }
//            else if ($scope.sc.period == "after") {
//                $scope.sc.fromDate = $scope.sc.chosenDate;
//            }
//            else if ($scope.sc.period == "between") {
//                $scope.sc.fromDate = $scope.sc.chosenDate;
//                $scope.sc.toDate =  $scope.sc.endDate;
//            }
//
//  		UserService.searchUsers($scope.sc).then(function(rs) {
//				if(rs.success)
//        		{
//        			$scope.sr = rs.data;
//        		}
//        		else
//        		{
//        			alertify.error(rs.message);
//        		}
//			});
//        };
//
//        $scope.reset = function () {
//        	$scope.sc = angular.copy(DEFAULT_SC);
//        	$scope.search();
//        };
//
//        $scope.showChangePasswordDialog = function($event, user) {
//            $mdDialog.show({
//                controller: function ($scope, $mdDialog) {
//                    $scope.user = user;
//                    $scope.changePassword = {'userId' : user.id};
//                    $scope.updateUserPassword = function(changePassword)
//                    {
//                        UserService.updateUserPassword(changePassword)
//                            .then(function (rs) {
//                                if(rs.success)
//                                {
//                                    $scope.changePassword = {};
//                                    $scope.hide();
//                                    alertify.success('Password changed');
//                                }
//                                else
//                                {
//                                    alertify.error(rs.message);
//                                }
//                            });
//                    };
//                    $scope.hide = function() {
//                        $mdDialog.hide(true);
//                    };
//                    $scope.cancel = function() {
//                        $mdDialog.cancel(false);
//                    };
//                },
//                templateUrl: 'app/_users/password_modal.html',
//                parent: angular.element(document.body),
//                targetEvent: event,
//                clickOutsideToClose:true,
//                fullscreen: true
//            })
//            .then(function(answer) {
//            	if(answer)
//            	{
//            		$state.reload();
//            	}
//            }, function() {
//            });
//        };
//
//        $scope.showEditProfileDialog = function(event, user) {
//            $mdDialog.show({
//                controller: function ($scope, $mdDialog) {
//                    $scope.user = angular.copy(user);
//                    $scope.updateUser = function() {
//                        UserService.createOrUpdateUser($scope.user).then(function(rs) {
//                            if(rs.success)
//                            {
//                                $scope.hide();
//                                alertify.success('Profile changed');
//                            }
//                            else
//                            {
//                                alertify.error(rs.message);
//                            }
//                        });
//                    };
//                    $scope.deleteUser = function() {
//                        UserService.deleteUser($scope.user.id).then(function(rs) {
//                            if(rs.success)
//                            {
//                                $scope.hide();
//                                alertify.success('User deleted');
//                            }
//                            else
//                            {
//                                alertify.error(rs.message);
//                            }
//                        });
//                    };
//                    $scope.hide = function() {
//                        $mdDialog.hide(true);
//                    };
//                    $scope.cancel = function() {
//                        $mdDialog.cancel(false);
//                    };
//                },
//                templateUrl: 'app/_users/edit_modal.html',
//                parent: angular.element(document.body),
//                targetEvent: event,
//                clickOutsideToClose:true,
//                fullscreen: true
//            })
//                .then(function(answer) {
//                	if(answer)
//                	{
//                		$state.reload();
//                	}
//                }, function() {
//                });
//        };
//
//        $scope.showCreateUserDialog = function(event) {
//            $mdDialog.show({
//                controller: function ($scope, $mdDialog) {
//                    $scope.createUser = function() {
//                        UserService.createOrUpdateUser($scope.user).then(function(rs) {
//                            if(rs.success)
//                            {
//                                $scope.hide();
//                                alertify.success('User created');
//                            }
//                            else
//                            {
//                                alertify.error(rs.message);
//                            }
//                        });
//                    };
//                    $scope.hide = function() {
//                        $mdDialog.hide(true);
//                    };
//                    $scope.cancel = function() {
//                        $mdDialog.cancel(false);
//                    };
//                },
//                templateUrl: 'app/_users/create_modal.html',
//                parent: angular.element(document.body),
//                targetEvent: event,
//                clickOutsideToClose:true,
//                fullscreen: true
//            })
//                .then(function(answer) {
//                	if(answer)
//                	{
//                		$state.reload();
//                	}
//                }, function() {
//                });
//        };

        $scope.showMonitorsSettingsDialog = function(event) {
            $mdDialog.show({
//                controller: GroupController,
                templateUrl: 'app/_monitors/create_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                }, function() {
                });
        };

//        $scope.isDateChosen = true;
//        $scope.isDateBetween = false;
//
//        $scope.changePeriod = function () {
//            if ($scope.sc.period == "between") {
//                $scope.isDateChosen = true;
//                $scope.isDateBetween = true;
//            }
//            else if ($scope.sc.period == "before" || $scope.sc.period == "after" || $scope.sc.period == "") {
//                $scope.isDateChosen = true;
//                $scope.isDateBetween = false;
//            }
//            else {
//                $scope.isDateChosen = false;
//                $scope.isDateBetween = false;
//            }
//        };
//		(function initController() {
//			 $scope.search(1);
////			 DashboardService.GetDashboards("USER_PERFORMANCE").then(function(rs) {
////                if(rs.success && rs.data.length > 0)
////                {
////                	$scope.pefrDashboardId = rs.data[0].id;
////                }
////            });
//		})();
	}


})();
