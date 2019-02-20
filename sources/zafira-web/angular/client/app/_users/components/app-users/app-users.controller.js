(function () {
    'use strict';

    angular.module('app.appUsers')
        .controller('AppUsersController', AppUsersController);

    // **************************************************************************
    function AppUsersController($scope, $state, $mdDialog, $mdDateRangePicker, $location,
        UserService, UtilService, DashboardService) {
        'ngInject';
            
        $scope.UtilService = UtilService;
        $scope.DashboardService = DashboardService;

        $scope.users = [];
        $scope.order = 'username';

        var tmpToday = new Date();

        var DEFAULT_SC = {page : 1, pageSize : 20};
        $scope.sc = angular.copy(DEFAULT_SC);

        $scope.tabs[$scope.tabs.indexOfField('name', 'Users')].countFunc = function() {
            return $scope.source && $scope.source.totalResults ? $scope.source.totalResults : 0;
        };

        $scope.isEqualDate = function () {
            if ($scope.searchValue.selectedRange.dateStart && $scope.searchValue.selectedRange.dateEnd) {
                return $scope.searchValue.selectedRange.dateStart.getTime() === $scope.searchValue.selectedRange.dateEnd.getTime();
            }
        };

        $scope.isDateChosen = true;
        $scope.isDateBetween = false;

        $scope.changePeriod = function () {
            if ($scope.searchValue.period == "between") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = true;
            }
            else if ($scope.searchValue.period == "before" || $scope.searchValue.period == "after" || $scope.searchValue.period == "") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = false;
            }
            else {
                $scope.isDateChosen = false;
                $scope.isDateBetween = false;
            }
        };

        $scope.pick = function ($event, showTemplate) {
            $scope.searchValue.selectedRange.showTemplate = showTemplate;
            $mdDateRangePicker.show({
                targetEvent: $event,
                model: $scope.searchValue.selectedRange
            }).then(function (result) {
                if (result) {$scope.searchValue.selectedRange = result;}

            })
        };

        $scope.onSelect = function () {
            console.log($scope.selectedRange.selectedTemplateName);
            return $scope.selectedRange.selectedTemplateName;
        };

        $scope.isFuture = function ($date) {
            return $date.getTime() < new Date().getTime();
        };

        $scope.showChangePasswordDialog = function ($event, user) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog, UtilService) {
                    'ngInject';

                    $scope.UtilService = UtilService;
                    $scope.user = user;
                    $scope.changePassword = { 'userId': user.id };
                    $scope.updateUserPassword = function (changePassword) {
                        UserService.updateUserPassword(changePassword)
                            .then(function (rs) {
                                if (rs.success) {
                                    $scope.changePassword = {};
                                    $scope.hide();
                                    alertify.success('Password changed');
                                }
                                else {
                                    alertify.error(rs.message);
                                }
                            });
                    };
                    $scope.hide = function () {
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function () {
                        $mdDialog.cancel(false);
                    };
                },
                template: require('./modals/password_modal.html'),
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: true
            })
                .then(function (answer) {
                    if (answer) {
                        $state.reload();
                    }
                }, function () {
                });
        };

        

        $scope.showEditProfileDialog = function (event, user, index) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog, UtilService) {
                    'ngInject';

                    $scope.UtilService = UtilService;
                    $scope.user = angular.copy(user);
                    $scope.updateStatus = function (user, status) {
                        user.status = status;
                        UserService.updateStatus(user).then(function (rs) {
                            if (rs.success) {
                                $scope.cancel(rs.data.status);
                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    };
                    $scope.updateUser = function () {
                        UserService.createOrUpdateUser($scope.user).then(function (rs) {
                            if (rs.success) {
                                $scope.hide();
                                alertify.success('Profile changed');
                            }
                            else {
                                alertify.error(rs.message);
                            }
                        });
                    };
                    $scope.hide = function () {
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function (status) {
                        $mdDialog.cancel(status);
                    };
                },
                template: require('./modals/edit_modal.html'),
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true
            })
                .then(function (answer) {
                    if (answer) {
                        $state.reload();
                    }
                }, function (status) {
                    if (status) {
                        $scope.sr.results[index].status = status;
                    }
                });
        };

        (function initController() {
        })();
    };
})();
