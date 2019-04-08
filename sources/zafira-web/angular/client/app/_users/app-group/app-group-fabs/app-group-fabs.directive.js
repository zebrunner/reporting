
import template from './app-group-fabs.html';

const appGroupFabs = function appGroupFabs() {
    return {
        template,
        scope: {
            createGroup: '&',
        },
        restrict: 'E',
        replace: true
    };
};

export default appGroupFabs;
