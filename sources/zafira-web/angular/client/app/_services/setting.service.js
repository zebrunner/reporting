(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('SettingsService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', SettingsService])

    function SettingsService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getAllSettings = getAllSettings;
        service.getSettingsByIntegration = getSettingsByIntegration;
        service.getAmazonPolicyCookies = getAmazonPolicyCookies;
        service.getSetting = getSetting;
        service.getCompanyLogo = getCompanyLogo;
        service.getSettingByName = getSettingByName;
        service.getSettingValueByName = getSettingValueByName;
        service.getSettingByTool = getSettingByTool;
        service.deleteSetting = deleteSetting;
        service.createSetting = createSetting;
        service.editSetting = editSetting;
        service.editSettings = editSettings;
        service.getSettingTools = getSettingTools;
        service.isToolConnected = isToolConnected;
        service.regenerateKey = regenerateKey;

        return service;

        function getAllSettings() {
            return $httpMock.get(API_URL + '/api/settings/list').then(UtilService.handleSuccess, UtilService.handleError('Unable to get settings list'));
        }

        function getSettingsByIntegration(isIntegrationTool) {
            var config = { params : {} };
            if(isIntegrationTool)
                config.params.isIntegrationTool = isIntegrationTool;
            return $httpMock.get(API_URL + '/api/settings/integration', config).then(UtilService.handleSuccess, UtilService.handleError('Unable to get settings by integration list'));
        }

        function getAmazonPolicyCookies() {
            return $httpMock.get(API_URL + '/api/settings/creds/amazon/cookies').then(UtilService.handleSuccess, UtilService.handleError('Unable to get Amazon s3 cookies'));
        }

        function getSettingByTool(tool) {
            return $httpMock.get(API_URL + '/api/settings/tool/' + tool).then(UtilService.handleSuccess, UtilService.handleError('Unable to load' + tool + 'settings'));
        }

        function getSettingByName(name) {
            return $httpMock.get(API_URL + '/api/settings/' + name).then(UtilService.handleSuccess, UtilService.handleError('Unable to get setting "' + name + '"'));
        }

        function getSettingValueByName(name) {
            return $httpMock.get(API_URL + '/api/settings/' + name + '/value').then(UtilService.handleSuccess, UtilService.handleError('Unable to get setting "' + name + '"'));
        }

        function getSetting(name) {
            return $httpMock.get(API_URL + '/api/settings/' + name).then(UtilService.handleSuccess, UtilService.handleError('Unable to get setting "' + name + '"'));
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
