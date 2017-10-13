(function () {
    'use strict';

    angular
        .module('app.monitors')
        .controller('MonitorsController', ['$scope', '$rootScope', '$state', '$mdConstant', '$stateParams', '$mdDialog', 'MonitorsService', 'UserService', MonitorsController])

    function MonitorsController($scope, $rootScope, $state, $mdConstant, $stateParams, $mdDialog, MonitorsService, UserService) {

        $scope.monitors = [];

        $scope.TYPES = {
            HTTP : 'HTTP',
            PING : 'PING'
        };

        $scope.HTTP_METHODS = {
            POST : 'POST',
            GET : 'GET',
            PUT : 'PUT'
        };

        $scope.blockView = true;

        $scope.switchViewType = function () {
            $scope.blockView = !$scope.blockView;
        };

        $scope.getAllMonitors = function () {
            MonitorsService.getAllMonitors().then(function (rs) {
                if(rs.success)
                {
                    $scope.monitors = rs.data;
                    $scope.initChips();
                }
            })
        };

        $scope.updateMonitor = function (monitor, switchJob) {
            monitor.recipients = monitor.emailList.toString();
            MonitorsService.updateMonitor(monitor, switchJob).then(function (rs) {
                if(rs.success)
                {
                    if(switchJob)
                    {
                        var status = rs.data.monitorEnabled ? 'ran': 'stopped';
                        alertify.success("Monitor was " + status);
                    } else {
                        alertify.success('Monitor was updated');
                    }
                }
            })
        };

        $scope.deleteMonitor = function (id) {
            MonitorsService.deleteMonitor(id).then(function (rs) {
                if(rs.success)
                {
                    $scope.monitors = $scope.monitors.filter(function (monitor) {
                        if(monitor.id === id) {
                            return false;
                        }
                        return true;
                    });
                    alertify.success('Monitor was deleted');
                }
            })
        };

        $scope.isHttpBlockPresent = function (monitor) {
            return monitor.type === $scope.TYPES['HTTP'];
        };

        $scope.isHttpBodyBlockPresent = function (monitor) {
            return monitor.type === $scope.TYPES['HTTP'] && ( monitor.httpMethod === $scope.HTTP_METHODS['POST'] || monitor.httpMethod === $scope.HTTP_METHODS['PUT']);
        };

        $scope.usersSearchCriteria = {};
        $scope.asyncContacts = [];
        $scope.filterSelected = true;

        $scope.email = {};
        $scope.email.recipients = [];
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SEMICOLON, $mdConstant.KEY_CODE.SPACE];

        var currentText;

        $scope.querySearch = querySearch;
        var stopCriteria = '########';

        function querySearch(criteria, monitor) {
            $scope.usersSearchCriteria.email = criteria;
            currentText = criteria;
            if (!criteria.includes(stopCriteria)) {
                stopCriteria = '########';
                return UserService.searchUsersWithQuery($scope.usersSearchCriteria, criteria).then(function (rs) {
                    if (rs.success) {
                        if (! rs.data.results.length) {
                            stopCriteria = criteria;
                        }
                        return rs.data.results.filter(searchFilter(monitor.emailList));
                    }
                    else {
                    }
                });
            }
            return "";
        }

        function searchFilter(recipients) {
            return function filterFn(user) {
                if(recipients) {
                    var users = recipients;
                    for (var i = 0; i < users.length; i++) {
                        if (users[i] === user.email) {
                            return false;
                        }
                    }
                }
                return true;
            };
        }

        $scope.checkAndTransformRecipient = function (currentUser, monitor) {
            var user = {};
            if (currentUser.username) {
                user = currentUser.email;
            } else {
                user = currentUser;
            }
            monitor.emailList.push(user);
            /*monitor.users.push(user);*/
            return user;
        };

        $scope.removeRecipient = function (email, monitor) {
            var index = monitor.emailList.indexOf(email);
            if (index >= 0) {
                monitor.emailList.splice(index, 1);
            }
        };

        $scope.initChips = function () {
            for(var i = 0; i < $scope.monitors.length; i++) {
                if($scope.monitors[i].recipients.length !== 0) {
                    $scope.monitors[i].emailList = $scope.monitors[i].recipients.split(',');
                } else {
                    $scope.monitors[i].emailList = [];
                }
            }
        };

        $scope.openMonitorDialog = function ($event, monitor) {
            $mdDialog.show({
                controller: function ($scope, $mdDialog, monitor) {

                    if(!monitor) {
                        $scope.monitor = {};
                        $scope.monitor.emailList = [];
                        $scope.monitor.type = $scope.TYPES['HTTP'];
                        $scope.monitor.httpMethod = $scope.HTTP_METHODS['GET'];
                    } else {
                        $scope.monitor = angular.copy(monitor);
                    }

                    $scope.createMonitor = function () {
                        $scope.monitor.recipients = $scope.monitor.emailList.toString();
                        MonitorsService.createMonitor($scope.monitor).then(function (rs) {
                            if(rs.success)
                            {
                                if(rs.data.recipients.length !== 0) {
                                    rs.data.emailList = rs.data.recipients.split(',');
                                } else {
                                    rs.data.emailList = [];
                                }
                                $scope.monitors.push(rs.data);
                                alertify.success('Monitor was created successfully');
                                $scope.hide();
                            }
                        })
                    };

                    $scope.hide = function() {
                        $mdDialog.hide(true);
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel(false);
                    };
                },
                templateUrl: 'app/_monitors/monitors_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                scope: $scope.$new(),
                preserveScope: true,
                locals: {
                    monitor: monitor
                }
            }).then(function(answer) {
                    if(answer)
                    {
                        $state.reload();
                    }
                }, function() {
                });
        };

        (function init(){
            $scope.getAllMonitors();
        })();
    }
})();
