(function () {
    'use strict';

    angular.module('app').controller('WebsocketReconnectController', [
        '$scope',
        '$mdToast',
        'reconnect',
        WebsocketReconnectController]);

    function WebsocketReconnectController($rootScope, $mdToast, reconnect) {
        $rootScope.reconnect = function() {
            angular.forEach($rootScope.disconnectedWebsockets.websockets, function (websocket, name) {
                reconnect(name);
            });
            $rootScope.closeToast();
        };

        $rootScope.closeToast = function() {
            $rootScope.disconnectedWebsockets.toastOpened = false;
            $mdToast
            .hide()
            .then(function() {
            });
        };
    }

})();
