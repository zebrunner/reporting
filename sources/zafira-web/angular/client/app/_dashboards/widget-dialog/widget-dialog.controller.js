const widgetDialogController = function widgetDialogController($scope, $rootScope, $mdDialog, DashboardService, ProjectProvider, widget, isNew, dashboard, currentUserId) {
    'ngInject';

    $scope.widget = {};
    $scope.dashboard = {};
    $scope.isNew = angular.copy(isNew);
    angular.copy(widget, $scope.widget);
    angular.copy(dashboard, $scope.dashboard);
    $scope.showWidget = false;

    $scope.isJson = function(json) {
        return typeof(json) === 'object';
    };

    if ($scope.isJson($scope.widget.model)){
        $scope.widget.model = JSON.stringify($scope.widget.model, null, 4);
    }

    if ($scope.isJson($scope.widget.location)) {
        $scope.widget.location = JSON.stringify($scope.widget.location, null, 4);
    }

    if($scope.isNew && $scope.widget)
    {
        $scope.widget.id = null;
    }

    $scope.createWidget = function(widget){
        DashboardService.CreateWidget(widget).then(function (rs) {
            if (rs.success) {
                alertify.success("Widget created");
                $scope.hide(rs.data, 'CREATE');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.updateWidget = function(widget){
        DashboardService.UpdateWidget(widget).then(function (rs) {
            if (rs.success) {
                alertify.success("Widget updated");
                $rootScope.$broadcast("$event:widgetIsUpdated");
                $scope.hide(rs.data, 'UPDATE');
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    $scope.$on("$event:executeSQL", function () {
        if (widget.sql){
            $scope.loadModalWidget($scope.widget, $scope.dashboard.attributes, true);
        }
        else {
            alertify.warning('Add SQL query');
        }
    });

    $scope.$on("$event:showWidget", function () {
        if (widget.sql){
            if(widget.type){
                $scope.loadModalWidget($scope.widget, $scope.dashboard.attributes);
            }
            else {
                alertify.warning('Choose widget type');
            }
         }
        else {
            alertify.warning('Add SQL query');
        }
    });

    $scope.loadModalWidget = function (widget, attributes, table) {

        $scope.isLoading = true;
        var sqlAdapter = {'sql': widget.sql, 'attributes': attributes};
        var params = setQueryParams(table);
        DashboardService.ExecuteWidgetSQL(params, sqlAdapter).then(function (rs) {
            if (rs.success) {
                var data = rs.data;
                var columns = {};
                for (var j = 0; j < data.length; j++) {
                    if(data[j] !== null) {
                        if (j === 0) {
                            columns = Object.keys(data[j]);
                        }
                        if (data[j].CREATED_AT) {
                            data[j].CREATED_AT = new Date(data[j].CREATED_AT);
                        }
                    }
                }
                if (table){
                    widget.executeType = 'table';
                    widget.testModel = {"columns" : columns};
                }
                else {
                    widget.executeType = widget.type;
                    widget.testModel = JSON.parse(widget.model);
                }
                widget.data = {};
                widget.data.dataset = data;
                $scope.isLoading = false;
                $scope.showWidget = true;
            }
            else {
                alertify.error(rs.message);
            }
        });
    };

    var setQueryParams = function(table){
        var params = ProjectProvider.getProjectsQueryParam();
        for(var i = 0; i < $scope.dashboard.attributes.length; i++){
            if ($scope.dashboard.attributes[i].key !== null && $scope.dashboard.attributes[i].key === 'project'){
                params = "?projects=" + $scope.dashboard.attributes[i].value;
            }
        }
        params = params !== "" ? params + "&dashboardName=" + $scope.dashboard.title : params + "?dashboardName=" + $scope.dashboard.title;
        if (currentUserId) {
            params = params + "&currentUserId=" + currentUserId;
        }
        if (table) {
            params = params + "&stackTraceRequired=" + true;
        }
        return params;
    };

    $scope.sort = {
        column: null,
        descending: false
    };

    $scope.changeSorting = function(column) {
        var specCharRegexp = /[-[\]{}()*+?.,\\^$|#\s%]/g;

        if (column.search(specCharRegexp) != -1) {
            column.replace("\"\"", "\"");
         }
        var sort = $scope.sort;
        if (sort.column == column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    $scope.asString = function (value) {
        if (value) {
            value = value.toString();
        }
        return value;
    };

    $scope.closeWidget = function(){
        $scope.widget.data.dataset = [];
        $scope.widget.executeType = null;
        $scope.showWidget = false;
    };

    $scope.hide = function (rs, action) {
        rs.action = action;
        $mdDialog.hide(rs);
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };

     (function initController() {
    })();
};

export default widgetDialogController;
