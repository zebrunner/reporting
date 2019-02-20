(function () {
    'use strict';

    angular.module('app.appGroup')
        .controller('AppGroupController', AppGroupController);

    // **************************************************************************
    function AppGroupController($scope, UserService, GroupService, UtilService) {
        'ngInject';

        $scope.tabs[$scope.tabs.indexOfField('name', 'Groups')].countFunc = function() {
            return $scope.groups.length;
        };

        $scope.UtilService = UtilService;

        $scope.getGroupsCount = function() {
            GroupService.getGroupsCount().then(function(rs) {
                if(rs.success)
                {
                    $scope.count = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.getAllGroups = function() {
            GroupService.getAllGroups().then(function(rs) {
                if(rs.success) {
                    $scope.groups.push.apply($scope.groups, rs.data);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };
        

        (function initController() {
            $scope.getAllGroups();
        })();
    };
})();
