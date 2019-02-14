const dashboardWidgetModalController = function dashboardWidgetModalController($scope, $mdDialog, DashboardService, widget, dashboardId, isNew) {
    'ngInject';

    $scope.isNew = isNew;
    $scope.widget = widget;

    if (isNew) {
        $scope.widget.location = 0;
        $scope.widget.size = 4;
    }

    $scope.addDashboardWidget = function (widget) {
        DashboardService.AddDashboardWidget(dashboardId, widget).then(function (rs) {
            if (rs.success) {
                alertify.success("Widget added");
                $scope.hide(rs.data, 'CREATE');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.deleteDashboardWidget = function (widget) {
        var confirmedDelete = confirm('Would you like to delete widget "' + widget.title + '" from dashboard?');
        if (confirmedDelete) {
            DashboardService.DeleteDashboardWidget(dashboardId, widget.id).then(function (rs) {
                if (rs.success) {
                    alertify.success("Widget deleted");
                    $scope.hide(rs.data, 'DELETE');
                }
                else {
                    alertify.error(rs.message);
                }
            });
        }
     };

    $scope.updateDashboardWidget = function (widget) {
        DashboardService.UpdateDashboardWidget(dashboardId, {
            "id": widget.id,
            "size": widget.size,
            "position": widget.location
        }).then(function (rs) {
            if (rs.success) {
                alertify.success("Widget updated");
                $scope.hide(rs.data, 'UPDATE');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.hide = function (rs, action) {
        $mdDialog.hide(rs, action);
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    (function initController() {
    })();
};

export default dashboardWidgetModalController;
