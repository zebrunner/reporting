(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('UtilService', [UtilService])
 
    function UtilService() {
        var service = {};
 
        service.untouchForm = untouchForm;
        service.truncate = truncate;
        service.handleSuccess = handleSuccess;
        service.handleError = handleError;
        service.isEmpty = isEmpty;
 
        return service;
 
        function untouchForm(form) {
        	form.$setPristine(); 
        	form.$setUntouched();
        }
        
        function truncate(fullStr, strLen) {
            if (fullStr == null || fullStr.length <= strLen) return fullStr;
            var separator = '...';
            var sepLen = separator.length,
                charsToShow = strLen - sepLen,
                frontChars = Math.ceil(charsToShow/2),
                backChars = Math.floor(charsToShow/2);
            return fullStr.substr(0, frontChars) +
                separator +
                fullStr.substr(fullStr.length - backChars);
        };
        
        function handleSuccess(res) {
            return { success: true, data: res.data };
        }
 
        function handleError(error) {
            return function () {
                return { success: false, message: error };
            };
        }
        
        function isEmpty(obj) {
        	return jQuery.isEmptyObject(obj);
        };
    }
})();