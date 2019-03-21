(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$mapper', ['$location', 'ProjectProvider', MapperUtilService])

    function MapperUtilService($location, ProjectProvider) {

        return {
            map: function (object, callback) {
                var result = {};
                angular.forEach(object, function (value, key) {
                    result[key] = callback(value);
                });
                return result;
            }
        };
    }
})();
