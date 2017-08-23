(function () {
    'use strict';

    angular
        .module('app.integrations')
        .controller('IntegrationsController', ['$scope', '$rootScope', '$state', '$mdConstant', '$stateParams', '$mdDialog', 'SettingsService', IntegrationsController])

    function IntegrationsController($scope, $rootScope, $state, $mdConstant, $stateParams, $mdDialog, SettingsService) {

        $scope.settingTools = [];

        var ENABLED_POSTFIX = '_ENABLED';

        $scope.saveTool = function (tool) {
            SettingsService.editSettings(tool.settings).then(function (rs) {
                if (rs.success) {
                    var settingTool = getSettingToolByName(tool.name);
                    settingTool.isConnected = rs.data.connected;
                    settingTool.settings = rs.data.settingList;
                    alertify.success('Tool ' + tool.name + ' was changed');
                }
            });
        };


        $scope.regenerateKey = function () {
            SettingsService.regenerateKey().then(function(rs) {
                if(rs.success)
                {
                    alertify.success('Encrypt key was regenerated');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.switchEnabled = function (tool) {
            var setting = getSettingByName(tool.name + ENABLED_POSTFIX);
            setting.value = tool.isEnabled;
            SettingsService.editSetting(setting).then(function (rs) {
                if (rs.success) {
                    if(setting.value)
                        alertify.success('Tool ' + tool.name + ' is enabled');
                    else
                        alertify.success('Tool ' + tool.name + ' is disabled');
                }
            });
        };

        var getSettingToolByName = function (name) {
            return $scope.settingTools.filter(function (tool) {
                return tool.name == name;
            })[0];
        };

        var isEnabledSetting  = function (tool, setting) {
            return setting.name == tool + ENABLED_POSTFIX;
        };

        var getEnabledSetting = function (tool, settings) {
            return settings.filter(function (setting) {
                if(isEnabledSetting(tool, setting)) {
                    return true;
                }
            })[0];
        };

        var getSettingByName = function (name) {
            return $scope.settings.filter(function (setting) {
                return setting.name == name;
            })[0];
        };

        (function init(){
            SettingsService.getSettingTools().then(function(tools) {
                if (tools.success) {
                    $scope.tools = tools.data;
                    SettingsService.getSettingsByIntegration(true).then(function (settings) {
                        if (settings.success) {
                            $scope.settings = settings.data;
                            for(var tool in $scope.tools) {
                                var currentTool = {};
                                currentTool.name = tool;
                                currentTool.isConnected = $scope.tools[tool];
                                currentTool.settings = settings.data.filter(function (setting) {
                                    if(isEnabledSetting(tool, setting)) {
                                        return false;
                                    }
                                    return setting.tool == tool;
                                });
                                currentTool.isEnabled = getEnabledSetting(tool, settings.data).value == 'true';
                                $scope.settingTools.push(currentTool);
                            }
                        }
                        else {
                            console.error('Failed to load settings');
                        }
                    });
                }
            });
        })();
    }
})();
