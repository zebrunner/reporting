(function () {
    'use strict';

    angular.module('app.user')
        .controller('InviteListController', ['$scope', 'InvitationService', '$location', InviteListController]);

    // **************************************************************************
    function InviteListController($scope, InvitationService, $location) {

        $scope.invitations = InvitationService.invitations;
        $scope.sc = {};

        $scope.today = new Date().getTime();

        $scope.tabs[$scope.tabs.indexOfField('name', 'Invitations')].countFunc = function() {
            return $scope.invitations ? $scope.invitations.length : 0;
        };

        $scope.getAllInvitations = function () {
            InvitationService.getAllInvitations().then(function (rs) {
                if(rs.success) {
                    $scope.invitations.push.apply($scope.invitations, rs.data);
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.takeOff = function (invite, index) {
            InvitationService.deleteInvitation(invite.id).then(function (rs) {
                if(rs.success) {
                    $scope.invitations.splice(index, 1);
                    alertify.success('Invitation was taken off successfully.');
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.retryInvite = function (invite, index) {
            InvitationService.retryInvite(invite).then(function (rs) {
                if(rs.success) {
                    $scope.invitations.splice(index, 1, rs.data);
                    alertify.success('Invitation was sent successfully.');
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.searchUser = function(invite) {
            var userTab = $scope.tabs.find(function (tab) {
                return tab.name == 'Users';
            });
            var userTabIndex = $scope.tabs.indexOfField('name', userTab.name);
            $location.search('email', invite.email);
            $scope.switchTab(userTab, userTabIndex);
        };

        (function initController() {
            $scope.getAllInvitations();
        })();
    };
})();
