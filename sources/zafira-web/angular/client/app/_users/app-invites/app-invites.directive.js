import template from './app-invites.html';

(function () {
    'use strict';

    require('./app-invites.controller');

    angular.module('app.appInvites').directive('appInvites', () => {
        return {
            template,
            controller: 'AppInvitesController',
            scope: {
                tabs: '=',
                invitations: "=",
                groups: "="
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true
        };
    });

})();
