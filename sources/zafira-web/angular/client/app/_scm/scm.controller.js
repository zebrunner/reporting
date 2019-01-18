(function () {
    'use strict';

    angular
        .module('app.scm')
        .controller('ScmController', ['$scope', '$window', '$location', '$rootScope', ScmController])

    function ScmController($scope, $window, $location, $rootScope) {

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
