(function () {
    'use strict';

    angular.module('app.services')
    .constant('mediaBreakpoints', {
        mobile: 600 //TODO: fix main menu if return to this breakpoint, need to clarify with designer
        // mobile: 480 //breakpoint value from main menu
    })
    .service('windowWidthService', ['$window', 'mediaBreakpoints', windowWidthService]);

    function windowWidthService($window, mediaBreakpoints) {
        const srv = {
            windowWidth: $window.innerWidth,
            windowHeight: $window.innerHeight,
            isMobile: isMobile
        };

        return srv;

        function isMobile() {
            return srv.windowWidth <= mediaBreakpoints.mobile;
        }
    }
})();
