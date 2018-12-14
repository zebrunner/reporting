(function () {
    'use strict';

    angular
        .module('app.user')
        .controller('UserProfileController', ['$scope', '$rootScope', '$mdDialog', '$timeout', '$location', '$state', 'UserService', 'DashboardService', 'UtilService', 'AuthService', 'UploadService', UserProfileController])

    // **************************************************************************
    function UserProfileController($scope, $rootScope, $mdDialog, $timeout, $location, $state, UserService, DashboardService, UtilService, AuthService, UploadService) {

        $scope.UtilService = UtilService;

        $scope.user = {};
        $scope.changePassword = {};
        $scope.preferences = [];
        $scope.preferenceForm = {};
        $scope.dashboards = [];
        $scope.pefrDashboardId = null;
        $scope.accessToken = null;

        var FILE_USERS_TYPE = 'USERS';

        $scope.deleteUserProfilePhoto = function () {
            UserService.deleteUserProfilePhoto().then(function (rs) {
                if (rs.success) {
                    alertify.success("Photo was deleted");
                    $rootScope.currentUser.photoURL = '?' + (new Date()).getTime();
                    $state.reload();
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.hasHiddenDashboardsPermission = function () {
            return AuthService.UserHasAnyPermission(["VIEW_HIDDEN_DASHBOARDS"]);
        };

        $scope.updateUserProfile = function (profile) {
            delete profile.preferences;
            UserService.updateUserProfile(profile)
                .then(function (rs) {
                    if (rs.success) {
                        $scope.user = rs.data;
                        $rootScope.currentUser.firstName = rs.data.firstName;
                        $rootScope.currentUser.lastName = rs.data.lastName;
                        alertify.success("User profile updated");
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
        };

        $scope.generateAccessToken = function () {
            AuthService.GenerateAccessToken()
                .then(function (rs) {
                    if (rs.success) {
                        $scope.accessToken = rs.data.token;
                    }
                });
        };

        $scope.copyAccessToken = function (accessToken) {
            var node = document.createElement('pre');
            node.textContent = accessToken;
            document.body.appendChild(node);

            var selection = getSelection();
            selection.removeAllRanges();

            var range = document.createRange();
            range.selectNodeContents(node);
            selection.addRange(range);

            document.execCommand('copy');
            selection.removeAllRanges();
            document.body.removeChild(node);

            alertify.success("Access token copied to clipboard");
        };

        $scope.loadDashboards = function () {
            DashboardService.GetDashboards($scope.hasHiddenDashboardsPermission()).then(function (rs) {
                if (rs.success) {
                    $scope.dashboards = rs.data;
                }
            });
        };

        $scope.updateUserPassword = function (changePassword) {
            changePassword.userId = $rootScope.currentUser.id;
            UserService.updateUserPassword(changePassword)
                .then(function (rs) {
                    if (rs.success) {
                        $scope.changePassword = {};
                        alertify.success("Password changed");
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
        };

        $scope.getUserProfile = function () {
            UserService.getUserProfile()
                .then(function (rs) {
                    if (rs.success) {
                        $scope.user = rs.data;
                        $scope.changePassword.userId = $scope.user.id;
                        if ($scope.user.preferences.length !== 0) {
                            $scope.preferences = $scope.user.preferences;
                        }
                        else {
                            $scope.getDefaultPreferences();
                        }
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
        };

        $scope.getDefaultPreferences = function () {
            UserService.getDefaultPreferences()
                .then(function (rs) {
                    if (rs.success) {
                        $scope.preferences = rs.data;
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
        };

        $scope.updateUserPreferences = function (preferenceForm) {
            var preferences = $scope.preferences;
            for (var i = 0; i < preferences.length; i++) {
                preferences[i].userId = $scope.user.id;
                if (preferences[i].name === 'DEFAULT_DASHBOARD') {
                    preferences[i].value = preferenceForm.defaultDashboard;
                }
                else if (preferences[i].name === 'REFRESH_INTERVAL') {
                    preferences[i].value = preferenceForm.refreshInterval;
                } else if (preferences[i].name === 'THEME') {
                    preferences[i].value = $scope.main.skin;
                }
            }
            UserService.updateUserPreferences($scope.user.id, preferences).then(function (rs) {
                if (rs.success) {
                    $scope.preferences = rs.data;
                    //$rootScope.$broadcast('event:preferencesReset');
                    if (rs.data && rs.data.length) {
                        UserService.setDefaultPreferences(rs.data);
                    }
                    alertify.success('User preferences are successfully updated');
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.resetPreferences = function () {
            UserService.resetUserPreferencesToDefault().then(function (rs) {
                if (rs.success) {
                    UserService.setDefaultPreferences(rs.data);
                    alertify.success('Preferences are set to default');
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.selectDashboard = function (dashboard) {
            return $rootScope.currentUser.defaultDashboard === dashboard.title;
        };

        $scope.selectInterval = function (interval) {
            return $rootScope.currentUser.refreshInterval == interval;
        };

        $scope.convertMillis = function (millis) {
            var sec = millis / 1000;
            if (millis == 0) {
                return 'Disabled'
            }
            else if (sec < 60) {
                return sec + ' sec';
            }
            else {
                return sec / 60 + ' min';
            }
        };

        $scope.showUploadImageDialog = function ($event, user) {
            $mdDialog.show({
                controller: FileUploadController,
                templateUrl: 'app/_users/upload_image_modal.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    user : user
                }
            })
                .then(function () {
                }, function (data) {
                    if(data) {
                        user.photoURL = data.photoURL += '?' + (new Date()).getTime();
                    }
                });
        };

        function FileUploadController($scope, $mdDialog, user) {
            $scope.user = user;
            $scope.uploadImage = function (multipartFile) {
                UploadService.upload(multipartFile, FILE_USERS_TYPE).then(function (rs) {
                    if(rs.success)
                    {
                        $scope.user.photoURL = rs.data.url;
                        //$rootScope.currentUser.photoURL = rs.data.url + '?' + (new Date()).getTime();
                        delete $scope.user.preferences;
                        UserService.updateUserProfile($scope.user)
                            .then(function (prs) {
                                if(prs.success)
                                {
                                    $scope.cancel(prs.data);
                                }
                            });
                        alertify.success("Photo was uploaded");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            };
            $scope.hide = function() {
                $mdDialog.hide();
            };
            $scope.cancel = function(data) {
                $mdDialog.cancel(data);
            };
        }

        (function initController() {
            $scope.$watch('currentUser.refreshInterval', function (newVal) {
                if(newVal) {
                    $scope.widgetRefreshIntervals = [0, 30000, 60000, 120000, 300000];
                    $scope.loadDashboards();
                    $scope.getUserProfile();
                }
            });
        })();

    };
})();
