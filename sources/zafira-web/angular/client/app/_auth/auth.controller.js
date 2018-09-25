(function() {
    'use strict';

    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$rootScope', '$location', '$state', '$cookies', '$templateCache', 'AuthService', 'UserService', 'UtilService', 'InvitationService', AuthController])

    function AuthController($scope, $rootScope, $location, $state, $cookies, $templateCache, AuthService, UserService, UtilService, InvitationService) {

        $scope.UtilService = UtilService;

        $scope.credentials = {
            valid: true
        };

        $scope.invitation = {};

        $scope.getInvitation = function (token) {
            InvitationService.getInvitation(token).then(function (rs) {
                if(rs.success) {
                    $scope.invitation = rs.data;
                    $scope.user = {};
                    $scope.user.email = $scope.invitation.email;
                    $scope.user.source = $scope.invitation.source;
                } else {
                    $state.go('signin');
                }
            });
        };

        var token;

        (function initController() {
            if($state.current.name == 'signup') {
                token = $location.search()['token'];
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

        $scope.signup = function(user, form) {
            AuthService.signup(user, token).then(function(rs) {
                    if (rs.success) {
                        $state.go('signin');
                    } else {
                        UtilService.resolveError(rs, form, 'validationError', 'username').then(function (rs) {
                        }, function (rs) {
                            alertify.error(rs.message);
                        });
                    }
                });
        };

        $scope.onChange = function(input) {
            input.$setValidity('validationError', true);
        };
    }
})();
