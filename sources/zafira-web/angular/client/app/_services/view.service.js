(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ViewService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', ViewService])

    function ViewService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getViewById = getViewById;
        service.getAllViews = getAllViews;
        service.createView = createView;
        service.updateView = updateView;
        service.deleteView = deleteView;

        return service;

        function getViewById(id) {
            return $http.get(API_URL + '/api/views/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get view by id'));
        }

        function getAllViews() {
            return $http.get(API_URL + '/api/views').then(UtilService.handleSuccess, UtilService.handleError('Unable to get all views'));
        }

        function createView(view) {
            return $http.post(API_URL + '/api/views', view).then(UtilService.handleSuccess, UtilService.handleError('Unable to create view'));
        }

        function updateView(view) {
            return $http.put(API_URL + '/api/views', view).then(UtilService.handleSuccess, UtilService.handleError('Unable to update view'));
        }

        function deleteView(id) {
            return $http.delete(API_URL + '/api/views/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete view'));
        }
    }
})();
