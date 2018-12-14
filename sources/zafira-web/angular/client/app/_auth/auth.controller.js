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

        $scope.VALIDATIONS = UtilService.validations;

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

        $scope.forgotPasswordType = {};
        $scope.forgotPasswordEmailWasSent = false;

        $scope.emailType = {};

        $scope.forgotPassword = function (forgotPassword) {
            AuthService.forgotPassword(forgotPassword).then(function (rs) {
                if(rs.success) {
                    $scope.forgotPassword = {};
                    $scope.forgotPasswordEmailWasSent = true;
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.getForgotPasswordInfo = function (token) {
            AuthService.getForgotPasswordInfo(token).then(function (rs) {
                if(rs.success) {
                    $scope.forgotPasswordType.email = rs.data.email;
                }
            });
        };

        $scope.resetPassword = function (credentials) {
            credentials.userId = 0;
            AuthService.resetPassword(credentials, token).then(function (rs) {
                if(rs.success) {
                    alertify.success('Your password was changed successfully');
                    $state.go('signin');
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.goToState = function (state) {
            $state.go(state);
        };

        (function initController() {
            switch($state.current.name) {
                case 'signup':
                    token = $location.search()['token'];
                    $scope.getInvitation(token);
                    break;
                case 'forgotPassword':
                    break;
                case 'resetPassword':
                    token = $location.search()['token'];
                    if(! token) {
                        $state.go('signin');
                        return;
                    }
                    $scope.getForgotPasswordInfo(token);
                default:
                    break;
            }
            AuthService.ClearCredentials();
        })();

        $scope.signin = function(credentials) {
            AuthService.Login(credentials.username, credentials.password)
                .then(function(rs) {
                    if (rs.success) {
                        var payload = {
                            auth: rs.data
                        };

                        $state.params.referrer && (payload.referrer = $state.params.referrer);
                        $state.params.referrerParams && (payload.referrerParams = $state.params.referrerParams);
                        $rootScope.$broadcast('event:auth-loginSuccess', payload);
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
