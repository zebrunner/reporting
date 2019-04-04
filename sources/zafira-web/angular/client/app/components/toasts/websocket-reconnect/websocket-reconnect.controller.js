(function () {
    'use strict';

    angular.module('app').controller('WebsocketReconnectController', WebsocketReconnectController);

    function WebsocketReconnectController($scope, $rootScope, $mdToast, reconnect) {
        'ngInject';

        $scope.reconnect = function() {
            angular.forEach($rootScope.disconnectedWebsockets.websockets, function (websocket, name) {
                reconnect(name);
            });
            $scope.closeToast();
        };

        $scope.closeToast = function() {
            $rootScope.disconnectedWebsockets.toastOpened = false;
            $mdToast.hide();
        };
    }

})();
