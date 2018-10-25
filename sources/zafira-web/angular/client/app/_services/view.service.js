(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ViewService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', ViewService])

    function ViewService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getViewById = getViewById;
        service.getAllViews = getAllViews;
        service.createView = createView;
        service.updateView = updateView;
        service.deleteView = deleteView;

        return service;

        function getViewById(id) {
            return $httpMock.get(API_URL + '/api/views/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get view by id'));
        }

        function getAllViews() {
            return $httpMock.get(API_URL + '/api/views').then(UtilService.handleSuccess, UtilService.handleError('Unable to get all views'));
        }

        function createView(view) {
            return $httpMock.post(API_URL + '/api/views', view).then(UtilService.handleSuccess, UtilService.handleError('Unable to create view'));
        }

        function updateView(view) {
            return $httpMock.put(API_URL + '/api/views', view).then(UtilService.handleSuccess, UtilService.handleError('Unable to update view'));
        }

        function deleteView(id) {
            return $httpMock.delete(API_URL + '/api/views/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete view'));
        }
    }
})();
