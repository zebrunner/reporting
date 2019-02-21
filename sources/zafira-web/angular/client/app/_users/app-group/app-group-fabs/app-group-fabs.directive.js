import controller from '../../user.view.controller';
import template from './app-group-fabs.html';

(function () {
    'use strict';

    angular.module('app.appGroup').directive('appGroupFabs', () => {
        return {
            template,
            controller,
            scope: {
            },
            restrict: 'E',
            replace: true
        };
    });

})();
