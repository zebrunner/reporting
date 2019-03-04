(function () {
    angular.module('app.layout')
        .controller('CustomizerCtrl', CustomizerCtrl);

    function CustomizerCtrl ($scope, $rootScope) {
        'ngInject';
        $rootScope.main = {};
    }
})();
