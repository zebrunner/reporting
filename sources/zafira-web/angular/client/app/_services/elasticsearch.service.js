(function() {
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
            return $q(function(resolve, reject) {
                getInstance().then(function(esInstance) {
                    esInstance.ping({
                        requestTimeout: 30000
                    }, function(error) {
                        resolve(!error);
                    });
                });
            });
        };

        function doAction(action, func, index, searchField, from, size, fromTime, query) {
            var body = {};
            switch (action) {
                case 'SEARCH':
                    body = {
                        sort: [{
                            '@timestamp': {
                                order: "asc"
                            }
                        }],
                        size: size,
                        from: from
                    };
                case 'COUNT':
                    body.query = {
                        bool: {
                            must: [{
                                    term: searchField
                                },
                                {
                                    range: {
                                        '@timestamp': {
                                            gte: fromTime
                                        }
                                    }
                                }
                            ]
                        }
                    };
                    break;
                case 'EXISTS':
                    body = {
                        index: index,
                        query: {
                            term: searchField
                        }
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

        function search(index, searchField, from, page, size, fromTime, query) {
            return $q(function(resolve, reject) {
                doAction('SEARCH', function(params) {
                    getInstance().then(function(esInstance) {
                        esInstance.search(params, function(err, res) {
                            if (err) {
                                reject(err);
                            } else {
                                resolve(res.hits.hits);
                            }
                        });
                    });
                }, index, searchField, from || from == 0 ? from : page && size ? Math.round((page - 1) * size) : undefined, size, fromTime, query);
            });
        };

        function count(index, searchField, fromTime, query) {
            return $q(function(resolve, reject) {
                doAction('COUNT', function(params) {
                    getInstance().then(function(esInstance) {
                        esInstance.count(params, function(err, res) {
                            if (err) {
                                reject(err);
                            } else {
                                resolve(res.count);
                            }
                        });
                    });
                }, index, searchField, null, null, fromTime, query);
            });
        };

        function isExists(index, searchField) {
            return $q(function(resolve, reject) {
                doAction('EXISTS', function(params) {
                    getInstance().then(function(esInstance) {
                        esInstance.indices.exists(params.body, function(err, res) {
                            if (err) {
                                reject(err);
                            } else {
                                resolve(res);
                            }
                        });
                    });
                }, index, searchField, null, null, null, null);
            });
        };

        function getInstance() {
            return $q(function(resolve, reject) {
                if (instance) {
                    resolve(instance);
                } else {
                    prepareData().then(function(rs) {
                        resolve(rs);
                    }, function(rs) {
                        alertify.error(rs.errorMessage);
                        reject();
                    });
                }
            });
        };

        function prepareData() {
            return $q(function(resolve, reject) {
                SettingsService.getSettingByTool('ELASTICSEARCH').then(function(settingsRs) {
                    if (settingsRs.success) {
                        var url = settingsRs.data.find(function(element, index, array) {
                            return element.name.toLowerCase() == 'url';
                        });
                        var user = settingsRs.data.find(function(element, index, array) {
                            return element.name.toLowerCase() == 'user';
                        });
                        var password = settingsRs.data.find(function(element, index, array) {
                            return element.name.toLowerCase() == 'password';
                        });
                        if (url && url.value) {
                            resolve(createInstance(url.value, user, password));
                        } else {
                            reject({
                                errorMessage: 'Cannot initialize elasticsearch url'
                            });
                        }
                    }
                });
            });
        };

        function createInstance(url, user, password) {
            if (user && user.value && password && password.value) {
                var protocol = url.split('://')[0];
                var host = url.split('://')[1].split(':')[0];
                var port = url.split(':')[2].match('\\d+')[0];
                return esFactory({
                    host: [{
                        protocol: protocol,
                        host: host,
                        port: port,
                        auth: user.value + ':' + password.value
                    }],
                    ssl: {
                        rejectUnauthorized: false
                    }
                });
            } else {
                return esFactory({
                    host: url,
                    ssl: {
                        rejectUnauthorized: false
                    }
                });
            }
        };
    }
})();