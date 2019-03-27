import controller from '../../user.view.controller';
import template from './app-group-fabs.html';

const appGroupFabs = function appGroupFabs() {
    return {
        template,
        controller,
        scope: {
            createGroup: '&',
        },
        restrict: 'E',
        replace: true
    };
};

export default appGroupFabs;
