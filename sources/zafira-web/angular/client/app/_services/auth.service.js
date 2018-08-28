(function() {
    'use strict';

    angular
        .module('app.services')
        .factory('AuthService', ['$http', '$cookies', '$rootScope', '$state', 'UtilService', 'UserService', 'API_URL', AuthService])

    function AuthService($http, $cookies, $rootScope, $state, UtilService, UserService, API_URL) {

        var service = {};

        service.Login = Login;
        service.Register = Register;
        service.SetCredentials = SetCredentials;
        service.ClearCredentials = ClearCredentials;
        service.RefreshToken = RefreshToken;
        service.GenerateAccessToken = GenerateAccessToken;
        service.IsLoggedIn = IsLoggedIn;
        service.UserHasAnyRole = UserHasAnyRole;
        service.UserHasAnyPermission = UserHasAnyPermission;

        function Login(username, password) {
            return $http.post(API_URL + '/api/auth/login', {
                'username': username,
                'password': password
            }).then(UtilService.handleSuccess, UtilService.handleError('Invalid credentials'));
        }

        function Register(user) {
            return $http.post(API_URL + '/api/auth/register', user).then(UtilService.handleSuccess, UtilService.handleError('Failed to register user'));
        }

        function RefreshToken(token) {
            return $http.post(API_URL + '/api/auth/refresh', {
                'refreshToken': token
            }).then(UtilService.handleSuccess, UtilService.handleError('Invalid refresh token'));
        }

        function GenerateAccessToken(token) {
            return $http.get(API_URL + '/api/auth/access').then(UtilService.handleSuccess, UtilService.handleError('Unable to generate token'));
        }

        function SetCredentials(auth) {
            $rootScope.globals = {
                "auth": auth
            };
            //$http.defaults.headers.common['Authorization'] = auth.type + " " + auth.accessToken;
            $cookies.putObject('globals', $rootScope.globals);
        }

        function ClearCredentials() {
            $rootScope.currentUser = null;
            $rootScope.globals = {};
            $cookies.remove('globals');
//            $cookies.remove('Access-Token');
            //$http.defaults.headers.common.Authorization = null;
        }

        function IsLoggedIn() {
            return $rootScope.currentUser != null && $rootScope.globals.auth != null;
        }

        function UserHasAnyRole(roles) {
            if (!IsLoggedIn()) {
                return false;
            }
            var found = false;
            angular.forEach(roles, function(role, index) {
                if ($rootScope.currentUser.roles.indexOf(role) >= 0) {
                    found = true;
                    return;
                }
            });
            return found;
        }

        function UserHasAnyPermission(permissions) {
            if (!IsLoggedIn()) {
                return false;
            }
            var found = false;
            angular.forEach(permissions, function(permission, index) {
                angular.forEach($rootScope.currentUser.permissions, function(userPermission, index) {
                    if (userPermission.name === permission) {
                        found = true;
                        return;
                    }
                });
            });
            return found;
        }

        return service;
    }

    angular.module('app')
        .directive('hasAnyRole', ['AuthService', function(AuthService) {
            return {
                restrict: 'A',
                scope: {
                    exceptCondition: '@'
                },
                link: function(scope, elem, attrs) {
                    scope.$watch(AuthService.IsLoggedIn, function(newVal) {
                        if(newVal) {
                            var exceptValue = !!(attrs.exceptCondition && attrs.exceptCondition == 'true');
                            if(! exceptValue) {
                                if (AuthService.UserHasAnyRole(eval(attrs.hasAnyRole))) {
                                    elem.show();
                                } else {
                                    elem.hide();
                                }
                            } else {
                                if (AuthService.UserHasAnyRole(eval(attrs.hasAnyRole))) {
                                    elem.hide()
                                } else {
                                    elem.show();
                                }
                            }
                        }
                    });
                }
            }
        }]).directive('hasAnyPermission', ['AuthService', function(AuthService) {
            return {
                restrict: 'A',
                link: function(scope, elem, attrs) {
                    scope.$watch(AuthService.IsLoggedIn, function() {
                        if (AuthService.UserHasAnyPermission(eval(attrs.hasAnyPermission))) {
                            elem.show();
                        } else {
                            elem.hide();
                        }
                    });
                }
            }
        }]).directive('isOwner', ['AuthService', function(AuthService) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {
                scope.$watch(AuthService.IsLoggedIn, function() {
                    var currentUser = attrs.user && attrs.user.length ? JSON.parse(attrs.user) : {};
                    if (currentUser && currentUser.id == attrs.isOwner) {
                        elem.show();
                    } else {
                        elem.hide();
                    }
                });
            }
        }
    }]);
})();
