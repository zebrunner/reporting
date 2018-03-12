(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('DownloadService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', DownloadService])

    function DownloadService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.download = download;
        service.check = check;

        return service;

        function download(filename) {
            return $http.get(API_URL + '/api/download?filename=' + filename, {responseType:'arraybuffer'}).then(function(res) {return {success: true, res: res}}, UtilService.handleError('Unable to download file \'' + filename + "\'"));
        }

        function check(filename) {
            return $http.get(API_URL + '/api/download/check?filename=' + filename).then(UtilService.handleSuccess, UtilService.handleError('Unable to check file existing'));
        }
    }
})();
