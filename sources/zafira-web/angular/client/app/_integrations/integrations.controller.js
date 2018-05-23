(function () {
    'use strict';

    angular
        .module('app.integrations')
        .controller('IntegrationsController', ['$scope', '$rootScope', '$state', '$mdConstant', '$stateParams', '$mdDialog', 'UploadService', 'SettingsService', IntegrationsController])

    function IntegrationsController($scope, $rootScope, $state, $mdConstant, $stateParams, $mdDialog, UploadService, SettingsService) {

        $scope.settingTools = [];
        $scope.enabledSettings = {};

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

        var NOT_EDITABLE_SETTINGS = ['GOOGLE_CLIENT_SECRET_ORIGIN'];

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
            var setting = {};
            setting.name = tool.name + ENABLED_POSTFIX;
            setting.tool = tool.name;
            setting.id = $scope.enabledSettings[tool.name];
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

        var initTool = function (tool) {
            SettingsService.getSettingByTool(tool).then(function (settings) {
                if (settings.success) {
                    var rsTool = settings.data[0].tool;
                    var enabledSetting = getEnabledSetting(rsTool, settings.data);
                    var currentTool = {
                        name: rsTool,
                        isConnected: $rootScope.tools[rsTool],
                        settings: settings.data.filter(function (setting) {
                            if(NOT_EDITABLE_SETTINGS.indexOf(setting.name) >= 0) {
                                setting.notEditable = true;
                            }
                            return isEnabledSetting(rsTool, setting) ? false : setting.tool === rsTool;
                        }),
                        isEnabled: enabledSetting.value === 'true',
                        newSetting: {}
                    };
                    $scope.enabledSettings[enabledSetting.tool] = enabledSetting.id;
                    if($scope.settingTools.indexOfName(tool) == -1) {
                        currentTool.settings.sort(compareBySettingSortOrder);
                        $scope.settingTools.push(currentTool);
                        $scope.settingTools.sort(compareByName);
                        $scope.settingTools.sort(compareByIsEnabled);
                    } else {
                        var index = $scope.settingTools.indexOfName(tool);
                        SettingsService.isToolConnected(tool).then(function (rs) {
                            if(rs.success) {
                                currentTool.isConnected = rs.data;
                                $rootScope.tools[tool] = rs.data;
                            }
                        });
                        $scope.settingTools.splice(index, 1, currentTool);
                    }
                } else {
                    console.error('Failed to load settings');
                }
            });
        };

        $scope.showUploadFileDialog = function ($event) {
            $mdDialog.show({
                controller: FileUploadController,
                templateUrl: 'app/_integrations/file_modal.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: true,
                scope: $scope,
                preserveScope: true
            })
                .then(function (tool) {
                }, function (tool) {
                    if (tool) {
                        initTool(tool);
                    }
                });
        };

        function FileUploadController($scope, $mdDialog) {
            $scope.uploadFile = function (multipartFile) {
                UploadService.uploadGoogleJson(multipartFile).then(function (rs) {
                    if(rs.success)
                    {
                        alertify.success("File was uploaded");
                        $scope.cancel();
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
                $mdDialog.cancel('GOOGLE');
            };
        }

        (function init(){
            if($rootScope.tools) {
                for(var key in $scope.tools) {
                    initTool(key);
                }
            }
            else
            {
                $rootScope.$on("event:settings-toolsInitialized", function(ev, tool){
                    initTool(tool);
                });
            }
        })();
    }
})();
