import template from './app-group.html';
import controller from './app-group.controller';

const appGroup = function appGroup() {
    return {
        template,
        controller,
        scope: {
            tabs: '=',
            groups: '=',
            showGroup: '&',
            deleteGroup: '&',
            search: '&',
            addUser: '&',
            deleteUser: '&',
        },
        restrict: 'E',
        replace: true
    };
};

export default appGroup;

