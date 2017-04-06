(function () {
    'use strict';

    angular.module('app.core', [
        // Angular modules
         'ngAnimate'
        ,'ngAria'
        ,'ngMessages'

        // Custom modules
        ,'app.layout'
        ,'app.i18n'
        
        // 3rd Party Modules
        ,'oc.lazyLoad'
        ,'ngMaterial'
        ,'ui.router'
        ,'duScroll'
    ]);

})();

