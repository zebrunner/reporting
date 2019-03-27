'use strict';

const UserViewController = function UserViewController($scope, $rootScope, $location, $state, $mdDialog, UserService, GroupService, InvitationService, AuthService) {
    'ngInject';

    let DEFAULT_SC = {
        page: 1, pageSize: 20, selectedRange: {
            selectedTemplate: null,
            selectedTemplateName: null,
            dateStart: null,
            dateEnd: null,
            showTemplate: false,
            fullscreen: false
        }
    };

    const vm = {
        tabs: [
            {
                name: 'Users',
                countFunc: undefined,
                show: function () {
                    return AuthService.UserHasAnyPermission(['MODIFY_USERS', 'VIEW_USERS']);
                }
            },
            {
                name: 'Groups',
                countFunc: undefined,
                show: function () {
                    return AuthService.UserHasAnyPermission(['MODIFY_USER_GROUPS']);
                }
            },
            {
                name: 'Invitations',
                countFunc: undefined,
                show: function () {
                    return AuthService.UserHasAnyRole(['ROLE_ADMIN']) && AuthService.UserHasAnyPermission(['INVITE_USERS', 'MODIFY_INVITATIONS']);
                }
            }
        ],
        switchTab: switchTab,
        searchActive: false,
        isFiltered: false,
        activeTab: null,
        sc: angular.copy(DEFAULT_SC),
        onSearchChange: onSearchChange,
        search: search,
        isEqualDate: isEqualDate,
        reset: reset,
        showCreateUserDialog: showCreateUserDialog,
        groups: GroupService.groups,
        showGroupDialog: showGroupDialog,
        deleteGroup: deleteGroup,
        addUserToGroup: addUserToGroup,
        deleteUserFromGroup: deleteUserFromGroup,
        usersSearchCriteria: {},
        querySearch: querySearch,
        showInviteUsersDialog: showInviteUsersDialog,
        selectedTabIndex: 0,

        get tools() {
            return $rootScope.tools;
        }
    }

    vm.$onInit = initController;

    return vm; 

    function switchTab(toTab, index) {
        vm.activeTab = toTab;
        vm.selectedTabIndex = index != undefined ? index : vm.selectedTabIndex;
    };

    function onSearchChange(fields) {
        vm.searchActive = false;
        fields.forEach( function (field) {
            if (field.$modelValue) {
                vm.searchActive = true;
            }
        })
    };

    function search(page) {
        vm.sc.date = null;
        vm.sc.toDate = null;
        vm.sc.fromDate = null;

        if (page) {
            vm.sc.page = page;
        }

        if (vm.sc.selectedRange.dateStart && vm.sc.selectedRange.dateEnd) {
            if (!vm.isEqualDate()) {
                vm.sc.fromDate = vm.sc.selectedRange.dateStart;
                vm.sc.toDate = vm.sc.selectedRange.dateEnd;
            }
            else {
                vm.sc.date = vm.sc.selectedRange.dateStart;
            }
        }

        var requestVariables = $location.search();
        if (requestVariables) {
            for (var key in requestVariables) {
                if (key && requestVariables[key]) {
                    vm.sc[key] = requestVariables[key];
                }
            }
        }

        UserService.searchUsers(vm.sc).then(function (rs) {
            if (rs.success) {
                vm.sr = rs.data;
            }
            else {
                alertify.error(rs.message);
            }
        });
        vm.isFiltered = true;
    };

    function isEqualDate() {
        if (vm.sc.selectedRange.dateStart && vm.sc.selectedRange.dateEnd) {
            return vm.sc.selectedRange.dateStart.getTime() === vm.sc.selectedRange.dateEnd.getTime();
        }
    };

    function reset() {
        vm.sc = angular.copy(DEFAULT_SC);
        $location.url($location.path());
        vm.search();
        vm.searchActive = false;
        vm.isFiltered = false;
    };


    function showCreateUserDialog(event) {
        $mdDialog.show({
            controller: function ($scope, $mdDialog, UtilService) {
                'ngInject';

                $scope.UtilService = UtilService;
                $scope.createUser = function () {
                    UserService.createOrUpdateUser($scope.user).then(function (rs) {
                        if (rs.success) {
                            $scope.hide();
                            alertify.success('User created');
                        }
                        else {
                            alertify.error(rs.message);
                        }
                    });
                };
                $scope.hide = function () {
                    $mdDialog.hide(true);
                };
                $scope.cancel = function () {
                    $mdDialog.cancel(false);
                };
            },
            template: require('./app-users/modals/create_modal.html'),
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose: true,
            fullscreen: true
        })
            .then(function (answer) {
                if (answer) {
                    $state.reload();
                }
            }, function () {
            });
    };

    /************** Groups *************************************/
    
    function showGroupDialog(event, group) {
        $mdDialog.show({
            controller: GroupController,
            template: require('./app-group/modals/group_modal.html'),
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose: true,
            fullscreen: true,
            bindToController: true,
            locals: {
                group: group
            }
        })
            .then(function () {
            }, function (group) {
                if (group) {
                    var index = vm.groups.indexOfField('id', group.id);
                    if (index >= 0) {
                        vm.groups.splice(index, 1, group);
                    } else {
                        vm.groups.push(group);
                    }
                }
            });
    };

    function deleteGroup(group) {
        GroupService.deleteGroup(group.id).then(function (rs) {
            if (rs.success) {
                vm.groups.splice(vm.groups.indexOfField('id', group.id), 1);
                $scope.count--;
                alertify.success('Group "' + group.name + '" was deleted');
            }
            else {
                if (rs.error && rs.error.data && rs.error.data.error && rs.error.data.error.code == 403 && rs.error.data.error.message) {
                    alertify.error(rs.error.data.error.message);
                } else {
                    alertify.error(rs.message);
                }
            }
        });
    };

    function addUserToGroup(user, group) {
        UserService.addUserToGroup(user, group.id).then(function (rs) {
            if (rs.success) {
                alertify.success('User "' + user.username + '" was added to group "' + group.name + '"');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };
    function deleteUserFromGroup(user, group) {
        UserService.deleteUserFromGroup(user.id, group.id).then(function (rs) {
            if (rs.success) {
                alertify.success('User "' + user.username + '" was deleted from group "' + group.name + '"');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    function querySearch(criteria, group) {
        vm.usersSearchCriteria.username = criteria;
        return UserService.searchUsersWithQuery(vm.usersSearchCriteria).then(function (rs) {
            if (rs.success) {
                return rs.data.results.filter(searchFilter(group));
            }
            else {
                alertify.error(rs.message);
            }
        }).finally(function (rs) {
        });
    }

    function searchFilter(group) {
        return function filterFn(user) {
            var users = group.users;
            for (var i = 0; i < users.length; i++) {
                if (users[i].id == user.id) {
                    return false;
                }
            }
            return true;
        };
    }

    /************** Invitations *****************************/

    function showInviteUsersDialog(event) {
        $mdDialog.show({
            controller: InviteController,
            template: require('./app-invites/modals/invite_modal.html'),
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose: false,
            fullscreen: true,
            locals: {
                groups: GroupService.groups,
                isLDAPConnected: $rootScope.tools['LDAP']
            }
        })
            .then(function (invitations) {
            }, function (invitations) {
                if (invitations) {
                    invitations.forEach(function (invite) {
                        InvitationService.invitations.push(invite);
                    });
                }
            });
    };

    function initController() {
        vm.activeTab = vm.tabs[0];
        vm.search(1);
        vm.isFiltered = false;
    };
};

// **************************************************************************
function InviteController($scope, $mdDialog, InvitationService, UtilService, groups, isLDAPConnected) {
    'ngInject';

    $scope.isLDAPConnected = isLDAPConnected;

    $scope.source = null;

    $scope.tryInvite = false;
    $scope.emails = [];
    $scope.groups = angular.copy(groups);
    $scope.userGroup = undefined;

    $scope.SOURCES = ['INTERNAL', 'LDAP'];

    var chipCtrl;
    var startedEmail;

    $scope.setMdChipsCtrl = function (mdChipCtrl) {
        chipCtrl = mdChipCtrl;
    };

    $scope.invite = function (emails, form) {
        if (chipCtrl.chipBuffer) {
            startedEmail = chipCtrl.chipBuffer;
        }
        if (emails && emails.length > 0) {
            $scope.tryInvite = true;
            InvitationService.invite(toInvite(emails, $scope.userGroup, $scope.source)).then(function (rs) {
                if (rs.success) {
                    var message = emails.length > 1 ? "Invitations were sent." : "Invitation was sent.";
                    alertify.success(message);
                    if (!startedEmail) {
                        $scope.cancel(rs.data);
                    } else {
                        $scope.emails = [];
                        $scope.emails.push(startedEmail);
                        startedEmail = undefined;
                        chipCtrl.chipBuffer = '';
                    }
                } else {
                    UtilService.resolveError(rs, form, 'validationError', 'email').then(function (rs) {
                    }, function (rs) {
                        alertify.error(rs.message);
                    });
                }
                $scope.tryInvite = false;
            });
        }
    };

    function toInvite(emails, groupId, source) {
        return {
            invitationTypes: emails.map(function (email) {
                return { 'email': email, 'groupId': groupId, 'source': source && $scope.SOURCES.indexOf(source) >= 0 ? source : 'INTERNAL' };
            })
        };
    };

    $scope.checkAndTransformRecipient = function (email) {
        if (email.trim().indexOf(' ') >= 0) {
            var emailsArr = email.split(' ');
            $scope.emails = $scope.emails.concat(emailsArr.filter(function (value, index, self) {
                return emailsArr.indexOf(value) === index && $scope.emails.indexOf(value) == -1 && value.trim();
            }));
        }
    };

    $scope.removeRecipient = function (email) {
        delete $scope.emails[email];
    };

    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function (invitations) {
        $mdDialog.cancel(invitations);
    };
    (function initController() {

    })();
}

// **************************************************************************
function GroupController($scope, $mdDialog, GroupService, PermissionService, UtilService, group) {
    'ngInject';

    $scope.UtilService = UtilService;
    $scope.group = group ? angular.copy(group) : {};
    $scope.blocks = {};
    $scope.roles = [];
    $scope.group.users = $scope.group.users || [];
    $scope.showGroups = false;
    $scope.getRoles = function () {
        GroupService.getRoles().then(function (rs) {
            if (rs.success) {
                $scope.roles = rs.data;
            }
            else {
                alertify.error(rs.message);
            }
        });
    };
    $scope.getGroupsCount = function () {
        GroupService.getGroupsCount().then(function (rs) {
            if (rs.success) {
                $scope.count = rs.data;
            }
            else {
                alertify.error(rs.message);
            }
        });
    };
    $scope.getAllPermissions = function () {
        PermissionService.getAllPermissions().then(function (rs) {
            if (rs.success) {
                $scope.permissions = rs.data;
                $scope.aggregatePermissionsByBlocks(rs.data);
                collectPermissions();
            }
            else {
                alertify.error(rs.message);
            }
        });
    };
    $scope.createGroup = function (group) {
        group.permissions = $scope.permissions.filter(function (permission) {
            return permission.value;
        });
        GroupService.createGroup(group).then(function (rs) {
            if (rs.success) {
                $scope.cancel(rs.data);
                alertify.success('Group "' + group.name + '" was created');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };
    $scope.getGroup = function (id) {
        GroupService.getGroup(id).then(function (rs) {
            if (rs.success) {
                $scope.group = rs.data;
            }
            else {
                alertify.error(rs.message);
            }
        });
    };
    $scope.updateGroup = function (group) {
        group.permissions = $scope.permissions.filter(function (permission) {
            return permission.value;
        });
        GroupService.updateGroup(group).then(function (rs) {
            if (rs.success) {
                $scope.cancel(rs.data);
                alertify.success('Group updated');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.aggregatePermissionsByBlocks = function (permissions) {
        permissions.forEach(function (p, index) {
            if (!$scope.blocks[p.block]) {
                $scope.blocks[p.block] = {};
                $scope.blocks[p.block].selected = [];
                $scope.blocks[p.block].permissions = [];
            }
            $scope.blocks[p.block].permissions.push(p);
        })
    };

    function collectPermissions() {
        if ($scope.group.permissions) {
            $scope.group.permissions.forEach(function (p) {
                if ($scope.blocks[p.block].selected) {
                    $scope.blocks[p.block].selected = [];
                }
                $scope.blocks[p.block].permissions.forEach(function (perm) {
                    if (perm.id === p.id) {
                        perm.value = true;
                    }
                });
            });
        }
    };

    $scope.isCheckedBlock = function (blockName) {
        return $scope.getCheckedPermissions(blockName).length === $scope.blocks[blockName].permissions.length;
    };

    $scope.getCheckedPermissions = function (blockName) {
        return $scope.blocks[blockName].permissions.filter(function (permission) {
            return permission.value;
        })
    };

    $scope.setPermissionsValue = function (blockName, value) {
        $scope.blocks[blockName].permissions.forEach(function (permission) {
            permission.value = value;
        })
    };

    $scope.toggleAllPermissions = function (blockName) {
        var checkedPermissionsCount = $scope.getCheckedPermissions(blockName).length;
        if (checkedPermissionsCount === $scope.blocks[blockName].permissions.length) {
            $scope.setPermissionsValue(blockName, false);
        } else if (checkedPermissionsCount === 0 || checkedPermissionsCount > 0) {
            $scope.setPermissionsValue(blockName, true);
        }
    };

    $scope.isIndeterminateBlock = function (blockName) {
        var checkedPermissionsCount = $scope.getCheckedPermissions(blockName).length;
        return (checkedPermissionsCount !== 0 && checkedPermissionsCount !== $scope.blocks[blockName].permissions.length);
    };

    $scope.clearPermissions = function () {
        $scope.permissions.forEach(function (permission) {
            delete permission.value;
        })
    };
    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function (group) {
        $mdDialog.cancel(group);
    };
    (function initController() {
        $scope.getRoles();
        $scope.getAllPermissions();
    })();
}

export default UserViewController;

