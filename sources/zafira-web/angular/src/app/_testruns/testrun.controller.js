(function () {
    'use strict';

    angular
        .module('app.testrun')
        .controller('TestRunListController', ['$scope', '$location', 'TestService', 'TestRunService', 'UtilService', TestRunListController])

    // **************************************************************************
    function TestRunListController($scope, $location, TestService, TestRunService, UtilService) {

        var DEFAULT_SC = {page : 1, pageSize : 20};

        $scope.UtilService = UtilService;

        $scope.sc = angular.copy(DEFAULT_SC);
        $scope.tests = {};

        $scope.search = function (page) {

        };

        (function initController() {
            $scope.search(1);
        })();
    }
})();
