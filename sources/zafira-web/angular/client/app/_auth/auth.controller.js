(function() {
    'use strict';

    angular
        .module('app.auth')
        .controller('AuthController', ['$scope', '$rootScope', '$state', '$cookies', '$templateCache', 'AuthService', 'UserService', 'UtilService', AuthController])

    function AuthController($scope, $rootScope, $state, $cookies, $templateCache, AuthService, UserService, UtilService) {

        $scope.UtilService = UtilService;

        $scope.credentials = {
            valid: true
        };

        (function initController() {
            AuthService.ClearCredentials();
        })();

        $scope.signin = function(credentials) {
            AuthService.Login(credentials.username, credentials.password)
                .then(function(rs) {
                    if (rs.success) {
                        $rootScope.$broadcast('event:auth-loginSuccess', rs.data);
                            $state.go('dashboards');
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
                        //$rootScope.$broadcast('event:auth-loginSuccess', rs.data);
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
