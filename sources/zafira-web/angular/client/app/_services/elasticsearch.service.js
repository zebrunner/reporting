(function() {
    'use strict';

    angular
        .module('app.services')
        .factory('ElasticsearchService', ['$q', '$http', 'esFactory', '$location', 'SettingsService', '$rootScope', ElasticsearchService])

    function ElasticsearchService($q, $http, esFactory, $location, SettingsService, $rootScope) {

        var instance;

        var instanceV2;

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
                        instance = rs;
                        resolve(instance);
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
                            resolve(createInstanceV2(url.value, user, password));
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

        function createInstanceV2(url, user, password) {
            if(instanceV2)
                return instanceV2;

            instanceV2 = {};
            instanceV2.url = url;
            instanceV2.user = user;
            instanceV2.password = password;
            instanceV2.basic = getAuthorizationValue(user.value, password.value);
            if(url.includes('@') && ! user.value && ! password.value && ! user.value.length && ! password.value.length) {
                var protocol_auth_slices = url.split('@')[0].split('://');
                if(protocol_auth_slices.length === 2 && protocol_auth_slices[1].includes(':')) {
                    var username_password_slices = protocol_auth_slices[1].split(':');
                    if(username_password_slices.length === 2) {
                        instanceV2.basic = getAuthorizationValue(username_password_slices[0], username_password_slices[1]);
                    }
                }
            }

            instanceV2.search = function (params, callback) {
                apply(buildHttpRequest(url, params.index, 'SEARCH', params.body, user, password), callback);
            };
            instanceV2.count = function (params, callback) {
                apply(buildHttpRequest(url, params.index, 'COUNT', params.body, user, password), callback);
            };
            instanceV2.indices = {
                exists: function (params, callback) {
                    apply(buildHttpRequest(url, params.index, 'EXISTS', null, user, password), callback);
                }
            };
            return instanceV2;
        }

        function apply(promise, callback) {
            promise.then(function (rs) {
                callback(null, rs.data);
            }, function (rs) {
                callback(rs.data, null);
            });
        };

        function getAuthorizationValue(username, password) {
            if(! username || ! password)
                return;
            return "Basic " + btoa(username + ":" + password)
        };

        function getCount(url, params) {
            return retrieveCount(url, params.index, params.body);
        };

        function getData(url, params) {
            return retrieveData(url, params.index, params.body);
        };

        function getExisting(url, params) {
            return retrieveExisting(url, params.index);
        };

        function retrieveCount(url, index, body) {
            return $http.post(url + '/' + index + '/_count', body).then(UtilService.handleSuccess, UtilService.handleError('Unable to get indices count from elasticsearch'));
        };

        function retrieveData(url, index, body) {
            return $http.post(url + '/' + index + '/_search', body).then(UtilService.handleSuccess, UtilService.handleError('Unable to get data from elasticsearch'));
        };

        function buildHttpRequest(url, index, action, body, username, password) {

            if(! url || ! action)
                return;

            var postfix = getPostfix(action, index);
            var request = {
                url: url + postfix
            };
            switch(action) {
                case 'SEARCH':
                case 'COUNT':
                    request.method = 'POST';
                    break;
                case 'EXISTS':
                    request.method = 'HEAD';
                    break;
                default:
                    break;
            }

            if(! request.method)
                return;

            if(['POST'].indexOf(request.method) !== -1) {
                request.data = body;
            }

            var authorizationValue = instanceV2.basic;
            if(authorizationValue) {
                request.headers = {
                    'Authorization': authorizationValue
                }
            }

            return $http(request);
        };

        function retrieveExisting(url, index) {
            return $q(function (resolve, reject) {
                $http.head(url + '/' + index).then(function (rs) {
                    resolve(true);
                }, function (rs) {
                    resolve(false);
                });
            });
        };

        function getPostfix(action, index) {
            if(! action)
                return;

            var postfix = '/' + index;

            switch(action) {
                case 'SEARCH':
                    postfix = postfix + '/_search';
                    break;
                case 'COUNT':
                    postfix = postfix + '/_count';
                    break;
                case 'EXISTS':
                    break;
                default:
                    break;
            }
            return postfix;
        };
    }
})();
