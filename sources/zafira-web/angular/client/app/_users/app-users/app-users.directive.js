import template from './app-users.html';
import controller from './app-users.controller';

const appUsers = function appUsers($timeout) {
    'ngInject';
    return {
        template,
        controller,
        scope: {
            onSearch: '&',
            source: '=',
            searchValue: '=',
            tabs: '=',
            onSearchChange: '&',
            searchAct: '='
        },
        controllerAs: '$ctrl',
        restrict: 'E',
        replace: true
    };
};

export default appUsers;
