(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ArtifactService', ['$rootScope', '$window', '$q', '$timeout', 'UtilService', ArtifactService])

    function ArtifactService($rootScope, $window, $q, $timeout, UtilService) {
        var service = {};

        service.connectVnc = connectVnc;
        service.provideLogs = provideLogs;

        return service;

        var rfb;
        var display;
        var ratio;
        var container;
        var containerHeightProperty = 'offsetHeight';
        var containerWidthProperty = 'offsetWidth';

        function connectVnc(containerElement, heightProperty, widthProperty, wsURL) {
            container = containerElement;
            containerHeightProperty = heightProperty;
            containerWidthProperty = widthProperty;
            rfb = new RFB(angular.element('#vnc')[0], wsURL, { shared: true, credentials: { password: 'selenoid' } });
            //rfb._viewOnly = true;
            rfb.addEventListener("connect",  connected);
            rfb.addEventListener("disconnect",  disconnected);
            rfb.scaleViewport = true;
            rfb.resizeSession = true;
            display = rfb._display;
            display._scale = 1;
            angular.element($window).bind('resize', function(){
                autoscale(display, ratio, container);
            });
            return rfb;
        };

        function provideLogs(rabbitmq, testRun, test, logsContainer, func) {
            return $q(function (resolve, reject) {
                var rabbitmqWatcher = $rootScope.$watch('rabbitmq.enabled', function (newVal) {
                    if(newVal)
                    {
                        rabbitmqWatcher();
                        var wsName = 'logs';
                        var testLogsStomp = Stomp.over(new SockJS(rabbitmq.ws));
                        testLogsStomp.debug = null;
                        testLogsStomp.connect(rabbitmq.user, rabbitmq.pass, function () {

                            UtilService.websocketConnected(wsName);

                            testLogsStomp.subscribe("/exchange/logs/" + testRun.ciRunId, function (data) {
                                if((test && (testRun.ciRunId + "/" + test.id) == data.headers['correlation-id'])
                                    || (! test && data.headers['correlation-id'].startsWith(testRun.ciRunId))) {
                                    var log = JSON.parse(data.body.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
                                    func.call(this, log);
                                    if(logsContainer) {
                                        scrollLogsOnBottom(logsContainer);
                                    }
                                }
                            });
                            resolve({stomp: testLogsStomp, name: wsName});
                        }, function () {
                            UtilService.reconnectWebsocket(wsName, provideLogs);
                        });
                    }
                });
            });
        };

        function scrollLogsOnBottom(logsContainer) {
            logsContainer.scrollTop = logsContainer.scrollHeight;
        };

        function connected(e) {
            var canvas = document.getElementsByTagName("canvas")[0];
            ratio = canvas.width / canvas.height;
            autoscale(display, ratio, container);

        };

        function disconnected(e) {
        };

        function autoscale(display, ratio, window) {
            var size = calculateSize(window, ratio);
            display.autoscale(size.width, size.height, false);
        };

        function calculateSize(window, ratio) {
            var width = window[containerWidthProperty];
            var height = ratio > 1 ?  width / ratio : width * ratio;
            if(height > window[containerHeightProperty])
            {
                height = window[containerHeightProperty];
                width = ratio < 1 ? height / ratio : height * ratio;
            }
            return {height: height, width: width};
        };
    }
})();
