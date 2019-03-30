(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('GroupService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', GroupService])

    function GroupService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        let groups = [];

        var service = {
            getRoles,
            createGroup,
            getGroup,
            getAllGroups,
            getGroupsCount,
            updateGroup,
            deleteGroup,
            get groups() {
                return groups;
            },
            set groups(data) {
                groups = data;
            }
        };

        return service;

        function getRoles(){
            return $httpMock.get(API_URL + '/api/groups/roles').then(UtilService.handleSuccess, UtilService.handleError('Failed to get roles'));
        }

        function createGroup(group){
            return $httpMock.post(API_URL + '/api/groups', group).then(UtilService.handleSuccess, UtilService.handleError('Failed to create group'));
        }

        function getGroup(id){
            return $httpMock.get(API_URL + '/api/groups/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to get group'));
        }

        function getAllGroups(isPublic){
            var postfix = isPublic ? '?public=true' : '';
            return $httpMock.get(API_URL + '/api/groups/all' + postfix).then(UtilService.handleSuccess, UtilService.handleError('Failed to get groups'));
        }

        function getGroupsCount(){
            return $httpMock.get(API_URL + '/api/groups/count').then(UtilService.handleSuccess, UtilService.handleError('Failed to get groups count'));
        }

        function updateGroup(group){
            return $httpMock.put(API_URL + '/api/groups', group).then(UtilService.handleSuccess, UtilService.handleError('Failed to update group'));
        }

        function deleteGroup(id){
            return $httpMock.delete(API_URL + '/api/groups/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete group'));
        }
    }
})();
