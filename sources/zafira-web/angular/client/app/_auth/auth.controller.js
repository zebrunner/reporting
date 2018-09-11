(function() {
    'use strict';

    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$rootScope', '$location', '$state', '$cookies', '$templateCache', 'AuthService', 'UserService', 'UtilService', AuthController])

    function AuthController($scope, $rootScope, $location, $state, $cookies, $templateCache, AuthService, UserService, UtilService) {

        $scope.UtilService = UtilService;

        $scope.credentials = {
            valid: true
        };

        $scope.invitation = {};

        $scope.getInvitation = function (token) {
            AuthService.getInvitation(token).then(function (rs) {
                if(rs.success) {
                    $scope.invitation = rs.data;
                    $scope.user = {};
                    $scope.user.email = $scope.invitation.email;
                } else {
                    $state.go('signin');
                }
            });
        };

        (function initController() {
            if($state.current.name == 'signup') {
                var token = $location.search()['token'];
                $scope.getInvitation(token);
            }
            AuthService.ClearCredentials();
        })();

        $scope.signin = function(credentials) {
            AuthService.Login(credentials.username, credentials.password)
                .then(function(rs) {
                    if (rs.success) {
                        $rootScope.$broadcast('event:auth-loginSuccess', rs.data);
                    } else {
                        $scope.credentials = {
                            valid: false
                        };
                    }
                });
        };

        $scope.signup = function(user) {
            AuthService.signup(user).then(function(rs) {
                    if (rs.success) {
                        $state.go('signin');
                    } else {
                        $scope.credentials = {
                            valid: false
                        };
                    }
                });
        };

        $scope.register = function() {
            AuthService.Register($scope.user)
                .then(function(rs) {
                    if (rs.success) {
                        $state.go('signin');
                        alertify.success('Success! Sign in now.');
                    } else {
                        $scope.credentials = {
                            valid: false
                        };
                    }
                });
        };
    }
})();
