(function () {
    'use strict';

    angular.module('app.user')
        .controller('GroupListController', GroupListController);

    // **************************************************************************
    function GroupListController($scope, $rootScope, $location, $mdDateRangePicker, $state, $mdDialog, UserService,
                                 GroupService, PermissionService, UtilService) {
        'ngInject';

        $scope.groups = GroupService.groups;

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
        $scope.deleteGroup = function(group) {
            GroupService.deleteGroup(group.id).then(function(rs) {
                if(rs.success)
                {
                    $scope.groups.splice($scope.groups.indexOfField('id', group.id), 1);
                    $scope.count --;
                    alertify.success('Group "' + group.name + '" was deleted');
                }
                else
                {
                    if(rs.error && rs.error.data && rs.error.data.error && rs.error.data.error.code == 403 && rs.error.data.error.message) {
                        alertify.error(rs.error.data.error.message);
                    } else {
                        alertify.error(rs.message);
                    }
                }
            });
        };
        $scope.addUserToGroup = function(user, group) {
            UserService.addUserToGroup(user, group.id).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('User "' + user.username + '" was added to group "' + group.name + '"');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.deleteUserFromGroup = function(user, group) {
            UserService.deleteUserFromGroup(user.id, group.id).then(function(rs) {
                if(rs.success)
                {
                    alertify.success('User "' + user.username + '" was deleted from group "' + group.name + '"');
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
