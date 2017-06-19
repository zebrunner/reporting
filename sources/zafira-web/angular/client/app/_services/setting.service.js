(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('SettingsService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', SettingsService])

    function SettingsService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getAllSettings = getAllSettings;
        service.getSetting = getSetting;
        service.deleteSetting = deleteSetting;
        service.createSetting = createSetting;
        service.editSetting = editSetting;

        return service;

        function getAllSettings() {
            return $http.get(API_URL + '/api/settings/list').then(UtilService.handleSuccess, UtilService.handleError('Unable to get settings list'));
        }

        function getSetting(name) {
            return $http.get(API_URL + '/api/settings/' + name).then(UtilService.handleSuccess, UtilService.handleError('Unable to get setting "' + name + '"'));
        }

        function deleteSetting(id) {
            return $http.delete(API_URL + '/api/settings/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete setting #' + id));
        }

        function createSetting(setting) {
            return $http.post(API_URL + '/api/settings', setting).then(UtilService.handleSuccess, UtilService.handleError('Unable to create setting'));
        }

        function editSetting(setting) {
            return $http.put(API_URL + '/api/settings', setting).then(UtilService.handleSuccess, UtilService.handleError('Unable to edit setting'));
        }
    }
})();
