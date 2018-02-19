(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('MonitorsService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', MonitorsService])

    function MonitorsService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.createMonitor = createMonitor;
        service.checkMonitor = checkMonitor;
        service.getMonitorById = getMonitorById;
        service.getAllMonitors = getAllMonitors;
        service.searchMonitors = searchMonitors;
        service.updateMonitor = updateMonitor;
        service.deleteMonitor = deleteMonitor;
        service.getMonitorsCount = getMonitorsCount;


        return service;

        function createMonitor(monitor) {
            return $http.post(API_URL + '/api/monitors', monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to create monitor'));
        }

        function checkMonitor(monitor, check) {
            return $http.post(API_URL + '/api/monitors/check?check=' + check, monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to check monitor'));
        }

        function getMonitorById(id) {
            return $http.get(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitor by id'));
        }

        function searchMonitors(sc) {
            return $http.post(API_URL + '/api/monitors/search', sc).then(UtilService.handleSuccess, UtilService.handleError('Unable to search monitors'));
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
