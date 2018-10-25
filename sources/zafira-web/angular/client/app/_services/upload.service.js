(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('UploadService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', UploadService])

    function UploadService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.upload = upload;
        service.uploadGoogleJson = uploadGoogleJson;

        return service;

        function upload(multipartFile, fileType) {
            return $httpMock.post(API_URL + '/api/upload?file=', multipartFile, {headers: {'FileType': fileType, 'Content-Type': undefined}, transformRequest : angular.identity}).then(UtilService.handleSuccess, UtilService.handleError('Unable to upload photo'));
        }

        function uploadGoogleJson(multipartFile) {
            return $httpMock.post(API_URL + '/api/upload/google?file=', multipartFile, {headers: {'Content-Type': undefined}, transformRequest : angular.identity}).then(UtilService.handleSuccess, UtilService.handleError('Unable to upload file'));
        }
    }
})();
