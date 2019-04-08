
import template from './app-group-item.html';

const appGroupItem = function appGroupItem() {
    return {
        template,
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