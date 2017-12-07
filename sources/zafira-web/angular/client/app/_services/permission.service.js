(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('PermissionService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', PermissionService])

    function PermissionService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.createPermission = createPermission;
        service.getAllPermissions = getAllPermissions;
        service.updatePermission = updatePermission;
        service.deletePermission = deletePermission;

        return service;

        function createPermission(monitor) {
            return $http.post(API_URL + '/api/permissions', monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to create permission'));
        }

        function updatePermission(permission) {
            return $http.put(API_URL + '/api/permissions/', permission).then(UtilService.handleSuccess, UtilService.handleError('Unable to update permission'));
        }

        function getAllPermissions() {
            return $http.get(API_URL + '/api/permissions').then(UtilService.handleSuccess, UtilService.handleError('Unable to get permissions list'));
        }

        function deletePermission(id) {
            return $http.delete(API_URL + '/api/permissions/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete permission'));
        }
    }
})();
