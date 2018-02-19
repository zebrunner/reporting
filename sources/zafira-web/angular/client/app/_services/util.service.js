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
        service.settingsAsMap = settingsAsMap;

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
            return function (res) {
                if(res.status == 400 && res.data.validationErrors && res.data.validationErrors.length) {
                    error = res.data.validationErrors.map(function(validation) {
                        return validation.message;
                    }).join('\n');
                }
                return { success: false, message: error, error: res };
            };
        }

        function isEmpty(obj) {
        		return jQuery.isEmptyObject(obj);
        };

        function settingsAsMap(settings) {
            var map = {};
            if(settings)
                settings.forEach(function(setting) {
                    map[setting.name] = setting.value;
                });
            return map;
	    };
    }
})();
