import controller from '../../user.view.controller';
import template from './app-group-item.html';

const appGroupItem = function appGroupItem() {
    return {
        template,
        controller,
        scope: {
            group: '=',
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

export default appGroupItem;