import controller from '../../user.view.controller';
import template from './app-invites-fabs.html';

(function () {
    'use strict';

    angular.module('app.appInvites').directive('appInvitesFabs', () => {
        return {
            template,
            controller,
            scope: {
                tools: '='
            },
            restrict: 'E',
            replace: true
        };
    });

})();
