(function () {
    'use strict';

    angular.module('app').controller('DebugModeController', DebugModeController);

    function DebugModeController($scope, $mdToast) {
        'ngInject';

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
