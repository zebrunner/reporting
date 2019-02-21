import template from './app-invites.html';
import controller from './app-invites.controller';

const appInvites = function appInvites() {
    return {
        template,
        controller,
        scope: {
            tabs: '=',
            invitations: "=",
            groups: "="
        },
        controllerAs: '$ctrl',
        restrict: 'E',
        replace: true
    };
};

export default appInvites;
