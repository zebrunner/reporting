(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('UserService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', '$q', UserService])

    function UserService($httpMock, $cookies, $rootScope, UtilService, API_URL, $q) {
        var _currentUser = null;
        var service = {
            getUserProfile: getUserProfile,
            getExtendedUserProfile: getExtendedUserProfile,
            updateStatus: updateStatus,
            searchUsers: searchUsers,
            searchUsersWithQuery: searchUsersWithQuery,
            updateUserProfile: updateUserProfile,
            deleteUserProfilePhoto: deleteUserProfilePhoto,
            updateUserPassword: updateUserPassword,
            createOrUpdateUser: createOrUpdateUser,
            addUserToGroup: addUserToGroup,
            deleteUserFromGroup: deleteUserFromGroup,
            getDefaultPreferences: getDefaultPreferences,
            updateUserPreferences: updateUserPreferences,
            resetUserPreferencesToDefault: resetUserPreferencesToDefault,
            deleteUserPreferences: deleteUserPreferences,
            initCurrentUser: initCurrentUser,
            getCurrentUser: getCurrentUser,
            clearCurrentUser: clearCurrentUser,
            setDefaultPreferences: setDefaultPreferences
        };

        return service;

        function getUserProfile() {
        	return $httpMock.get(API_URL + '/api/users/profile').then(UtilService.handleSuccess, UtilService.handleError('Unable to get user profile'));
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

        function initCurrentUser() {
            if (_currentUser) {
                !$rootScope.currentUser && ($rootScope.currentUser = _currentUser);

                return $q.resolve(_currentUser);
            }

            return getExtendedUserProfile()
                .then(function(rs) {
                    if (rs.success) {
                        _currentUser = rs.data['user'];
                        $rootScope.currentUser = _currentUser; //TODO: get rid of $rootScope.currentUser and use service instead
                        $rootScope.currentUser.isAdmin = $rootScope.currentUser.roles.indexOf('ROLE_ADMIN') >= 0;
                        setDefaultPreferences($rootScope.currentUser.preferences);

                        $rootScope.currentUser.pefrDashboardId = rs.data['performanceDashboardId'];
                        if (!$rootScope.currentUser.pefrDashboardId) {
                            alertify.error('\'User Performance\' dashboard is unavailable!');
                        }

                        $rootScope.currentUser.personalDashboardId = rs.data['personalDashboardId'];
                        if (!$rootScope.currentUser.personalDashboardId) {
                            alertify.error('\'Personal\' dashboard is unavailable!');
                        }

                        $rootScope.currentUser.stabilityDashboardId = rs.data['stabilityDashboardId'];

                        $rootScope.currentUser.defaultDashboardId = rs.data['defaultDashboardId'];
                        if (!$rootScope.currentUser.defaultDashboardId) {
                            alertify.warning('Default Dashboard is unavailable!');
                        }

                        return _currentUser;
                    } else {
                        return $q.reject(rs);
                    }
                });
        }

        function clearCurrentUser() {
            _currentUser = null;
            $rootScope.currentUser = _currentUser;

            return _currentUser;
        }

        function getCurrentUser() {
            return _currentUser;
        }

        function setDefaultPreferences(userPreferences){
            userPreferences.forEach(function(userPreference) {
                switch(userPreference.name) {
                    case 'DEFAULT_DASHBOARD':
                        _currentUser.defaultDashboard = userPreference.value;
                        break;
                    case 'REFRESH_INTERVAL':
                        _currentUser.refreshInterval = userPreference.value;
                        break;
                    case 'THEME':
                        _currentUser.theme = userPreference.value;
                        break;
                    default:
                        break;
                }
            });
        }
    }
})();
