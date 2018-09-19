(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('GroupService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', GroupService])

    function GroupService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.groups = [];

        service.getRoles = getRoles;
        service.createGroup = createGroup;
        service.getGroup = getGroup;
        service.getAllGroups = getAllGroups;
        service.getGroupsCount = getGroupsCount;
        service.updateGroup = updateGroup;
        service.deleteGroup = deleteGroup;

        return service;

        function getRoles(){
            return $http.get(API_URL + '/api/groups/roles').then(UtilService.handleSuccess, UtilService.handleError('Failed to get roles'));
        }

        function createGroup(group){
            return $http.post(API_URL + '/api/groups', group).then(UtilService.handleSuccess, UtilService.handleError('Failed to create group'));
        }

        function getGroup(id){
            return $http.get(API_URL + '/api/groups/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to get group'));
        }

        function getAllGroups(){
            return $http.get(API_URL + '/api/groups/all').then(UtilService.handleSuccess, UtilService.handleError('Failed to get groups'));
        }

        function getGroupsCount(){
            return $http.get(API_URL + '/api/groups/count').then(UtilService.handleSuccess, UtilService.handleError('Failed to get groups count'));
        }

        function updateGroup(group){
            return $http.put(API_URL + '/api/groups', group).then(UtilService.handleSuccess, UtilService.handleError('Failed to update group'));
        }

        function deleteGroup(id){
            return $http.delete(API_URL + '/api/groups/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete group'));
        }
    }
})();
