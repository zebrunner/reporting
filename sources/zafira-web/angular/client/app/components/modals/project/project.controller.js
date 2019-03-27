(function () {
    'use strict';

    angular.module('app').controller('ProjectController', ProjectController);

    function ProjectController($scope, $mdDialog, ProjectService, UtilService) {
        'ngInject';

        $scope.project = {};
        $scope.UtilService = UtilService;

        $scope.createProject = function(project){
            ProjectService.createProject(project).then(function(rs) {
                if (rs.success) {
                    alertify.success("Project created successfully");
                } else
                {
                    alertify.error(rs.message);
                }
            });
            $scope.hide();
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }

})();
