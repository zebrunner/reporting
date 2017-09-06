(function() {
 'use strict';

 angular
  .module('app.monitor')
  .controller('MonitorListController', ['$scope', '$rootScope', '$location', '$state','$mdConstant','$mdDialog', 'MonitorService', MonitorListController])

 // **************************************************************************
 function MonitorListController($scope, $rootScope, $location, $state, $$mdConstant, $mdDialog, MonitorService) {

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
     controller: MonitorDialogController,
     templateUrl: 'app/_monitors/create_modal.html',
     parent: angular.element(document.body),
     targetEvent: event,
     clickOutsideToClose: true,
     fullscreen: true,
     scope: $scope
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

 }






 function MonitorDialogController($scope, $rootScope, $mdDialog, $mdConstant, DashboardService, MonitorService) {



         $scope.email = {};
         $scope.email.recipients = [];
         $scope.users = [];
         $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SEMICOLON, $mdConstant.KEY_CODE.SPACE];

         var currentText;




               $scope.monitor = {};
               $scope.createMonitor = function() {
                MonitorService.createMonitor($scope.monitor).then(function(rs) {
                 if (rs.success) {
                 $scope.monitors.push(rs.data);
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




         $scope.sendEmail = function () {
             if (! $scope.users.length) {
                 if (currentText && currentText.length) {
                     $scope.email.recipients.push(currentText);
                 } else {
                     alertify.error('Add a recipient!');
                     return;
                 }
             }
             $scope.hide();
             $scope.email.recipients = $scope.email.recipients.toString();
             DashboardService.SendDashboardByEmail($scope.email).then(function (rs) {
                 if (rs.success) {
                     alertify.success('Email was successfully sent!');
                 }
                 else {
                     alertify.error(rs.message);
                 }
             });
         };

         $scope.users_all = [];

         $scope.usersSearchCriteria = {};
         $scope.asyncContacts = [];
         $scope.filterSelected = true;

         $scope.querySearch = querySearch;
         var stopCriteria = '########';

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

         function searchFilter(u) {
             return function filterFn(user) {
                 var users = u;
                 for(var i = 0; i < users.length; i++) {
                     if(users[i].id == user.id) {
                         return false;
                     }
                 }
                 return true;
             };
         }

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

         $scope.removeRecipient = function (user) {
             var index = $scope.email.recipients.indexOf(user.email);
             if (index >= 0) {
                 $scope.email.recipients.splice(index, 1);
             }
         };

         $scope.hide = function () {
             $mdDialog.hide();
         };
         $scope.cancel = function () {
             $mdDialog.cancel();
         };
         (function initController() {
         })();
     }

})();