(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('MonitorsService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', MonitorsService])

    function MonitorsService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.createMonitor = createMonitor;
        service.getMonitorById = getMonitorById;
        service.getAllMonitors = getAllMonitors;
        service.updateMonitor = updateMonitor;
        service.deleteMonitor = deleteMonitor;
        service.getMonitorsCount = getMonitorsCount;


        return service;

        function createMonitor(monitor) {
            return $http.post(API_URL + '/api/monitors', monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to create monitor'));
        }

        function getMonitorById(id) {
            return $http.get(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitor by id'));
        }

        function getAllMonitors() {
            return $http.get(API_URL + '/api/monitors').then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitors list'));
        }

        function updateMonitor(monitor, switchJob) {
            return $http.put(API_URL + '/api/monitors?switchJob=' + switchJob, monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to update monitor'));
        }

        function deleteMonitor(id) {
            return $http.delete(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete monitor'));
        }

        function getMonitorsCount() {
            return $http.get(API_URL + '/api/monitors/count').then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitors count'));
        }
    }
})();
