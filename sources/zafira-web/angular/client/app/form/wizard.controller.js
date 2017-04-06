(function () {
    'use strict';

    angular.module('app.ui.form')
        .controller('WizardCtrl', ['$scope', '$q', '$timeout', 'WizardHandler', WizardCtrl]);

    function WizardCtrl ($scope, $q, $timeout, WizardHandler) {
        $scope.canExit = false;
        $scope.stepActive = true;

        $scope.finished = function() {
            alert("Wizard finished :)");
        };
        $scope.logStep = function() {
            console.log("Step continued");
        };
        $scope.goBack = function() {
            WizardHandler.wizard().goTo(0);
        };
        $scope.exitWithAPromise = function() {
            var d = $q.defer();
            $timeout(function() {
                d.resolve(true);
            }, 1000);
            return d.promise;
        };
        $scope.exitToggle = function() {
            $scope.canExit = !$scope.canExit;
        };
        $scope.stepToggle = function() {
            $scope.stepActive = !$scope.stepActive;
        }
        $scope.exitValidation = function() {
            return $scope.canExit;
        };
    }
})(); 
