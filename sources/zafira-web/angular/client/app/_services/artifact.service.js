(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ArtifactService', ['$rootScope', '$window', '$q', '$timeout', 'UtilService', ArtifactService])

    function ArtifactService($rootScope, $window, $q, $timeout, UtilService) {
        var service = {};

        var display;
        var ratio;
        var container;
        var containerHeightProperty = 'offsetHeight';
        var containerWidthProperty = 'offsetWidth';

        service.connectVnc = connectVnc;
        service.resize = resize;
        service.provideLogs = provideLogs;

        return service;

        function connectVnc(containerElement, heightProperty, widthProperty, wsURL, disconnectFunc) {
            container = containerElement;
            containerHeightProperty = heightProperty;
            containerWidthProperty = widthProperty;
            var rfb = new RFB(angular.element('#vnc')[0], wsURL, { shared: true, credentials: { password: 'selenoid' } });
            //rfb._viewOnly = true;
            rfb.addEventListener("connect",  connected);
            rfb.addEventListener("disconnect",  disconnectFunc ? disconnectFunc : disconnected);
            rfb.scaleViewport = true;
            rfb.resizeSession = true;
            display = rfb._display;
            display._scale = 1;
            angular.element($window).bind('resize', function(){
                autoscale(display, ratio, container);
            });
            return rfb;
        };

        function provideLogs(rabbitmq, testRun, test, logsContainer, needReconnect, func) {
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
                                if((test && (testRun.ciRunId + "_" + test.id) == data.headers['correlation-id'] || (data.headers['correlation-id'].includes(testRun.ciRunId + "_" + test.id + '_') && data.headers['correlation-id'].startsWith(testRun.ciRunId)))
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
                            if(needReconnect) {
                                UtilService.reconnectWebsocket(wsName, provideLogs);
                            }
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
            var height;
            var width;
            if(ratio > 1) {
                width = window[containerWidthProperty];
                height = width / ratio;
            } else {
                height = window[containerHeightProperty];
                width = height * ratio;
            }
            return {height: height, width: width};
        };

        function resize(element, rfb) {
            container = element;
            display = rfb._display;
            connected();
        };
    }
})();
