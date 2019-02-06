(function () {
    'use strict';

    angular.module('app.page')
    .controller('invoiceCtrl', invoiceCtrl)
    .controller('authCtrl', authCtrl);

    function invoiceCtrl($scope, $window) {
        'ngInject';

        var printContents, originalContents, popupWin;
        
        $scope.printInvoice = function() {
            printContents = document.getElementById('invoice').innerHTML;
            originalContents = document.body.innerHTML;        
            popupWin = window.open();
            popupWin.document.open();
            popupWin.document.write('<html><head><link rel="stylesheet" type="text/css" href="styles/main.css" /></head><body onload="window.print()">' + printContents + '</html>');
            popupWin.document.close();
        }
    }

    function authCtrl($scope, $window, $location) {
        'ngInject';

        $scope.login = function() {
            $location.url('/')
        }

        $scope.signup = function() {
            $location.url('/')
        }

        $scope.reset =    function() {
            $location.url('/')
        }

        $scope.unlock =    function() {
            $location.url('/')
        }     
    }

})(); 



