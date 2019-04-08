import template from './app-invites-fabs.html';

const appInvitesFabs = function appInvitesFabs() {
    return {
        template,
        scope: {
            tools: '=',
            invite: '&',
        },
        restrict: 'E',
        replace: true
    };
};

export default appInvitesFabs;
