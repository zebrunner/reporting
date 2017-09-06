(function() {
 'use strict';

 angular
  .module('app.monitor')
  .controller('MonitorListController', ['$scope', '$rootScope', '$location', '$state', '$mdDialog', 'MonitorService', MonitorListController])

 // **************************************************************************
 function MonitorListController($scope, $rootScope, $location, $state, $mdDialog, MonitorService) {

  (function initController() {
   MonitorService.getAllMonitors()
    .then(function(rs) {
     if (rs.success) {
      $scope.monitors = rs.data;
     } else {
      alertify.error(rs.message);
     }
    });
  })();

  $scope.showCreateMonitorDialog = function(event) {
   $mdDialog.show({
     controller: function($scope, $mdDialog) {
      $scope.monitor = {};
      $scope.createMonitor = function() {
       MonitorService.createMonitor($scope.monitor).then(function(rs) {
        if (rs.success) {
         $scope.hide();
         alertify.success('Monitor created');
        } else {
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
     templateUrl: 'app/_monitors/create_modal.html',
     parent: angular.element(document.body),
     targetEvent: event,
     clickOutsideToClose: true,
     fullscreen: true
    })
    .then(function(answer) {
     if (answer) {
      $state.reload();
     }
    }, function() {});
  };



 $scope.deleteMonitor = function(monitor) {
       MonitorService.deleteMonitor(monitor.id).then(function(rs) {
        if (rs.success) {
         alertify.success('Monitor deleted');
         var index = $scope.monitors.indexOf(monitor);
        $scope.monitors.splice(index, 1);
        } else {
         alertify.error(rs.message);
        }
       });
      $scope.hide = function() {
       $mdDialog.hide(true);
      };
      $scope.cancel = function() {
       $mdDialog.cancel(false);
      };
  };




$scope.updateMonitor = function(monitor) {
       MonitorService.updateMonitor(monitor).then(function(rs) {
        if (rs.success) {
         alertify.success('Monitor with name:'+monitor.name+' is updated');
        } else {
         alertify.error(rs.message);
        }
       });
      $scope.hide = function() {
       $mdDialog.hide(true);
      };
      $scope.cancel = function() {
       $mdDialog.cancel(false);
      };
  };




$scope.checkAndTransformRecipient = function (currentUser) {
            var user = {};
            if (currentUser.username) {
                user = currentUser;
                $scope.email.recipients.push(user.email);
                $scope.users.push(user);
            } else {
                user.email = currentUser;
                $scope.email.recipients.push(user.email);
                $scope.users.push(user);
            }
            return user;
        };


$scope.email = {};
        $scope.email.subject = "Zafira Monitors";
        $scope.email.text = "This is auto-generated email, please do not reply!";
        $scope.email.hostname = document.location.hostname;
        $scope.email.urls = [document.location.href];
        $scope.email.recipients = [];
        $scope.users = [];


$scope.removeRecipient = function (user) {
            var index = $scope.email.recipients.indexOf(user.email);
            if (index >= 0) {
                $scope.email.recipients.splice(index, 1);
            }
        };



        $scope.checkAndTransformRecipient = function (currentUser) {
                    var user = {};
                    if (currentUser.username) {
                        user = currentUser;
                        $scope.email.recipients.push(user.email);
                        $scope.users.push(user);
                    } else {
                        user.email = currentUser;
                        $scope.email.recipients.push(user.email);
                        $scope.users.push(user);
                    }
                    return user;
                };




                function querySearch(criteria, user) {
                            $scope.usersSearchCriteria.email = criteria;
                            currentText = criteria;
                            if (!criteria.includes(stopCriteria)) {
                                stopCriteria = '########';
                                return UserService.searchUsersWithQuery($scope.usersSearchCriteria, criteria).then(function (rs) {
                                    if (rs.success) {
                                        if (! rs.data.results.length) {
                                            stopCriteria = criteria;
                                        }
                                        return rs.data.results.filter(searchFilter(user));
                                    }
                                    else {
                                    }
                                });
                            }
                            return "";
                        }



 }

})();