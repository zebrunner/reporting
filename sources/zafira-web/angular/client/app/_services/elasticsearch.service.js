(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ElasticsearchService', ['$q', 'esFactory', '$location', 'SettingsService', '$rootScope', ElasticsearchService])

    function ElasticsearchService($q, esFactory, $location, SettingsService, $rootScope) {

        var instance;

        $rootScope.$on('event:elasticsearch-toolsInitialized', function (event, data) {
            if(data.host && data.port) {
                instance = getInstance(data.host.value, data.port.value);
            } else {
                alertify.error('Cannot initialize elasticsearch host and port');
            }
        });

        var service = {};

        service.ping = ping;
        service.search = search;

        return service;

        function ping() {
            return $q(function (resolve, reject) {
                instance.ping({
                    requestTimeout: 30000
                }, function (error) {
                    resolve(! error);
                });
            });
        }

        function search(index, query, page, size) {
            return $q(function (resolve, reject) {
                var searchParams = {
                    index: index,
                    from: page && size ? (page - 1) * size : undefined,
                    size: size,
                    q: query,
                    body: {
                        sort: [
                            {
                                timestamp: {
                                    order: "asc"
                                }
                            }
                        ]
                    }
                };
                elasticsearch(searchParams).then(function (rs) {
                    resolve(rs);
                });
            });
        }

        function elasticsearch(params) {
            return $q(function (resolve, reject) {
                waitUntilInstanceInitialized(function () {
                    instance.search(params, function (err, res) {
                        if(err) {
                            reject(err);
                        } else {
                            resolve(res.hits.hits);
                        }
                    });
                });
            });
        }

        function getInstance(host, port) {
            instance = instance || esFactory({
                //host: host + ':' + port
                host: [
                    {
                        host: host,
                        auth: port,
                        protocol: $location.protocol(),
                        port: 9200
                    }
                ]
            });
            return instance;
        }

        function waitUntilInstanceInitialized(func) {
            var elasticsearchWatcher = $rootScope.$watchGroup(['elasticsearch.host', 'elasticsearch.port'], function (newVal) {
                if(newVal[0] && newVal[1]) {
                    func.call();
                    elasticsearchWatcher();
                }
            });
        };
    }
})();
