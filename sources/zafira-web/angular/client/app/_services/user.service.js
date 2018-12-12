(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('UserService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', UserService])

    function UserService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.getUserProfile = getUserProfile;
        service.getExtendedUserProfile = getExtendedUserProfile;
        service.updateStatus = updateStatus;
        service.searchUsers = searchUsers;
        service.searchUsersWithQuery = searchUsersWithQuery;
        service.updateUserProfile = updateUserProfile;
        service.deleteUserProfilePhoto = deleteUserProfilePhoto;
        service.updateUserPassword = updateUserPassword;
        service.createOrUpdateUser = createOrUpdateUser;
        service.addUserToGroup = addUserToGroup;
        service.deleteUserFromGroup = deleteUserFromGroup;
        service.getDefaultPreferences = getDefaultPreferences;
        service.updateUserPreferences = updateUserPreferences;
        service.resetUserPreferencesToDefault = resetUserPreferencesToDefault;
        service.deleteUserPreferences = deleteUserPreferences;
        service.fetchUserProfile = fetchUserProfile;

        return service;

        function getUserProfile() {
        	return $httpMock.get(API_URL + '/api/users/profile').then(UtilService.handleSuccess, UtilService.handleError('Unable to get user profile'));
        }

        function fetchUserProfile() {
            return $httpMock.get(API_URL + '/api/users/profile').then(UtilService.handleSuccess);
        }

        function getExtendedUserProfile() {
            return $httpMock.get(API_URL + '/api/users/profile/extended').then(UtilService.handleSuccess, UtilService.handleError('Unable to get extended user profile'));
        }

        function updateStatus(user) {
            return $httpMock.put(API_URL + '/api/users/status', user).then(UtilService.handleSuccess, UtilService.handleError('Unable to get extended user profile'));
        }

        function searchUsers(criteria) {
        	return $httpMock.post(API_URL + '/api/users/search', criteria).then(UtilService.handleSuccess, UtilService.handleError('Unable to search users'));
        }

        function searchUsersWithQuery(searchCriteria, criteria) {
            return $httpMock.post(API_URL + '/api/users/search', searchCriteria, {params: {q: criteria}}).then(UtilService.handleSuccess, UtilService.handleError('Unable to search users'));
        }

        function updateUserProfile(profile) {
        	return $httpMock.put(API_URL + '/api/users/profile', profile).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user profile'));
        }

        function deleteUserProfilePhoto() {
            return $httpMock.delete(API_URL + '/api/users/profile/photo').then(UtilService.handleSuccess, UtilService.handleError('Unable to delete profile photo'));
        }

        function updateUserPassword(password) {
        	return $httpMock.put(API_URL + '/api/users/password', password).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user password'));
        }

        function createOrUpdateUser(user){
            return $httpMock.put(API_URL + '/api/users', user).then(UtilService.handleSuccess, UtilService.handleError('Failed to update user'));
        }

        function addUserToGroup(user, id){
            return $httpMock.put(API_URL + '/api/users/group/' + id, user).then(UtilService.handleSuccess, UtilService.handleError('Failed to add user to group'));
        }

        function deleteUserFromGroup(idUser, idGroup){
            return $httpMock.delete(API_URL + '/api/users/' + idUser + '/group/' + idGroup).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete user from group'));
        }

        function getDefaultPreferences() {
            return $httpMock.get(API_URL + '/api/users/preferences').then(UtilService.handleSuccess, UtilService.handleError('Unable to get default preferences'));
        }

        function updateUserPreferences(userId, preferences) {
            return $httpMock.put(API_URL + '/api/users/' + userId + '/preferences', preferences).then(UtilService.handleSuccess, UtilService.handleError('Unable to update user preferences'));
        }

        function deleteUserPreferences(userId) {
            return $httpMock.delete(API_URL + '/api/users/' + userId + '/preferences').then(UtilService.handleSuccess, UtilService.handleError('Unable to delete user preferences'));
        }
        function resetUserPreferencesToDefault() {
            return $httpMock.put(API_URL + '/api/users/preferences/default').then(UtilService.handleSuccess, UtilService.handleError('Unable to reset user preferences to default'));
        }
    }
})();
