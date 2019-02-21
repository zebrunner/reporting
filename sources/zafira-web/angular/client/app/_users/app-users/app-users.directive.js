import template from './app-users.html';
import controller from './app-users.controller';

const appUsers = function appUsers() {
    return {
        template,
        controller,
        scope: {
            onSearch: '&',
            source: '=',
            searchValue: '=',
            tabs: '='
        },
        controllerAs: '$ctrl',
        restrict: 'E',
        replace: true
    };
}

export default appUsers;
