(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ElasticsearchService', ['$q', 'esFactory', 'SettingsService', '$rootScope', ElasticsearchService])

    function ElasticsearchService($q, esFactory, SettingsService, $rootScope) {

        var instance = getInstance();

        var service = {};

        service.getInstance = getInstance;
        service.ping = ping;
        service.search = search;

        return service;

        function getInstance() {
            instance = instance || esFactory({
                host: $rootScope.elasticsearch.host + ':' + $rootScope.elasticsearch.port
            });
            return instance;
        }

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
                instance.search(params, function (err, res) {
                    if(err) {
                        reject(err);
                    } else {
                        resolve(res.hits.hits);
                    }
                });
            });
        }
    }
})();
