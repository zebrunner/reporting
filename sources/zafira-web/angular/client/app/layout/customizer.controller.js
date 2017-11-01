(function () {
    angular.module('app.layout')
        .controller('CustomizerCtrl', ['$scope', '$rootScope', CustomizerCtrl]);

    function CustomizerCtrl ($scope, $rootScope) {
        $rootScope.main = {};

    }

})();
