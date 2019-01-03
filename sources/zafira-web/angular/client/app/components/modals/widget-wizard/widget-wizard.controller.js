(function () {
    'use strict';

    angular.module('app').controller('WidgetWizardController', [
        '$scope',
        '$mdDialog',
        '$q',
        'DashboardService',
        'ProjectProvider',
        WidgetWizardController]);

    function WidgetWizardController($scope, $mdDialog, $q, DashboardService, ProjectProvider, widget, isNew, dashboard, currentUserId) {

        $scope.templates = [
            {
                name: 'Detailed failures report',
                description: 'A line chart or line graph is a type of chart which displays information as a series of data points called \'markers\' connected by straight line segments.'
            },
            {
                name: 'Weekly test implementation progress',
                description: 'A line chartt is a type of chart which displays information.'
            },
            {
                name: 'Total tests',
                description: 'A line chartttt is a type of chart which displays information as a series of data points called \'markers\'.'
            },
            {
                name: 'Weekly test implementation progress',
                description: 'A line charttttttt or line graph is a type of chart which displays information as a series of data points called \'markers\'.'
            },
            {
                name: 'Total tests',
                description: 'A line charttttt is a type of chart which displays information as a series of data points called \'markers\'.'
            },
            {
                name: 'Weekly test implementation progress',
                description: 'A line chartttttttttt is a type of chart which displays information.'
            }
        ];

        $scope.widget = {
            template: {}
        };

        $scope.hide = function (rs, action) {
            //rs.action = action;
            $mdDialog.hide(rs);
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };

        (function initController() {
        })();
    }

})();
