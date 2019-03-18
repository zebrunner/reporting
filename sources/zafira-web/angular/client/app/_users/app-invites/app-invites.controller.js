const AppInvitesController = function AppInvitesController($scope, InvitationService, $location) {
    'ngInject';

    $scope.sc = {};

    $scope.today = new Date().getTime();
    $scope.tabs[$scope.tabs.indexOfField('name', 'Invitations')].countFunc = function () {
        return $scope.invitations ? $scope.invitations.length : 0;
    };

    $scope.takeOff = function (invite, index) {
        InvitationService.deleteInvitation(invite.id).then(function (rs) {
            if (rs.success) {
                $scope.invitations.splice(index, 1);
                alertify.success('Invitation was taken off successfully.');
            } else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.retryInvite = function (invite, index) {
        InvitationService.retryInvite(invite).then(function (rs) {
            if (rs.success) {
                $scope.invitations.splice(index, 1, rs.data);
                alertify.success('Invitation was sent successfully.');
            } else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.searchUser = function (invite) {
        var userTab = $scope.tabs.find(function (tab) {
            return tab.name == 'Users';
        });
        var userTabIndex = $scope.tabs.indexOfField('name', userTab.name);
        $location.search('email', invite.email);
        $scope.search(1);
        $scope.switchTab(userTab, userTabIndex);
    };

    $scope.getAllInvitations = function () {
        InvitationService.getAllInvitations().then(function (rs) {
            if (rs.success) {
                $scope.invitations.push.apply($scope.invitations, rs.data);
            } else {
                alertify.error(rs.message);
            }
        });
    };

    (function initController() {
        $scope.getAllInvitations();
    })();
};

export default AppInvitesController;
