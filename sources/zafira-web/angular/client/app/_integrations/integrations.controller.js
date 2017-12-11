(function () {
    'use strict';

    angular
        .module('app.integrations')
        .controller('IntegrationsController', ['$scope', '$rootScope', '$state', '$mdConstant', '$stateParams', '$mdDialog', 'SettingsService', IntegrationsController])

    function IntegrationsController($scope, $rootScope, $state, $mdConstant, $stateParams, $mdDialog, SettingsService) {

        $scope.settingTools = [];

        var ENABLED_POSTFIX = '_ENABLED';
        var PASSWORD_POSTFIX = '_PASSWORD';
        var ALTERNATIVE_PASSWORD_POSTFIX = '_API_TOKEN_OR_PASSWORD';

        var SORT_POSTFIXES = {
            '_URL': 1,
            '_USER': 2,
            '_PASSWORD': 3,
            '_ACCESS_KEY': 4,
            '_SECRET_KEY': 5
        };

        $scope.saveTool = function (tool) {
           SettingsService.editSettings(tool.settings).then(function (rs) {
                if (rs.success) {
                    var settingTool = getSettingToolByName(tool.name);
                    settingTool.isConnected = rs.data.connected;
                    settingTool.settings = rs.data.settingList;
                    settingTool.settings.sort(compareBySettingSortOrder);
                    alertify.success('Tool ' + tool.name + ' was changed');
                }
            });
        };

        $scope.createSetting = function (tool) {
            var addedSetting = tool.newSetting;
            addedSetting.tool = tool.name;
            tool.settings.push(addedSetting);
            tool.newSetting = {};
            SettingsService.createSetting(addedSetting).then(function (rs) {
                if (rs.success) {
                      alertify.success('New setting for ' + tool.name + ' was added');
                }
            });
        };

        $scope.deleteSetting = function (setting, tool) {
            var array = tool.settings;
            var index = array.indexOf(setting);
            if (index > -1) {
                array.splice(index, 1);
            }
            SettingsService.deleteSetting(setting.id).then(function (rs) {
                if (rs.success) {

                    alertify.success('Setting ' + setting.name + ' was deleted');
                }
            });
        };

        $scope.regenerateKey = function () {
            SettingsService.regenerateKey().then(function(rs) {
                if(rs.success)
                {
                    $state.reload();
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
            var settings = [];
            settings.push(setting);
            SettingsService.editSettings(settings).then(function (rs) {
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
                return tool.name === name;
            })[0];
        };

        var isEnabledSetting  = function (tool, setting) {
            return setting.name === tool + ENABLED_POSTFIX;
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
                return setting.name === name;
            })[0];
        };

        function compareBySettingSortOrder(a, b) {
            var aSortOrder = getSortOrderByPostfix(a.name);
            var bSortOrder = getSortOrderByPostfix(b.name);
            return compareTo(aSortOrder, bSortOrder)
        }

        function compareByName(a, b) {
            var aSortOrder = a.name;
            var bSortOrder = b.name;
            return compareTo(aSortOrder, bSortOrder)
        }

        function compareByIsEnabled(a, b) {
            var aSortOrder = a.isEnabled;
            var bSortOrder = b.isEnabled;
            return compareTo(aSortOrder, bSortOrder)
        }

        function compareTo(aSortOrder, bSortOrder) {
            if(typeof(aSortOrder) === 'boolean' && typeof(bSortOrder) === 'boolean') {
                if(aSortOrder === true && bSortOrder === false) {
                    return -1;
                } else if(aSortOrder === false && bSortOrder === true) {
                    return 1;
                } else
                    return 0;
            } else {
                if (aSortOrder < bSortOrder) {
                    return -1;
                } else if (aSortOrder > bSortOrder) {
                    return 1;
                } else
                    return 0;
            }
        }

        var getSortOrderByPostfix = function (settingName) {
            for(var postfix in SORT_POSTFIXES) {
                try {
                    if (settingName.includes(postfix)) {
                        return SORT_POSTFIXES[postfix];
                    }
                } catch(e) {
                    console.log('setting name ' + settingName);
                }
            }
            return getMaxSortOrder() + 1;
        };

        var getMaxSortOrder = function () {
            var max = 0;
            for(var postfix in SORT_POSTFIXES) {
                if(SORT_POSTFIXES[postfix] > max) {
                    max = SORT_POSTFIXES[postfix];
                }
            }
            return max;
        };

        var initTools = function(tools) {
            SettingsService.getSettingsByIntegration(true).then(function (settings) {
                if (settings.success) {
                    $scope.settings = settings.data;
                    for(var tool in tools) {
                        var currentTool = {};
                        currentTool.name = tool;
                        currentTool.isConnected = tools[tool];
                        currentTool.settings = settings.data.filter(function (setting) {
                            if(isEnabledSetting(tool, setting)) {
                                return false;
                            }
                            return setting.tool === tool;
                        });
                        currentTool.isEnabled = getEnabledSetting(tool, settings.data).value === 'true';
                        currentTool.settings.sort(compareBySettingSortOrder);
                        currentTool.newSetting = {};
                        $scope.settingTools.push(currentTool);
                    }
                    $scope.settingTools.sort(compareByName);
                    $scope.settingTools.sort(compareByIsEnabled)
                }
                else {
                    console.error('Failed to load settings');
                }
            });
        };

        (function init(){
            if($rootScope.tools)
            {
                initTools($rootScope.tools);
            }
            else
            {
                $rootScope.$on("event:settings-toolsInitialized", function(ev, tools){
                    initTools(tools);
                });
            }
        })();
    }
})();
