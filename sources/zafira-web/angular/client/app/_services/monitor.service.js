(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('MonitorService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', MonitorService])

    function MonitorService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getAllMonitors = getAllMonitors;
        service.createMonitor = createMonitor;
        service.updateMonitor = updateMonitor;
        service.getCountMonitors = getCountMonitors;
        service.deleteMonitor = deleteMonitor;
        service.getMonitor = getMonitor;

        return service;


        function getAllMonitors() {
        	return $http.get(API_URL + '/api/monitors').then(UtilService.handleSuccess, UtilService.handleError('Unable to get all monitors'));
        }

        function createMonitor(monitor) {
        	return $http.post(API_URL + '/api/monitors', monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to create monitor'));
        }

        function updateMonitor(monitor) {
        	return $http.put(API_URL + '/api/monitors', monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to update monitor'));
        }

        function getCountMonitors() {
        	return $http.get(API_URL + '/api/monitors/count').then(UtilService.handleSuccess, UtilService.handleError('Unable to get count monitors'));
        }

        function deleteMonitor(id){
            return $http.delete(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete monitor'));
        }

        function getMonitor(id){
            return $http.get(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitor'));
        }

    }
})();
