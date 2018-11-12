(function () {
    'use strict';

    angular
        .module('app.dashboard')
        .controller('DashboardController', ['$scope', '$rootScope', '$q', '$timeout', '$interval', '$cookies', '$location', '$state', '$http', '$mdConstant', '$stateParams', '$mdDialog', '$mdToast', 'UtilService', 'DashboardService', 'UserService', 'AuthService', 'ProjectProvider', DashboardController])

    function DashboardController($scope, $rootScope, $q, $timeout, $interval, $cookies, $location, $state, $http, $mdConstant, $stateParams, $mdDialog, $mdToast, UtilService, DashboardService, UserService, AuthService, ProjectProvider) {

        $scope.currentUserId = $location.search().userId;

        $scope.pristineWidgets = [];

        $scope.unexistWidgets = [];

        $scope.dashboard = {};

        $scope.gridstackOptions = {
            disableDrag: true,
            disableResize: true,
            verticalMargin: 20,
            resizable: {
                handles: 'se, sw'
            },
            cellHeight: 20
        };

        $scope.isJson = function(json) {
            return typeof(json) === 'object';
        };

        $scope.startEditWidgets = function () {
            angular.element('.grid-stack').gridstack($scope.gridstackOptions).data('gridstack').enable();
            showGridActionToast();
        };

        var defaultWidgetLocation = '{ "x":0, "y":0, "width":4, "height":11 }';

        function loadDashboardData (dashboard, refresh) {
            for (var i = 0; i < dashboard.widgets.length; i++) {
                var currentWidget = dashboard.widgets[i];
                currentWidget.location = jsonSafeParse(currentWidget.location);
                if (!refresh || refresh && currentWidget.refreshable) {
                    loadWidget(dashboard.title, currentWidget, dashboard.attributes, refresh);
                }
            }
            angular.copy(dashboard.widgets, $scope.pristineWidgets);
        };

        function loadWidget (dashboardName, widget, attributes, refresh) {
            var sqlAdapter = {'sql': widget.sql, 'attributes': attributes};
            if(!refresh){
                $scope.isLoading = true;
            }
            var params = setQueryParams(dashboardName);
            DashboardService.ExecuteWidgetSQL(params, sqlAdapter).then(function (rs) {
                if (rs.success) {
                    var data = rs.data;
                    for (var j = 0; j < data.length; j++) {
                        if (data[j] !== null && data[j].CREATED_AT) {
                            data[j].CREATED_AT = new Date(data[j].CREATED_AT);
                        }
                    }
                    if(!refresh){
                        widget.model = jsonSafeParse(widget.model);
                    }
                    widget.data = {};
                    widget.data.dataset = data;
                    if (widget.title.toUpperCase().includes("CRON")) {
                        addOnClickConfirm();
                    }
                    if (data.length !== 0) {
                        $scope.isLoading = false;
                    }
                }
                else {
                    alertify.error(rs.message);
                }
            });
        }

        function getNextEmptyGridArea(defaultLocation) {
            var gridstack = angular.element('.grid-stack').gridstack($scope.gridstackOptions).data('gridstack');
            var location = jsonSafeParse(defaultLocation);
            while(! gridstack.isAreaEmpty(location.x, location.y, location.width, location.height)) {
                location.y = location.y + 11;
                if(location.y > 1100)
                    break;
            }
            return jsonSafeStringify(location);
        }

        $scope.addDashboardWidget = function (widget) {
            widget.location = getNextEmptyGridArea(defaultWidgetLocation);
            var data = {"id": widget.id, "location": widget.location};
            DashboardService.AddDashboardWidget($stateParams.id, data).then(function (rs) {
                if (rs.success) {
                    $scope.dashboard.widgets.push(widget);
                    $scope.dashboard.widgets.forEach(function (widget) {
                        widget.location = jsonSafeStringify(widget.location);
                    });
                    loadDashboardData($scope.dashboard, false);
                    alertify.success("Widget added");
                    updateWidgetsToAdd();
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.deleteDashboardWidget = function (widget) {
            var confirmedDelete = confirm('Would you like to delete widget "' + widget.title + '" from dashboard?');
            if (confirmedDelete) {
                DashboardService.DeleteDashboardWidget($stateParams.id, widget.id).then(function (rs) {
                    if (rs.success) {
                        $scope.dashboard.widgets.splice($scope.dashboard.widgets.indexOf(widget), 1);
                        $scope.dashboard.widgets.forEach(function (widget) {
                            widget.location = jsonSafeStringify(widget.location);
                        });
                        loadDashboardData($scope.dashboard, false);
                        alertify.success("Widget deleted");
                        updateWidgetsToAdd();
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
            }
        };

        var isJSON = function (json) {
            try {
                JSON.parse(json);
                return false;
            } catch (e) {
                return true;
            }
        };

        function updateWidgetsToAdd () {
            $timeout(function () {
                if($scope.widgets && $scope.dashboard.widgets)
                $scope.unexistWidgets =  $scope.widgets.filter(function(widget) {
                    var existingWidget = $scope.dashboard.widgets.filter(function(w) {
                        return w.id == widget.id;
                    });
                    return !existingWidget.length || widget.id != existingWidget[0].id;
                });
            }, 800);
        };

        $scope.resetGrid = function () {
            var gridstack = angular.element('.grid-stack').gridstack($scope.gridstackOptions).data('gridstack');
            //gridstack.batchUpdate();
            $scope.pristineWidgets.forEach(function (widget) {
                var currentWidget = $scope.dashboard.widgets.filter(function(w) {
                    return widget.id === w.id;
                })[0];
                if(currentWidget) {
                    widget.location = jsonSafeParse(widget.location);
                    currentWidget.location.x = widget.location.x;
                    currentWidget.location.y = widget.location.y;
                    currentWidget.location.height = widget.location.height;
                    currentWidget.location.width = widget.location.width;
                    var element = angular.element('#widget-' + currentWidget.id);
                    gridstack.update(element, widget.location.x, widget.location.y,
                        widget.location.width, widget.location.height);
                }
            });
            gridstack.disable();
            //gridstack.commit();
        };

        function showGridActionToast() {
            $mdToast.show({
                hideDelay: 0,
                position: 'bottom right',
                scope: $scope,
                preserveScope: true,
                controller  : function ($scope, $mdToast) {
                    $scope.updateWidgetsPosition = function(){
                        var widgets = [];
                        for(var i = 0; i < $scope.dashboard.widgets.length; i++) {
                            var currentWidget = $scope.dashboard.widgets[i];
                            var widgetData = {};
                            angular.copy(currentWidget, widgetData);
                            widgetData.location = JSON.stringify(widgetData.location);
                            widgets.push({'id': currentWidget.id, 'location': widgetData.location});
                        }
                        DashboardService.UpdateDashboardWidgets($stateParams.id, widgets).then(function (rs) {
                            if (rs.success) {
                                angular.copy(rs.data, $scope.pristineWidgets);
                                $scope.resetGrid();
                                $scope.closeToast();
                                alertify.success("Widget positions were updated");
                            }
                            else {
                                alertify.error(rs.message);
                            }
                        });
                    };

                    $scope.closeToast = function() {
                        $mdToast
                            .hide()
                            .then(function() {
                            });
                    };
                },
                templateUrl : 'app/_dashboards/widget-placement_toast.html'
            });
        };

        var setQueryParams = function(dashboardName){
            var params = ProjectProvider.getProjectsQueryParam();
            for(var i = 0; i<$scope.dashboard.attributes.length; i++){
                if ($scope.dashboard.attributes[i].key != null && $scope.dashboard.attributes[i].key == 'project'){
                    params = "?projects=" + $scope.dashboard.attributes[i].value;
                }
            }
            params = params != "" ? params + "&dashboardName=" + dashboardName : params + "?dashboardName=" + dashboardName;
            if ($scope.currentUserId) {
                params = params + "&currentUserId=" + $scope.currentUserId;
            }
            return params;
        };

        $scope.asString = function (value) {
            if (value) {
                value = value.toString();
            }
            return value;
        };

        $scope.isFormatted = function (string) {
            var pattern = /^<.+>.*<\/.+>$/g;
            return pattern.test(string);
        };

        function jsonSafeParse (preparedJson) {
            if(!isJSON(preparedJson)) {
                return JSON.parse(preparedJson);
            }
            return preparedJson;
        };

        function jsonSafeStringify (preparedJson) {
            if(isJSON(preparedJson)) {
                return JSON.stringify(preparedJson);
            }
            return preparedJson;
        };

        $scope.sort = {
            column: null,
            descending: false
        };

        $scope.deleteWidget = function($event, widget){
            var confirmedDelete = confirm('Would you like to delete widget "' + widget.title + '" ?');
            if (confirmedDelete) {
                DashboardService.DeleteWidget(widget.id).then(function (rs) {
                    if (rs.success) {
                        $scope.widgets.splice($scope.widgets.indexOfId(widget.id), 1);
                        if($scope.dashboard.widgets.indexOfId(widget.id) >= 0) {
                            $scope.dashboard.widgets.splice($scope.dashboard.widgets.indexOfId(widget.id), 1);
                        }
                        updateWidgetsToAdd();
                        alertify.success("Widget deleted");
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
            }
        };

        $scope.changeSorting = function(widget, column) {
            var specCharRegexp = /[-[\]{}()*+?.,\\^$|#\s%]/g;

            if (column.search(specCharRegexp) != -1) {
                column.replace("\"\"", "\"");
             }
             if(! widget.sort) {
                 widget.sort = {};
                 angular.copy($scope.sort, widget.sort);
             }
            if (widget.sort.column == column) {
                widget.sort.descending = !widget.sort.descending;
            } else {
                widget.sort.column = column;
                widget.sort.descending = false;
            }
        };

        /*$scope.deleteDashboard = function(dashboard){
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
        };*/

        $scope.showDashboardWidgetDialog = function (event, widget, isNew) {
            $mdDialog.show({
                controller: DashboardWidgetController,
                templateUrl: 'app/_dashboards/dashboard_widget_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    widget: widget,
                    isNew: isNew,
                    dashboardId: $stateParams.id
                }
            })
                .then(function (rs, action) {
                }, function () {
                });
        };

        $scope.showDashboardDialog = function (event, dashboard, isNew) {
            $mdDialog.show({
                controller: DashboardSettingsController,
                templateUrl: 'app/_dashboards/dashboard_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    dashboard: dashboard,
                    isNew: isNew
                }
            })
                .then(function (rs) {
                	if(rs) {
                        switch(rs.action) {
                            case 'CREATE':
                                $state.go('dashboard', {id: rs.id});
                                $rootScope.dashboardList.splice(rs.position, 0, rs);
                                break;
                            case 'UPDATE':
                                rs.widgets = $scope.dashboard.widgets;
                                $scope.dashboard = angular.copy(rs);
                                $rootScope.dashboardList.splice(rs.position, 1, rs);
                                break;
                            default:
                                break;
                        }
                        delete rs.action;
                    }
                }, function () {
                });
        };

        $scope.showWidgetDialog = function (event, widget, isNew, dashboard) {
            $mdDialog.show({
                controller: WidgetController,
                templateUrl: 'app/_dashboards/widget_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    widget: widget,
                    isNew: isNew,
                    dashboard: dashboard,
                    currentUserId: $scope.currentUserId
                }
            })
                .then(function (rs) {
                	if(rs) {
                        switch(rs.action) {
                            case 'CREATE':
                                $scope.widgets.push(rs);
                                updateWidgetsToAdd();
                                break;
                            case 'UPDATE':
                                $scope.widgets.splice($scope.widgets.indexOfId(rs.id), 1, rs);
                                updateWidgetsToAdd();
                                break;
                            default:
                                break;
                        }
                        delete rs.action;
                    }
                }, function () {
                });
        };

        $scope.showEmailDialog = function (event, widgetId) {
            $mdDialog.show({
                controller: EmailController,
                templateUrl: 'app/_dashboards/email_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    widgetId: widgetId
                }
            })
                .then(function (answer) {
                }, function () {
                });
        };

        var toAttributes = function (qParams) {
            var attributes = [];
            for(var param in qParams) {
                var currentAttribute = {};
                currentAttribute.key = param;
                currentAttribute.value = qParams[param];
                attributes.push(currentAttribute);
            }
            return attributes;
        };

        var getQueryAttributes = function () {
            var qParams = $location.search();
            var qParamsLength = Object.keys(qParams).length;
            if(qParamsLength > 0 && $stateParams.id) {
                return toAttributes(qParams);
            }
        };

        $scope.getDataWithAttributes = function (dashboard, refresh) {
            var queryAttributes = getQueryAttributes();
            if(queryAttributes) {
                for (var i = 0; i < queryAttributes.length; i++) {
                    dashboard.attributes.push(queryAttributes[i]);
                }
            }
            loadDashboardData(dashboard, refresh);
        };

        $scope.optimizeWidget = function (widget, index) {
            if (widget.type == 'table' && (Object.size(widget.data.dataset) == 0 || Object.size(widget.data.dataset) == index + 1)) {
                $timeout(function () {
                    var gridstack = angular.element('.grid-stack').gridstack($scope.gridstackOptions).data('gridstack');
                    $scope.gridstackOptions.disableResize = false;
                    var el = angular.element('#' + widget.id)[0];
                    var gridstackEl = angular.element('#widget-' + widget.id)[0];
                    if(Object.size(widget.data.dataset) == 0) {
                        gridstack.resize(gridstackEl, widget.location.width, (Math.ceil(el.offsetHeight / $scope.gridstackOptions.cellHeight / 2)) + 2);
                    } else {
                        gridstack.resize(gridstackEl, widget.location.width, (Math.ceil(el.offsetHeight / $scope.gridstackOptions.cellHeight / 2)) + 2);
                    }
                    $scope.gridstackOptions.disableResize = true;
                }, 100);
            }
        };


        var refreshIntervalInterval;

        function refresh() {
            if($scope.dashboard.title && $rootScope.currentUser.refreshInterval && $rootScope.currentUser.refreshInterval != 0) {
                refreshIntervalInterval = $interval(function () {
                    loadDashboardData($scope.dashboard, true);
                }, $rootScope.currentUser.refreshInterval);
            }
        };

        $scope.stopRefreshIntervalInterval = function() {
            if (angular.isDefined(refreshIntervalInterval)) {
                $interval.cancel(refreshIntervalInterval);
                refreshIntervalInterval = undefined;
            }
        };

        $scope.$on('$destroy', function() {
            $scope.stopRefreshIntervalInterval();
        });

        function getDashboardById (dashboardId) {
            return $q(function (resolve, reject) {
                DashboardService.GetDashboardById(dashboardId).then(function (rs) {
                    if (rs.success) {
                        $scope.dashboard = rs.data;
                        $scope.getDataWithAttributes($scope.dashboard, false);
                        resolve(rs.data);
                    } else {
                        reject(rs.message);
                    }
                });
            });
        }

        $scope.$watch(
            function() {
                if ($scope.currentUserId && $location.$$search.userId){
                    return $scope.currentUserId !== $location.$$search.userId;
                }
            },
            function() {
                if ($scope.currentUserId && $location.$$search.userId) {
                    if ($scope.currentUserId !== $location.$$search.userId) {
                        $scope.currentUserId = $location.search().userId;
                        getDashboardById($stateParams.id);
                    }
                }
            }
        );

        $scope.$on("$event:widgetIsUpdated", function () {
            getDashboardById($stateParams.id);
        });

        $scope.$on('$destroy', function () {
            $scope.resetGrid();
        });

        function addOnClickConfirm() {
            $scope.$watch(function () {
                return angular.element('#cron_rerun').is(':visible')
            }, function () {
                var rerunAllLinks = document.getElementsByClassName("cron_rerun_all");
                Array.prototype.forEach.call(rerunAllLinks, function(link) {
                    link.addEventListener("click", function (event) {
                        if (!confirm('Rebuild for all tests in cron job will be started. Continue?')) {
                            event.preventDefault();
                        }
                    }, false);
                });
                var rerunFailuresLinks = document.getElementsByClassName("cron_rerun_failures");
                Array.prototype.forEach.call(rerunFailuresLinks, function(link) {
                    link.addEventListener("click", function (event) {
                        if (!confirm('Rebuild for failures in cron job will be started. Continue?')) {
                            event.preventDefault();
                        }
                    }, false);
                });
            });
        }

        var defaultDashboardWatcher = $scope.$watch('currentUser.defaultDashboard', function (newVal) {
            if(newVal) {
                if ($rootScope.currentUser.isAdmin)
                    DashboardService.GetWidgets().then(function (rs) {
                        if (rs.success) {
                            $scope.widgets = rs.data;
                            updateWidgetsToAdd();
                        } else {
                            alertify.error(rs.message);
                        }
                    });
                defaultDashboardWatcher();
            }
        });

        (function init() {

            if(!$stateParams.id && $rootScope.currentUser && $rootScope.currentUser.defaultDashboardId) {
                $state.go('dashboard', {id: $rootScope.currentUser.defaultDashboardId})
            }
            getDashboardById($stateParams.id).then(function (rs) {
                $timeout(function () {
                    refresh();
                }, 0, false);
            }, function () {
            });
        })();
    }

    // **************************************************************************
    function DashboardWidgetController($scope, $mdDialog, DashboardService, widget, dashboardId, isNew) {

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
    }

    function DashboardSettingsController($scope, $mdDialog, $location, DashboardService, dashboard, isNew) {

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
    }

    function WidgetController($scope, $rootScope, $mdDialog, DashboardService, ProjectProvider, widget, isNew, dashboard, currentUserId) {
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
    }

    function EmailController($scope, $rootScope, $mdDialog, $mdConstant, DashboardService, UserService, ProjectProvider, widgetId) {

        var TYPE = widgetId ? 'WIDGET' : 'DASHBOARD';

        var CURRENT_DASHBOARD_TITLE = angular.element('#dashboard_title')[0].value + ' dashboard';
        var CURRENT_WIDGET_TITLE = TYPE == 'WIDGET' ? CURRENT_DASHBOARD_TITLE + ' - ' + angular.element('#widget-title-' + widgetId)[0].value + ' widget' : '';

        var EMAIL_TYPES = {
            'DASHBOARD': {
                title: CURRENT_DASHBOARD_TITLE,
                subject: CURRENT_DASHBOARD_TITLE,
                func: sendDashboardEmail
            },
            'WIDGET': {
                title: CURRENT_WIDGET_TITLE,
                subject: CURRENT_WIDGET_TITLE,
                func: sendWidgetEmail
            }
        };

        $scope.title = EMAIL_TYPES[TYPE].title;
        $scope.subjectRequired = true;
        $scope.textRequired = true;

        $scope.email = {};
        $scope.email.subject = EMAIL_TYPES[TYPE].subject;
        $scope.email.text = "This is auto-generated email, please do not reply!";
        $scope.email.hostname = document.location.hostname;
        $scope.email.urls = [document.location.href];
        $scope.email.recipients = [];
        $scope.users = [];
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SEMICOLON, $mdConstant.KEY_CODE.SPACE];

        var currentText;

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
            var projects = ProjectProvider.getProjects();
            projects = projects && projects.length ? projects : null;
            EMAIL_TYPES[TYPE].func(projects);
        };

        function sendDashboardEmail(projects) {
            DashboardService.SendDashboardByEmail($scope.email, projects).then(function (rs) {
                if (rs.success) {
                    alertify.success('Email was successfully sent!');
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        function sendWidgetEmail(projects) {
            DashboardService.SendWidgetByEmail($scope.email, projects, widgetId).then(function (rs) {
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
