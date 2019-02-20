import controller from '../../user.view.controller';

(function () {
    'use strict';

    angular.module('app.appGroup').directive('appGroupFabs', () => {
        return {
            template: require('./app-group-fabs.html'),
            controller,
            scope: {
            },
            restrict: 'E',
            replace: true
        };
    });

})();
