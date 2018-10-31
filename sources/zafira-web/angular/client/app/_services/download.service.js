(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('DownloadService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', DownloadService])

    function DownloadService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.download = download;
        service.plainDownload = plainDownload;
        service.check = check;

        return service;

        function download(filename) {
            return $httpMock.get(API_URL + '/api/download?filename=' + filename, {responseType:'arraybuffer'}).then(function(res) {return {success: true, res: res}}, UtilService.handleError('Unable to download file \'' + filename + "\'"));
        }

        function plainDownload(url) {
            return $httpMock.get(url, {responseType:'blob'}).then(function(res) {return {success: true, res: res}}, UtilService.handleError('Unable to download file'));
        };

        function check(filename) {
            return $httpMock.get(API_URL + '/api/download/check?filename=' + filename).then(UtilService.handleSuccess, UtilService.handleError('Unable to check file existing'));
        }
    }
})();
