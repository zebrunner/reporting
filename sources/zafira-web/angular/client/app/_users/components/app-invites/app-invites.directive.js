(function () {
    'use strict';

    require('./app-invites.controller');

    angular.module('app.appInvites').directive('appInvites', () => {
        return {
            template: require('./app-invites.html'),
            controller: 'AppInvitesController',
            scope: {
                tabs: '=',
                invitations: "=",
                groups: "="
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true,
            link:() => {}
        };
    });

})();
