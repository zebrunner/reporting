import template from './app-users-fabs.html';

const appUsersFabs = function appUserFabs() {
    return {
        template,
        scope: {
            createUser: '&'
        },
        restrict: 'E',
        replace: true
    };
};

export default appUsersFabs;
