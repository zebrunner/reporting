(function () {
    'use strict';

    angular.module('app.services')
    .constant('mediaBreakpoints', {
        mobile: 600 //TODO: fix main menu if return to this breakpoint, need to clarify with designer
        // mobile: 480 //breakpoint value from main menu
    })
    .service('windowWidthService', ['$window', function($window) {
        this.windowWidth = $window.innerWidth;
        this.windowHeight = $window.innerHeight;
    }]);
})();
