const dashboardSettingsModalController = function dashboardSettingsModalController($scope, $mdDialog, $location, DashboardService, dashboard, isNew) {
    'ngInject';

    $scope.isNew = isNew;
    $scope.dashboard = angular.copy(dashboard);
    $scope.newAttribute = {};

    if($scope.isNew)
    {
        $scope.dashboard.hidden = false;
    }

    $scope.createDashboard = function(dashboard){
        DashboardService.CreateDashboard(dashboard).then(function (rs) {
            if (rs.success) {
                alertify.success("Dashboard created");
                $scope.hide(rs.data, 'CREATE');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.updateDashboard = function(dashboard){
        dashboard.widgets = null;
        DashboardService.UpdateDashboard(dashboard).then(function (rs) {
            if (rs.success) {
                alertify.success("Dashboard updated");
                $scope.hide(rs.data, 'UPDATE');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.deleteDashboard = function(dashboard){
        var confirmedDelete = confirm('Would you like to delete dashboard "' + dashboard.title + '"?');
        if (confirmedDelete) {
            DashboardService.DeleteDashboard(dashboard.id).then(function (rs) {
                if (rs.success) {
                    alertify.success("Dashboard deleted");
                    var mainDashboard = $location.$$absUrl.substring(0, $location.$$absUrl.lastIndexOf('/'));
                    window.open(mainDashboard, '_self');
                }
                else {
                    alertify.error(rs.message);
                }
            });
        }
        $scope.hide();
    };

     // Dashboard attributes
    $scope.createAttribute = function(attribute){
        DashboardService.CreateDashboardAttribute(dashboard.id, attribute).then(function (rs) {
            if (rs.success) {
                $scope.dashboard.attributes = rs.data;
                $scope.newAttribute = {};
                alertify.success('Dashboard attribute created');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.updateAttribute = function(attribute){
        DashboardService.UpdateDashboardAttribute(dashboard.id, attribute).then(function (rs) {
            if (rs.success) {
                $scope.dashboard.attributes = rs.data;
                alertify.success('Dashboard attribute updated');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.deleteAttribute = function(attribute){
        DashboardService.DeleteDashboardAttribute(dashboard.id, attribute.id).then(function (rs) {
            if (rs.success) {
                $scope.dashboard.attributes = rs.data;
                alertify.success('Dashboard attribute removed');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.hide = function (result, action) {
        if(result) {
            result.action = action;
        }
        $mdDialog.hide(result);
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    (function initController(dashboard) {
    })();
};

export default dashboardSettingsModalController;
