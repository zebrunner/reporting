(function () {
    'use strict';

    angular
    .module('app.appHeader')
    .controller('AppHeaderController', function(UserService, $rootScope) {
        'ngInject';

        return {
            mainData: {},
            get companyLogo() { return $rootScope.companyLogo; },
            get currentUser() { return UserService.currentUser; },
        };

    });
})();
