import controller from '../../user.view.controller';
import template from './app-group-item.html';

(function () {
    'use strict';

    angular.module('app.appGroup').directive('appGroupItem', () => {
        return {
            template,
            controller,
            scope: {
                group: '='
            },
            restrict: 'E',
            replace: true
        };
    });

})();