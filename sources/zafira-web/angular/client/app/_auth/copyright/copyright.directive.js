(function () {
    'use strict';

    angular.module('app.copyright').directive('copyright', function () {
        return {
            template: require('./copyright.html'),
            controller: function copyrightController($rootScope) {
                'ngInject';

                return {
                    get version() { return $rootScope.version; }
                };
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true,
        };
    });
})();
