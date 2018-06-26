(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('ElasticsearchService', ['$q', 'esFactory', '$location', 'SettingsService', '$rootScope', ElasticsearchService])

    function ElasticsearchService($q, esFactory, $location, SettingsService, $rootScope) {

        var instance;

        var service = {};

        service.ping = ping;
        service.search = search;

        return service;

        function ping() {
            return $q(function (resolve, reject) {
                getInstance().then(function (esInstance) {
                    esInstance.ping({
                        requestTimeout: 30000
                    }, function (error) {
                        resolve(! error);
                    });
                });
            });
        };

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
        };

        function elasticsearch(params) {
            return $q(function (resolve, reject) {
                getInstance().then(function (esInstance) {
                    esInstance.search(params, function (err, res) {
                            if(err) {
                                reject(err);
                            } else {
                                resolve(res.hits.hits);
                            }
                        });
                })
            });
        };

        function getInstance() {
            return $q(function (resolve, reject) {
                if(instance) {
                    resolve(instance);
                } else {
                    prepareData().then(function (rs) {
                        resolve(rs);
                    }, function (rs) {
                        alertify.error(rs);
                        reject();
                    });
                }
            });
        };

        function prepareData() {
            return $q(function (resolve, reject) {
                SettingsService.getSettingByTool('ELASTICSEARCH').then(function (settingsRs) {
                    if(settingsRs.success) {
                        var url = settingsRs.data.find(function (element, index, array) {
                            return element.name.toLowerCase() == 'url';
                        });
                        if(url) {
                            resolve(createInstance(url.value));
                        } else {
                            reject({errorMessage: 'Cannot initialize elasticsearch url'});
                        }
                    }
                });
            });
        };

        function createInstance(url) {
            return esFactory({
                host: url,
                ssl: {
                    rejectUnauthorized: false
                }
            });
        };
    }
})();
