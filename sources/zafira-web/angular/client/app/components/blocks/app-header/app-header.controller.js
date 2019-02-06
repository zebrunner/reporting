(function () {
    'use strict';

    angular
    .module('app.appHeader')
    .controller('AppHeaderController', function(UserService, $rootScope) {
        'ngInject';

        return {
            mainData: {},
            companyLogo: $rootScope.companyLogo,
            currentUser: UserService.getCurrentUser()
        };

    });
})();
