(function () {
    'use strict';

    angular
        .module('app.settings')
        .controller('SettingsController', ['$scope', '$rootScope', '$state', '$mdConstant', '$stateParams', '$mdDialog', 'SettingsService', SettingsController])

    function SettingsController($scope, $rootScope, $state, $mdConstant, $stateParams, $mdDialog, SettingsService) {

    	$scope.settings = [];
        $scope.toolName = null;

    	$scope.showSettingsDialog = function(event, setting) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog) {

                	$scope.setting = {};

                	if(setting)
                	{
                		$scope.setting = setting;
                	}

                    $scope.create = function(setting) {
                    	SettingsService.createSetting(setting).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('Setting created');
                            }
                            else
                            {
                                alertify.error(rs.message);
                            }
                        });
                    };

                    $scope.update = function(settings) {
                    	SettingsService.editSetting(settings).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('Setting updated');
                            }
                            else
                            {
                                alertify.error(rs.message);
                            }
                        });
                    };

                    $scope.delete = function(id) {
                    	SettingsService.deleteSetting(id).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('Setting deleted');
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
                templateUrl: 'app/_settings/settings_modal.html',
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


    	(function init(){
            SettingsService.getSettingsByIntegration(false).then(function(rs) {
                if(rs.success)
                {
                    $scope.settings = rs.data;
                }
                else
                {
                    console.error('Failed to load settings');
                }
            });
        })();
    }
})();
