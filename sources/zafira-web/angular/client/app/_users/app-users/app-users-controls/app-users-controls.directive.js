import template from './app-users-controls.html';

const appUsersControls = function appUsersControls() {
    return {
        template,
        scope: {
            onSearch: '&',
            onReset: '&'
        },
        restrict: 'E',
        replace: true
    };
};

export default appUsersControls;

