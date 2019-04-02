'use strict';

const elasticsearchService = function elasticsearchService($http, $q, $location, SettingsService, UtilService) {
    'ngInject';

    let instance;
    const service = {
        ping,
        search,
        count,
        isExists,
    };

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
    }

    function doAction(action, func, index, searchField, from, size, fromTime, query) {
        var body = {};
        switch (action) {
            case 'SEARCH':
                body = {
                    sort: [{
                        '@timestamp': {
                            order: 'asc'
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
                        }]
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
    }

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
            }, index, searchField, from || from === 0 ? from : page && size ? Math.round((page - 1) * size) : undefined, size, fromTime, query);
        });
    }

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
    }

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
    }

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
    }

    function prepareData() {
        return $q(function(resolve, reject) {
            SettingsService.getSettingByTool('ELASTICSEARCH').then(function(settingsRs) {
                if (settingsRs.success) {
                    var url = settingsRs.data.find(function(element, index, array) {
                        return element.name.toLowerCase() === 'url';
                    });
                    var user = settingsRs.data.find(function(element, index, array) {
                        return element.name.toLowerCase() === 'user';
                    });
                    var password = settingsRs.data.find(function(element, index, array) {
                        return element.name.toLowerCase() === 'password';
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
    }

    function createInstance(url, user, password) {
        if (instance) { return instance; }

        instance = {};
        instance.url = url;
        instance.user = user;
        instance.password = password;
        instance.basic = getAuthorizationValue(user.value, password.value);
        if(url.includes('@') && ! user.value && ! password.value && ! user.value.length && ! password.value.length) {
            var protocol_auth_slices = url.split('@')[0].split('://');
            if(protocol_auth_slices.length === 2 && protocol_auth_slices[1].includes(':')) {
                var username_password_slices = protocol_auth_slices[1].split(':');
                if(username_password_slices.length === 2) {
                    instance.basic = getAuthorizationValue(username_password_slices[0], username_password_slices[1]);
                }
            }
        }

        instance.search = function (params, callback) {
            apply(buildHttpRequest(url, params.index, 'SEARCH', params.body), callback);
        };
        instance.count = function (params, callback) {
            apply(buildHttpRequest(url, params.index, 'COUNT', params.body), callback);
        };
        instance.indices = {
            exists: function (params, callback) {
                apply(buildHttpRequest(url, params.index, 'EXISTS', null), callback);
            }
        };
        return instance;
    }

    function apply(promise, callback) {
        promise.then(function (rs) {
            return callback(null, rs.data);
        }, function (rs) {
            const error = (rs && rs.data) || UtilService.handleError('Unable to get data from elasticsearch')({});

            return callback(error, null);
        });
    }

    function getAuthorizationValue(username, password) {
        if (!username || !password) { return; }

        return "Basic " + btoa(username + ":" + password);
    }

    function buildHttpRequest(url, index, action, body) {
        if (!url || !action) { return; }

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

        if (!request.method) { return; }

        if(['POST'].indexOf(request.method) !== -1) {
            request.data = body;
        }

        var authorizationValue = instance.basic;
        if (authorizationValue) {
            request.headers = { 'Authorization': authorizationValue };
        }

        return $http(request);
    }

    function getPostfix(action, index) {
        if (!action) { return; }

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
    }
};

export default elasticsearchService;
