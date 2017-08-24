(function () {
    'use strict';

    angular
        .module('app.dashboard')
        .controller('DashboardController', ['$scope', '$rootScope', '$cookies', '$location', '$state', '$http', '$mdConstant', '$stateParams', '$mdDialog', 'UtilService', 'DashboardService', 'UserService', 'AuthService', 'ProjectProvider', DashboardController])

    function DashboardController($scope, $rootScope, $cookies, $location, $state, $http, $mdConstant, $stateParams, $mdDialog, UtilService, DashboardService, UserService, AuthService, ProjectProvider) {

        $scope.dashboardId = null;
        $scope.currentUserId = $location.search().userId;

        $scope.dashboard = {};

        $scope.loadDashboardData = function (dashboard) {
            for (var i = 0; i < dashboard.widgets.length; i++) {
                if ('sql' != dashboard.widgets[i].type) {
                    $scope.loadWidget(dashboard.title, dashboard.widgets[i], dashboard.attributes);
                }
            }
        };

        $scope.loadWidget = function (dashboardName, widget, attributes) {
            var sqlAdapter = {'sql': widget.sql, 'attributes': attributes};
            var params = ProjectProvider.getProjectQueryParam();
            for(var i = 0; i<$scope.dashboard.attributes.length; i++){
                if ($scope.dashboard.attributes[i].key != null && $scope.dashboard.attributes[i].key == 'project'){
                    params = "?project=" + $scope.dashboard.attributes[i].value;
                }
    		}
            params = params != "" ? params + "&dashboardName=" + dashboardName : params + "?dashboardName=" + dashboardName;
            if ($scope.currentUserId) {
                params = params + "&currentUserId=" + $scope.currentUserId;
            }
            $scope.isLoading = true;
            DashboardService.ExecuteWidgetSQL(params, sqlAdapter).then(function (rs) {
                if (rs.success) {
                    var data = rs.data;
                    for (var j = 0; j < data.length; j++) {
                        if (data[j].CREATED_AT) {
                            data[j].CREATED_AT = new Date(data[j].CREATED_AT);
                        }
                    }

                    if ('sql' != widget.type) {
                        widget.model = JSON.parse(widget.model);
                        widget.data = {};
                        widget.data.dataset = data;
                    }
                    if (data.length != 0) {
                        $scope.isLoading = false;
                    }
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.asString = function (value) {
            if (value) {
                value = value.toString();
            }
            return value;
        };

        $scope.sort = {
            column: null,
            descending: false
        };

        $scope.changeSorting = function(column) {
            var specCharRegexp = /[-[\]{}()*+?.,\\^$|#\s%]/g;

            if (column.search(specCharRegexp) != -1) {
                // handle by quotes from both sides
                 column = "\"" + column + "\"";
             }
            var sort = $scope.sort;
            if (sort.column == column) {
                sort.descending = !sort.descending;
            } else {
                sort.column = column;
                sort.descending = false;
            }
        };

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
                    dashboardId: $scope.dashboardId
                }
            })
                .then(function (answer) {
                	if(answer == true) $state.reload();
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
                .then(function (answer) {
                	if(answer == true) $state.reload();
                }, function () {
                });
        };

        $scope.showWidgetDialog = function (event, widget, isNew) {
            $mdDialog.show({
                controller: WidgetController,
                templateUrl: 'app/_dashboards/widget_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true,
                locals: {
                    widget: widget,
                    isNew: isNew
                }
            })
                .then(function (answer) {
                	if(answer == true) $state.reload();
                }, function () {
                });
        };

        $scope.showEmailDialog = function (event) {
            $mdDialog.show({
                controller: EmailController,
                templateUrl: 'app/_dashboards/email_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                fullscreen: true
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

        (function init() {

        	var token = $cookies.get("Access-Token") ? $cookies.get("Access-Token") : $rootScope.globals.auth.refreshToken;

        	AuthService.RefreshToken(token)
    		.then(
            function (rs) {
            	if(rs.success)
            	{
            		AuthService.SetCredentials(rs.data);

            		DashboardService.GetDashboards().then(function (rs) {
                        if (rs.success) {
                            $scope.dashboardId = $stateParams.id ? $stateParams.id : rs.data[0].id;
                            DashboardService.GetDashboardById($scope.dashboardId).then(function (rs) {
                                if (rs.success) {
                                    $scope.dashboard = rs.data;
                                    var queryAttributes = getQueryAttributes();
                                    for(var i = 0; i < queryAttributes.length; i++) {
                                        $scope.dashboard.attributes.push(queryAttributes[i]);
                                    }
                                    $scope.loadDashboardData($scope.dashboard);
                                }
                            });
                        }
                    });

            		DashboardService.GetWidgets().then(function (rs) {
                        if (rs.success) {
                            $scope.widgets = rs.data;
                        } else {
                            alertify.error(rs.message);
                        }
                    });
            	}
            });
        })();
    }

    // **************************************************************************
    function DashboardWidgetController($scope, $mdDialog, DashboardService, widget, dashboardId, isNew) {

        $scope.isNew = isNew;
        $scope.widget = widget;

        if (isNew) {
            $scope.widget.position = 0;
            $scope.widget.size = 4;
        }

        $scope.addDashboardWidget = function (widget) {
            DashboardService.AddDashboardWidget(dashboardId, widget).then(function (rs) {
                if (rs.success) {
                	alertify.success("Widget added");
                	$scope.hide(true);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.deleteDashboardWidget = function (widget) {
            DashboardService.DeleteDashboardWidget(dashboardId, widget.id).then(function (rs) {
                if (rs.success) {
                	alertify.success("Widget deleted");
                	$scope.hide(true);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.updateDashboardWidget = function (widget) {
            DashboardService.UpdateDashboardWidget(dashboardId, {
                "id": widget.id,
                "size": widget.size,
                "position": widget.position
            }).then(function (rs) {
                if (rs.success) {
                	alertify.success("Widget updated");
                	$scope.hide(true);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.hide = function (result) {
            $mdDialog.hide(result);
        };
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
        (function initController() {
        })();
    }

    function DashboardSettingsController($scope, $mdDialog, $location, DashboardService, dashboard, isNew) {

        $scope.isNew = isNew;
        $scope.dashboard = dashboard;
        $scope.newAttribute = {};

        if($scope.isNew)
        {
            $scope.dashboard.hidden = false;
        }

        $scope.createDashboard = function(dashboard){
            DashboardService.CreateDashboard(dashboard).then(function (rs) {
                if (rs.success) {
                	alertify.success("Dashboard created");
                	$scope.hide(true);
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
                	$scope.hide(true);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.deleteDashboard = function(dashboard){
            DashboardService.DeleteDashboard(dashboard.id).then(function (rs) {
                if (rs.success)
                {
                	alertify.success("Dashboard deleted");
                    var mainDashboard = $location.$$absUrl.substring(0, $location.$$absUrl.lastIndexOf('/'));
                    window.open(mainDashboard, '_self');
                }
                else {
                    alertify.error(rs.message);
                }
            });
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

        $scope.hide = function (result) {
            $mdDialog.hide(result);
        };
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
        (function initController() {
        })();
    }

    function WidgetController($scope, $mdDialog, DashboardService, widget, isNew) {

        $scope.isNew = isNew;
        $scope.widget = widget;
        if($scope.isNew && $scope.widget)
        {
            $scope.widget.id = null;
        }

        $scope.createWidget = function(widget){
            DashboardService.CreateWidget(widget).then(function (rs) {
                if (rs.success) {
                	alertify.success("Widget created");
                	$scope.hide(true);
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
                	$scope.hide(true);
                }
                else {
                    alertify.error(rs.message);
                }
            });
            $scope.hide(success);
        };

        $scope.deleteWidget = function(widget){
            DashboardService.DeleteWidget(widget.id).then(function (rs) {
                if (rs.success) {
                	alertify.success("Widget deleted");
                	$scope.hide(true);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.hide = function (result) {
            $mdDialog.hide(result);
        };
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
        (function initController() {
        })();
    }

    function EmailController($scope, $rootScope, $mdDialog, $mdConstant, DashboardService, UserService) {

        $scope.title = "Zafira Dashboard";
        $scope.subjectRequired = true;
        $scope.textRequired = true;

        $scope.email = {};
        $scope.email.subject = "Zafira Dashboards";
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
