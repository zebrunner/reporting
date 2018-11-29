(function () {
    'use strict';

    angular
        .module('app.scm')
        .controller('ScmController', ['$scope', '$window', '$location', '$rootScope', ScmController])

    function ScmController($scope, $window, $location, $rootScope) {

        function getCode() {
            var urlParams = new URLSearchParams(window.location.search);
            return urlParams.get('code');
        };

        (function init(){
            var code = getCode();
            if(code) {

                $window.close();
            }
        })();
    }
})();
