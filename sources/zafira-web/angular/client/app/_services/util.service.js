(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('UtilService', ['$rootScope', '$mdToast', '$timeout', '$q', '$window', UtilService]);

    function UtilService($rootScope, $mdToast, $timeout, $q, $window) {
        var service = {};

        service.untouchForm = untouchForm;
        service.resolveError = resolveError;
        service.truncate = truncate;
        service.handleSuccess = handleSuccess;
        service.handleError = handleError;
        service.isEmpty = isEmpty;
        service.settingsAsMap = settingsAsMap;
        service.reconnectWebsocket = reconnectWebsocket;
        service.websocketConnected = websocketConnected;
        service.setOffset = setOffset;
        service.showDeleteMessage = showDeleteMessage;

        service.validations = {
            username: [
                {
                    name: 'minlength',
                    message: 'Username must be between 3 and 50 characters'
                },
                {
                    name: 'maxlength',
                    message: 'Username must be between 3 and 50 characters'
                },
                {
                    name: 'pattern',
                    message: 'Username must have only latin letters, numbers and special characters',
                    additional: '_ -'
                },
                {
                    name: 'required',
                    message: 'Username required'
                }
            ],
            password: [
                {
                    name: 'minlength',
                    message: 'Password must be between 8 and 40 characters'
                },
                {
                    name: 'maxlength',
                    message: 'Password must be between 8 and 40 characters'
                },
                {
                    name: 'pattern',
                    message: 'Password must have only latin letters, numbers or special symbols',
                    additional: '@ ! _'
                },
                {
                    name: 'required',
                    message: 'Password required'
                }
            ],
            confirmPwd: [
                {
                    name: 'minlength',
                    message: 'Password must be between 8 and 40 characters'
                },
                {
                    name: 'maxlength',
                    message: 'Password must be between 8 and 40 characters'
                },
                {
                    name: 'pattern',
                    message: 'Password does not match',
                },
                {
                    name: 'required',
                    message: 'Password required'
                }
            ],
            name: [
                {
                    name: 'minlength',
                    message: 'Must be between 2 and 50 characters'
                },
                {
                    name: 'maxlength',
                    message: 'Must be between 2 and 50 characters'
                },
                {
                    name: 'pattern',
                    message: 'Must have only latin letters'
                }
            ]
        };

        return service;

        function untouchForm(form) {
        	form.$setPristine();
        	form.$setUntouched();
        }

        function truncate(fullStr, strLen) {
            if (fullStr == null || fullStr.length <= strLen) return fullStr;
            var separator = '...';
            var sepLen = separator.length,
                charsToShow = strLen - sepLen,
                frontChars = Math.ceil(charsToShow/2),
                backChars = Math.floor(charsToShow/2);
            return fullStr.substr(0, frontChars) +
                separator +
                fullStr.substr(fullStr.length - backChars);
        };

        function handleSuccess(res) {
            return { success: true, data: res.data };
        }

        function handleError(error) {
            return function (res) {
                if(res.status == 400 && res.data.validationErrors && res.data.validationErrors.length) {
                    error = res.data.validationErrors.map(function(validation) {
                        return validation.message;
                    }).join('\n');
                }
                return { success: false, message: error, error: res };
            };
        }

        function isEmpty(obj) {
        		return jQuery.isEmptyObject(obj);
        };

        function settingsAsMap(settings) {
            var map = {};
            if(settings)
                settings.forEach(function(setting) {
                    map[setting.name] = setting.value;
                });
            return map;
	    };

        // ************** Websockets **************

        function reconnectWebsocket(name, func) {
            if(! $rootScope.disconnectedWebsockets) {
                $rootScope.disconnectedWebsockets = {};
                $rootScope.disconnectedWebsockets.websockets = {};
                $rootScope.disconnectedWebsockets.toastOpened = false;
            }
            var attempt = $rootScope.disconnectedWebsockets.websockets[name] ? $rootScope.disconnectedWebsockets.websockets[name].attempt - 1 : 3;
            $rootScope.disconnectedWebsockets.websockets[name] = {function: func, attempt: attempt};
            reconnect(name);
        };

        function reconnect (name) {
            var websocket = $rootScope.disconnectedWebsockets.websockets[name];
            if(websocket.attempt > 0) {
                var delay = 5000;
                $timeout(function () {
                    tryToReconnect(name);
                }, delay);
            } else {
                if(! $rootScope.disconnectedWebsockets.toastOpened) {
                    $rootScope.disconnectedWebsockets.toastOpened = true;
                    showReconnectWebsocketToast();
                }
            }
        };

        function websocketConnected (name) {
            if($rootScope.disconnectedWebsockets && $rootScope.disconnectedWebsockets.websockets[name]) {
                delete $rootScope.disconnectedWebsockets.websockets[name];
            }
        };

        function tryToReconnect(name) {
            $rootScope.$applyAsync($rootScope.disconnectedWebsockets.websockets[name].function);
        };

        function showReconnectWebsocketToast() {
            $mdToast.show({
                hideDelay: 0,
                position: 'bottom right',
                scope: $rootScope,
                locals: {
                    reconnect: tryToReconnect
                },
                preserveScope: true,
                controller: 'WebsocketReconnectController',
                // templateUrl: 'app/components/toasts/websocket-reconnect/websocket-reconnect.html'
                template: require('../components/toasts/websocket-reconnect/websocket-reconnect.html')
            });
        };

        /**
         * Errors resolver
         */

        function resolveError(rs, form, ngMessage, defaultField) {
            return $q(function (resolve, reject) {
                var errorField = getErrorField(rs, defaultField);
                if(errorField) {
                    var showMessage = callError(function () {
                        return errorField;
                    }, form, errorField, getErrorMessage(rs), ngMessage);
                    resolve(showMessage);
                } else {
                    reject(rs);
                }
            });
        };

        function getErrorMessage(rs) {
            var result;
            if(rs.error && rs.error.status == 400 && rs.error.data.error) {
                result = rs.error.data.validationErrors ? rs.error.data.validationErrors[0].message : rs.error.data.error.message;
            }
            return result;
        };

        function getErrorField(rs, defaultField) {
            var result;
            if(rs.error && rs.error.status == 400 && rs.error.data.error) {
                result = rs.error.data.error.field;
                result = result ? result : defaultField;
            }
            return result;
        };

        function callError(func, form, inputName, errorMessage, ngMessage) {
            var result = false;
            var condition = func.call();
            if (condition) {
                form[inputName].errorMessage = errorMessage;
            } else {
                result = true;
            }
            form[inputName].$setValidity(ngMessage, result);
            return result;
        };

        function setOffset(event) {
            const bottomHeight = $window.innerHeight - event.target.clientHeight - event.clientY;

            $rootScope.currentOffset = 0;
            if (bottomHeight < 400) {
                $rootScope.currentOffset = -250 + bottomHeight;
            }
        }

        function buildMessage(keysToDelete, results, errors) {
            const result = {};

            if (keysToDelete.length === results.length + errors.length) {
                if (results.length) {
                    let message = results.length ? results[0].message : '';
                    let ids = '';

                    results.forEach(function(result, index) {
                        ids = ids + '#' + result.id;
                        if (index !== results.length - 1) {
                            ids += ', ';
                        }
                    });
                    message = message.format(results.length > 1 ? 's' : ' ', ids);
                    result.message = message;
                }
                if (errors.length) {
                    let errorIds = '';
                    let errorMessage = errors.length ? errors[0].message : '';

                    errors.forEach(function(result, index) {
                        errorIds = errorIds + '#' + result.id;
                        if (index !== errors.length - 1) {
                            errorIds += ', ';
                        }
                    });
                    errorMessage = errorMessage.format(errors.length > 1 ? 's' : ' ', errorIds);
                    result.errorMessage = errorMessage;
                }
            }

            return result;
        }

        function showDeleteMessage(rs, keysToDelete, results, errors) {
            let message;

            if (rs.success) {
                results.push(rs);
            } else {
                errors.push(rs);
            }

            message = buildMessage(keysToDelete, results, errors);
            if (message.message) {
                alertify.success(message.message);
            }
            if(message.errorMessage) {
                alertify.error(message.errorMessage);
            }
        }
    }
})();
