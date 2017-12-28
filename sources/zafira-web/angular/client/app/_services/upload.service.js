(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('UploadService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', UploadService])

    function UploadService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.upload = upload;

        return service;

        function upload(multipartFile, fileType) {
            return $http.post(API_URL + '/api/upload?file=', multipartFile, {headers: {'FileType': fileType, 'Content-Type': undefined}, transformRequest : angular.identity}).then(UtilService.handleSuccess, UtilService.handleError('Unable to upload photo'));
        }
    }
})();
