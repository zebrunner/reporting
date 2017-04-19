(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('UtilService', [UtilService])
 
    function UtilService() {
        var service = {};
 
        service.untouchForm = untouchForm;
        service.handleSuccess = handleSuccess;
        service.handleError = handleError;
 
        return service;
 
        function untouchForm(form) {
        	form.$setPristine(); 
        	form.$setUntouched();
        }
        
        function handleSuccess(res) {
            return { success: true, data: res.data };
        }
 
        function handleError(error) {
            return function () {
                return { success: false, message: error };
            };
        }
    }
})();