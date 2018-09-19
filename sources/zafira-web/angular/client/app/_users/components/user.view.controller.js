(function () {
    'use strict';

    angular.module('app.user')
        .controller('UserViewController', ['$scope', '$rootScope', '$location', '$mdDateRangePicker', '$state', '$mdDialog', 'UserService', 'GroupService', 'PermissionService', 'InvitationService', 'AuthService', 'UtilService', UserViewController]);

    // **************************************************************************
    function UserViewController($scope, $rootScope, $location, $mdDateRangePicker, $state, $mdDialog, UserService, GroupService, PermissionService, InvitationService, AuthService, UtilService) {

        var COMPONENTS_ROOT = 'app/_users/components/';

        GroupService.groups = [];
        InvitationService.invitations = [];

        $scope.tabs = [
            {
                name: 'Users',
                countFunc: undefined,
                template: COMPONENTS_ROOT + 'users/user.table.html',
                controls: COMPONENTS_ROOT + 'users/user.controls.html',
                fabControls: COMPONENTS_ROOT + 'users/user.fab.controls.html',
                fabControlsCount: 1,
                show: function () {
                    return AuthService.UserHasAnyPermission(['MODIFY_USERS', 'VIEW_USERS']);
                },
                onActive: function () {
                    $scope.search(1);
                }
            },
            {
                name: 'Groups',
                countFunc: undefined,
                template: COMPONENTS_ROOT + 'groups/group.table.html',
                fabControls: COMPONENTS_ROOT + 'groups/group.fab.controls.html',
                fabControlsCount: 1,
                show: function () {
                    return AuthService.UserHasAnyPermission(['MODIFY_USER_GROUPS']);
                },
                onActive: function () {

                }
            },
            {
                name: 'Invitations',
                countFunc: undefined,
                template: COMPONENTS_ROOT + 'invites/invite.table.html',
                controls: COMPONENTS_ROOT + 'invites/invite.controls.html',
                fabControls: COMPONENTS_ROOT + 'invites/invite.fab.controls.html',
                fabControlsCount: 1,
                show: function () {
                    return AuthService.UserHasAnyRole(['ROLE_ADMIN']) && AuthService.UserHasAnyPermission(['INVITE_USERS', 'MODIFY_INVITATIONS']);
                },
                onActive: function () {

                }
            }
        ];

        $scope.activeTab = $scope.tabs[0];

        $scope.switchTab = function (toTab, index) {
            $scope.activeTab = toTab;
            $scope.selectedTabIndex = index != undefined ? index : $scope.selectedTabIndex;
            $scope.activeTab.onActive();
        };

        var DEFAULT_SC = {page : 1, pageSize : 20};
        $scope.sc = angular.copy(DEFAULT_SC);

        $scope.search = function (page) {
            $scope.sc.date = null;
            $scope.sc.toDate = null;
            $scope.sc.fromDate = null;

            if(page)
            {
                $scope.sc.page = page;
            }

            if ($scope.selectedRange.dateStart && $scope.selectedRange.dateEnd) {
                if(!$scope.isEqualDate()){
                    $scope.sc.fromDate = $scope.selectedRange.dateStart;
                    $scope.sc.toDate = $scope.selectedRange.dateEnd;
                }
                else {
                    $scope.sc.date = $scope.selectedRange.dateStart;
                }
            }

            var requestVariables = $location.search();
            if(requestVariables) {
                for(var key in requestVariables) {
                    if(key && requestVariables[key]) {
                        $scope.sc[key] = requestVariables[key];
                    }
                }
            }

            UserService.searchUsers($scope.sc).then(function(rs) {
                if(rs.success)
                {
                    $scope.sr = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.isEqualDate = function() {
            if($scope.selectedRange.dateStart && $scope.selectedRange.dateEnd){
                return $scope.selectedRange.dateStart.getTime() === $scope.selectedRange.dateEnd.getTime();
            }
        };

        $scope.reset = function () {
            $scope.selectedRange.dateStart = null;
            $scope.selectedRange.dateEnd = null;
            $scope.sc = angular.copy(DEFAULT_SC);
            $location.url($location.path());
            $scope.search();
        };

        $scope.isDateChosen = true;
        $scope.isDateBetween = false;

        $scope.changePeriod = function () {
            if ($scope.sc.period == "between") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = true;
            }
            else if ($scope.sc.period == "before" || $scope.sc.period == "after" || $scope.sc.period == "") {
                $scope.isDateChosen = true;
                $scope.isDateBetween = false;
            }
            else {
                $scope.isDateChosen = false;
                $scope.isDateBetween = false;
            }
        };

        /**
         DataRangePicker functionality
         */

        var tmpToday = new Date();
        $scope.selectedRange = {
            selectedTemplate: null,
            selectedTemplateName: null,
            dateStart: null,
            dateEnd: null,
            showTemplate: false,
            fullscreen: false
        };

        $scope.onSelect = function(scope) {
            console.log($scope.selectedRange.selectedTemplateName);
            return $scope.selectedRange.selectedTemplateName;
        };

        $scope.pick = function($event, showTemplate) {
            $scope.selectedRange.showTemplate = showTemplate;
            $mdDateRangePicker.show({
                targetEvent: $event,
                model: $scope.selectedRange
            }).then(function(result) {
                if (result) $scope.selectedRange = result;
            })
        };

        $scope.clear = function() {
            $scope.selectedRange.selectedTemplate = null;
            $scope.selectedRange.selectedTemplateName = null;
            $scope.selectedRange.dateStart = null;
            $scope.selectedRange.dateEnd = null;
        };

        $scope.isFuture = function($date) {
            return $date.getTime() < new Date().getTime();
        };

        $scope.showCreateUserDialog = function(event) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog) {
                    $scope.createUser = function() {
                        UserService.createOrUpdateUser($scope.user).then(function(rs) {
                            if(rs.success)
                            {
                                $scope.hide();
                                alertify.success('User created');
                            }
                            else
                            {
                                alertify.error(rs.message);
                            }
                        });
                    };
                    $scope.hide = function() {
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel(false);
                    };
                },
                templateUrl: 'app/_users/components/users/create_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                    if(answer)
                    {
                        $state.reload();
                    }
                }, function() {
                });
        };

        /************** Groups *************************************/

        $scope.groups = GroupService.groups;

        $scope.showGroupDialog = function(event, group) {
            $mdDialog.show({
                controller: GroupController,
                templateUrl: 'app/_users/components/groups/group_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    group: group
                }
            })
                .then(function() {
                }, function(group) {
                    if(group) {
                        var index = $scope.groups.indexOfField('id', group.id);
                        if(index >= 0) {
                            $scope.groups.splice(index, 1, group);
                        } else {
                            $scope.groups.push(group);
                        }
                    }
                });
        };

        $scope.usersSearchCriteria = {};
        $scope.querySearch = querySearch;

        function querySearch (criteria, group) {
            $scope.usersSearchCriteria.username = criteria;
            return UserService.searchUsersWithQuery($scope.usersSearchCriteria).then(function(rs) {
                if(rs.success)
                {
                    return rs.data.results.filter(searchFilter(group));
                }
                else
                {
                    alertify.error(rs.message);
                }
            }).finally(function (rs) {
            });
        }

        function searchFilter(group) {
            return function filterFn(user) {
                var users = group.users;
                for(var i = 0; i < users.length; i++) {
                    if(users[i].id == user.id) {
                        return false;
                    }
                }
                return true;
            };
        }

        /************** Invitations *****************************/

        $scope.invitations = InvitationService.invitations;

        $scope.showInviteUsersDialog = function(event) {
            $mdDialog.show({
                controller: InviteController,
                templateUrl: 'app/_users/components/invites/invite_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:false,
                fullscreen: true,
                locals: {
                    groups: GroupService.groups
                }
            })
                .then(function(invitations) {
                }, function(invitations) {
                    if(invitations) {
                        invitations.forEach(function (invite) {
                            $scope.invitations.push(invite);
                        });
                    }
                });
        };

        (function initController() {
        })();
    };

    // **************************************************************************
    function InviteController($scope, $mdDialog, InvitationService, UtilService, groups) {

        $scope.tryInvite = false;
        $scope.emails = [];
        $scope.groups = angular.copy(groups);
        $scope.userGroup = undefined;

        var chipCtrl;
        var startedEmail;

        $scope.setMdChipsCtrl = function(mdChipCtrl) {
            chipCtrl = mdChipCtrl;
        };

        $scope.invite = function(emails, form) {
            if(chipCtrl.chipBuffer) {
                startedEmail = chipCtrl.chipBuffer;
            }
            if(emails && emails.length > 0) {
                $scope.tryInvite = true;
                InvitationService.invite(toInvite(emails, $scope.userGroup)).then(function (rs) {
                    if (rs.success) {
                        var message = emails.length > 1 ? "Invitations were sent." : "Invitation was sent.";
                        alertify.success(message);
                        if(! startedEmail) {
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

        function toInvite(emails, groupId) {
            return {
                invitationTypes: emails.map(function (email) {
                                return {'email': email, 'groupId': groupId};
                            })};
        };

        $scope.checkAndTransformRecipient = function (email) {
            if(email.trim().indexOf(' ') >= 0) {
                var emailsArr = email.split(' ');
                $scope.emails = $scope.emails.concat(emailsArr.filter(function (value, index, self) {
                    return emailsArr.indexOf(value) === index && $scope.emails.indexOf(value) == -1 && value.trim();
                }));
            }
        };

        $scope.removeRecipient = function (email) {
            delete $scope.emails[email];
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function(invitations) {
            $mdDialog.cancel(invitations);
        };
        (function initController() {
        })();
    }

    // **************************************************************************
    function GroupController($scope, $mdDialog, UserService, GroupService, PermissionService, UtilService, group) {
        $scope.UtilService = UtilService;
        $scope.group = group ? angular.copy(group) : {};
        $scope.blocks = {};
        $scope.roles = [];
        $scope.group.users = [];
        $scope.showGroups = false;
        $scope.getRoles = function() {
            GroupService.getRoles().then(function(rs) {
                if(rs.success)
                {
                    $scope.roles = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
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
        $scope.getAllPermissions = function() {
            PermissionService.getAllPermissions().then(function(rs) {
                if(rs.success)
                {
                    $scope.permissions = rs.data;
                    $scope.aggregatePermissionsByBlocks(rs.data);
                    collectPermissions();
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.createGroup = function(group) {
            group.permissions = $scope.permissions.filter(function(permission) {
                return permission.value;
            });
            GroupService.createGroup(group).then(function(rs) {
                if(rs.success)
                {
                    $scope.cancel(rs.data);
                    alertify.success('Group "' + group.name + '" was created');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.getGroup = function(id) {
            GroupService.getGroup(id).then(function(rs) {
                if(rs.success)
                {
                    $scope.group = rs.data;
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };
        $scope.updateGroup = function(group) {
            group.permissions = $scope.permissions.filter(function(permission) {
                return permission.value;
            });
            GroupService.updateGroup(group).then(function(rs) {
                if(rs.success)
                {
                    $scope.cancel(rs.data);
                    alertify.success('Group updated');
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.aggregatePermissionsByBlocks = function (permissions) {
            permissions.forEach(function(p, index) {
                if(!$scope.blocks[p.block]) {
                    $scope.blocks[p.block] = {};
                    $scope.blocks[p.block].selected = [];
                    $scope.blocks[p.block].permissions = [];
                }
                $scope.blocks[p.block].permissions.push(p);
            })
        };

        function collectPermissions() {
            if($scope.group.permissions) {
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

        $scope.isCheckedBlock = function(blockName) {
            return $scope.getCheckedPermissions(blockName).length === $scope.blocks[blockName].permissions.length;
        };

        $scope.getCheckedPermissions = function (blockName) {
            return $scope.blocks[blockName].permissions.filter(function(permission) {
                return permission.value;
            })
        };

        $scope.setPermissionsValue = function (blockName, value) {
            $scope.blocks[blockName].permissions.forEach(function(permission) {
                permission.value = value;
            })
        };

        $scope.toggleAllPermissions = function(blockName) {
            var checkedPermissionsCount = $scope.getCheckedPermissions(blockName).length;
            if (checkedPermissionsCount === $scope.blocks[blockName].permissions.length) {
                $scope.setPermissionsValue(blockName, false);
            } else if (checkedPermissionsCount === 0 || checkedPermissionsCount > 0) {
                $scope.setPermissionsValue(blockName, true);
            }
        };

        $scope.isIndeterminateBlock = function(blockName) {
            var checkedPermissionsCount = $scope.getCheckedPermissions(blockName).length;
            return (checkedPermissionsCount !== 0 && checkedPermissionsCount !== $scope.blocks[blockName].permissions.length);
        };

        $scope.clearPermissions = function () {
            $scope.permissions.forEach(function(permission) {
                delete permission.value;
            })
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function(group) {
            $mdDialog.cancel(group);
        };
        (function initController() {
            $scope.getRoles();
            $scope.getAllPermissions();
        })();
    }
})();
