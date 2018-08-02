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
        service.count = count;
        service.isExists = isExists;

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

        function doAction(action, func, index, page, size, fromTime, query) {
            var from = page && size ? (page - 1) * size : undefined;
            var body = {};
            switch(action) {
                case 'SEARCH':
                    body = {
                        sort: [
                            {
                                timestamp: {
                                    order: "asc"
                                }
                            }
                        ],
                        size: size,
                        from: from
                    };
                case 'COUNT':
                    body.query = {
                        range : {
                            "timestamp" : {
                                gte : fromTime
                            }
                        }
                    };
                    break;
                case 'EXISTS':
                    body = {
                        index: index
                    };
                    break;
                default:
                    break;
            }
            var params = {
                index: index,
                from: from,
                size: size,
                q: query,
                body: body
            };
            func(params);
        };

        function search(index, page, size, fromTime, query) {
            return $q(function (resolve, reject) {
                doAction('SEARCH', function (params) {
                    getInstance().then(function (esInstance) {
                        esInstance.search(params, function (err, res) {
                            if (err) {
                                reject(err);
                            } else {
                                resolve(res.hits.hits);
                            }
                        });
                    });
                }, index, page, size, fromTime, query);
            });
        };

        function count(index, fromTime, query) {
            return $q(function (resolve, reject) {
                doAction('COUNT', function (params) {
                    getInstance().then(function (esInstance) {
                        esInstance.count(params, function (err, res) {
                            if (err) {
                                reject(err);
                            } else {
                                resolve(res.count);
                            }
                        });
                    });
                }, index, null, null, fromTime, query);
            });
        };

        function isExists(index) {
            return $q(function (resolve, reject) {
                doAction('EXISTS', function (params) {
                    getInstance().then(function (esInstance) {
                        esInstance.indices.exists(params.body, function (err, res) {
                            if (err) {
                                reject(err);
                            } else {
                                resolve(res);
                            }
                        });
                    });
                }, index, null, null, null, null);
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
