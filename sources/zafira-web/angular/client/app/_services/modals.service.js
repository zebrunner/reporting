(function() {
    'use strict';

    angular
        .module('app.services')
        .factory('modalsService', ['$mdDialog', modalsService]);

    function modalsService($mdDialog) {
        return {
            openModal: openModal,
        };

        /**
         * Create and open modal with default params: clickOutsideToClose = true and fullscreen = true
         * @param params {Object} - configuration for modal
         */
        function openModal(params) {
            const defaultParams = {
                clickOutsideToClose: true,
                fullscreen: true,
            };
            const mergedParams = angular.extend(defaultParams, params || {});

            return $mdDialog.show(mergedParams);
        }
    }
})();
