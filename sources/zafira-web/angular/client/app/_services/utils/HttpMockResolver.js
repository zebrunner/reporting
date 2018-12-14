(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$httpMock', ['$http', '$rootScope', '$q', '$transitions', HttpMockResolver])

    function HttpMockResolver($http, $rootScope, $q, $transitions) {

        var service = {
            // trigger mock
            back: function () { isBackClicked = true; },
            isBackClicked: function() {return isBackClicked},
            clearBackClicking: function() {isBackClicked = false;},
            // on controller fully init on back action
            clear: clear,
            'post': request('post'),
            'get': request('get'),
            'delete': request('delete'),
            'put': request('put')
        };

        var storage = {};
        var state = '';
        var isBackClicked = false;

        $transitions.onStart({}, function(trans) {
            var toState = trans.to();

            if (needStore(toState)) {
                state = toState.name;
                storage[state] = {};
            } else if (needRestore(toState)) {//TODO: what missed here?

            }
        });

        function needStore(toState) {
            return toState.store && ! isBackClicked;
        };

        function needRestore(toState) {
            return toState.store && isBackClicked && storage[toState.name];
        };

        return service;

        function request(method) {
            if (!$http[method]) {
                method = 'get';
            }

            return function(url) {
                if (isBackClicked && storage[state] && storage[state][url]) {
                    return $q.resolve(storage[state][url]);
                }

                var params = Array.from(arguments).slice(1);
                params.unshift(url);

                return $http[method].apply($http, params).then(function(response) {
                    if(storage[state]) {
                        storage[state][url] = response;
                    }

                    return response;
                });
            }
        }

        /**
         * Clear storage or item by url
         * @param url - if need clear only url
         */
        function clear(url) {
            if (! url && isBackClicked) {
                storage = {};
                state = '';
                service.clearBackClicking();
                return;
            }

            Reflect.deleteProperty(storage, url);
        }
    }
})();

// return $httpMock.post(API_URL + '/api/projects').then(UtilService.handleSuccess, UtilService.handleError('Unable to get projects list'));
