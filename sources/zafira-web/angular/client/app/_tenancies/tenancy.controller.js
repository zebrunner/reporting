(function () {
    'use strict';

    angular
        .module('app.tenancy')
        .controller('TenancyListController', ['$scope', '$mdDialog', 'TenancyService', TenancyListController]);

    // **************************************************************************
    function TenancyListController($scope, $mdDialog, TenancyService) {

        $scope.tenancies = [];

        var SORT_BY = 'id';

        function compare(a, b) {
            var nameA = a[SORT_BY];
            var nameB = b[SORT_BY];
            if (nameA < nameB)
                return -1;
            if (nameA > nameB)
                return 1;
            return 0;
        };

        $scope.getAllTenancies = function () {
            TenancyService.getAllTenancies().then(function (rs) {
                if(rs.success) {
                    $scope.tenancies = rs.data;
                    $scope.tenancies.sort(compare);
                } else {
                    alertify.error(rs.message);
                }
            })
        };

        $scope.showTenancySettingDialog = function ($event, tenancy, index) {
            $mdDialog.show({
                controller: TenancySettingController,
                templateUrl: 'app/_tenancies/setting_modal.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    tenancy: tenancy,
                    index: index
                }
            })
                .then(function (rs) {
                    if(rs.status == 'CREATE') {
                        $scope.tenancies.push(rs.tenancy);
                        $scope.tenancies.sort(compare);
                    } else if(rs.status == 'UPDATE') {
                        $scope.tenancies.splice(index, 1, rs.tenancy);
                        $scope.tenancies.sort(compare);
                    }
                }, function () {
                });
        };

        (function init() {
            $scope.getAllTenancies();
        })();

        function TenancySettingController($scope, $mdDialog, tenancy, index) {

            var FORM_NAME = 'tenancy_form';

            $scope.tenancy = angular.copy(tenancy);

            $scope.createTenancy = function (tenancy, form) {
                TenancyService.createTenancy(tenancy).then(function (rs) {
                    if(rs.success) {
                        $scope.hide(rs.data, 'CREATE');
                        alertify.success('Tenancy is created.');
                    } else {
                        var errorField = getErrorField(rs);
                        if(errorField) {
                            callError(function () {
                                return errorField;
                            }, form, errorField, getErrorMessage(rs))
                        } else {
                            alertify.error('Tenancy is not created.');
                        }
                    }
                })
            };

            $scope.updateTenancy = function (tenancy, form) {
                TenancyService.updateTenancy(tenancy).then(function (rs) {
                    if(rs.success) {
                        $scope.hide(rs.data, 'UPDATE');
                        alertify.success('Tenancy is updated.');
                    } else {
                        var errorField = getErrorField(rs);
                        if(errorField) {
                            callError(function () {
                                return errorField;
                            }, form, errorField, getErrorMessage(rs))
                        } else {
                            alertify.error('Tenancy is not updated.');
                        }
                    }
                })
            };

            function getErrorMessage(rs) {
                var result;
                if(rs.error && rs.error.status == 400 && rs.error.data.error) {
                    result = rs.error.data.error.message;
                }
                return result;
            };

            function getErrorField(rs) {
                var result;
                if(rs.error && rs.error.status == 400 && rs.error.data.error) {
                    result = rs.error.data.error.field;
                }
                return result;
            };

            function callError(func, form, inputName, errorMessage) {
                var condition = func.call();
                if (condition) {
                    form[inputName].errorMessage = errorMessage;
                    form[inputName].$setValidity('validationError', false);
                }
                else {
                    form[inputName].$setValidity('validationError', true);
                }
            };

            $scope.hide = function(tenancy, status) {
                $mdDialog.hide({tenancy: tenancy, status: status});
            };
            $scope.cancel = function() {
                $mdDialog.cancel();
            };

            (function initController() {

            })();
        }
    }

})();
