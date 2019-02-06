(function () {
    'use strict';

    angular
        .module('app.scm')
        .controller('ScmController', ScmController);

    function ScmController($scope, $window, $location, $rootScope) {
        'ngInject';

        function getCode() {
            return $location.search()['code'];
        };

        (function init(){
            var code = getCode();
            if(code) {
                localStorage.setItem("code", code);
                $window.close();
            }
        })();
    }
})();
