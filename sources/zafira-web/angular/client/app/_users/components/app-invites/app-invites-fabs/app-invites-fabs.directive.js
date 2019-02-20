import controller from '../../user.view.controller';

(function () {
    'use strict';

    angular.module('app.appInvites').directive('appInvitesFabs', () => {
        return {
            template: require('./app-invites-fabs.html'),
            controller,
            scope: {
                tools: '='
            },
            restrict: 'E',
            replace: true,
            link: () => {}
        };
    });

})();
