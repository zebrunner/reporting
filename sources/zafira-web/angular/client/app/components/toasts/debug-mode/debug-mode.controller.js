(function () {
    'use strict';

    angular.module('app').controller('DebugModeController', [
        '$scope',
        '$mdToast',
        DebugModeController]);

    function DebugModeController($scope, $mdToast) {
        const vm = {
            debugPort: null,
            debugHost: null,
            stopDebugMode: null,
            stopDebug: stopDebug,
        };

        return vm;

        function stopDebug() {
            vm.stopDebugMode && vm.stopDebugMode();
            $mdToast.hide();
        }
    }

})();
