import controller from '../../user.view.controller';
import template from './app-invites-fabs.html';

const appInvitesFabs = function appInvitesFabs() {
    return {
        template,
        controller,
        scope: {
            tools: '='
        },
        restrict: 'E',
        replace: true
    };
};

export default appInvitesFabs;
