(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('SettingsService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', SettingsService])

    function SettingsService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getSetting = getSetting;
        service.getCompanyLogo = getCompanyLogo;
        service.getSettingByTool = getSettingByTool;
        service.deleteSetting = deleteSetting;
        service.createSetting = createSetting;
        service.editSetting = editSetting;
        service.editSettings = editSettings;
        service.getSettingTools = getSettingTools;
        service.isToolConnected = isToolConnected;
        service.regenerateKey = regenerateKey;

        return service;

        function getSettingByTool(tool) {
            return $httpMock.get(API_URL + '/api/settings/tool/' + tool).then(UtilService.handleSuccess, UtilService.handleError('Unable to load' + tool + 'settings'));
        }

        function getSetting(tool, name) {
            return getSettingByTool(tool).then(function (rs) {
                if(rs.success) {
                    rs.data = rs.data.find(function (setting) {
                        return setting.name === name;
                    });
                }
                return rs;
            })
        }

        function getCompanyLogo() {
            return $httpMock.get(API_URL + '/api/settings/companyLogo').then(UtilService.handleSuccess, UtilService.handleError('Unable to get company logo URL'));
        }

        function deleteSetting(id) {
            return $httpMock.delete(API_URL + '/api/settings/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete setting #' + id));
        }

        function createSetting(setting) {
            return $httpMock.post(API_URL + '/api/settings', setting).then(UtilService.handleSuccess, UtilService.handleError('Unable to create setting'));
        }

        function editSetting(setting) {
            return $httpMock.put(API_URL + '/api/settings', setting).then(UtilService.handleSuccess, UtilService.handleError('Unable to edit setting'));
        }

        function editSettings(settings) {
            return $httpMock.put(API_URL + '/api/settings/tool', settings).then(UtilService.handleSuccess, UtilService.handleError('Unable to edit settings'));
        }

        function getSettingTools() {
            return $httpMock.get(API_URL + '/api/settings/tools').then(UtilService.handleSuccess, UtilService.handleError('Unable to get tools'));
        }

        function isToolConnected(name) {
            return $httpMock.get(API_URL + '/api/settings/tools/' + name).then(UtilService.handleSuccess, UtilService.handleError('Unable to get tool connection'));
        }

        function regenerateKey() {
            return $httpMock.post(API_URL + '/api/settings/key/regenerate').then(UtilService.handleSuccess, UtilService.handleError('Unable to get tools'));
        }
    }
})();
