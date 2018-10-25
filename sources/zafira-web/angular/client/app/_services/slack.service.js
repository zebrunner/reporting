(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('SlackService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', SlackService])

    function SlackService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.triggerReviewNotif = triggerReviewNotif;

        return service;

        function triggerReviewNotif(id) {
            return $httpMock.get(API_URL + '/api/slack/triggerReviewNotif/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to trigger review notif'));
        }
    }
})();
