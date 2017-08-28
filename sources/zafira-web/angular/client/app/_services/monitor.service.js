(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('MonitorService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', MonitorService])

    function UserService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getUserProfile = getUserProfile;
        service.searchUsers = searchUsers;
        service.searchUsersWithQuery = searchUsersWithQuery;
        service.updateUserProfile = updateUserProfile;
        service.updateUserPassword = updateUserPassword;
        service.createOrUpdateUser = createOrUpdateUser;
        service.deleteUser = deleteUser;
        service.addUserToGroup = addUserToGroup;
        service.deleteUserFromGroup = deleteUserFromGroup;

        return service;

        function getUserProfile() {
        	return $http.get(API_URL + '/api/monitors/profile').then(UtilService.handleSuccess, UtilService.handleError('Unable to get user profile'));
        }

        function searchUsers(criteria) {
        	return $http.post(API_URL + '/api/monitors/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search users'));
        }

        function searchUsersWithQuery(searchCriteria, criteria) {
            return $http.post(API_URL + '/api/monitors/search', searchCriteria, {params: {q: criteria}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to search users'));
        }

        function updateUserProfile(profile) {
        	return $http.put(API_URL + '/api/monitors/profile', profile).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user profile'));
        }

        function updateUserPassword(password) {
        	return $http.put(API_URL + '/api/monitors/password', password).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user password'));
        }

        function createOrUpdateUser(user){
            return $http.put(API_URL + '/api/monitors', monitor).then(UtilService.handleSuccess, UtilService.handleError('Failed to update user'));
        }

        function deleteUser(id){
            return $http.delete(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete user'));
        }

        function addUserToGroup(user, id){
            return $http.put(API_URL + '/api/monitors/group/' + id, monitors).then(UtilService.handleSuccess, UtilService.handleError('Failed to add user to group'));
        }

        function deleteUserFromGroup(idUser, idGroup){
            return $http.delete(API_URL + '/api/monitors/' + idUser + '/group/' + idGroup).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete user from group'));
        }
    }
})();
