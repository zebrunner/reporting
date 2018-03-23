(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('DashboardService', ['$http', '$cookies', '$rootScope', '$location', 'UtilService', 'API_URL', DashboardService])

    function DashboardService($http, $cookies, $rootScope, $location, UtilService, API_URL) {

    	var service = {};

        service.GetDashboards = GetDashboards;
        service.GetDashboardByTitle = GetDashboardByTitle;
        service.CreateDashboard = CreateDashboard;
        service.UpdateDashboard = UpdateDashboard;
        service.DeleteDashboard = DeleteDashboard;
        service.CreateDashboardAttribute = CreateDashboardAttribute;
        service.CreateDashboardAttributes = CreateDashboardAttributes;
        service.UpdateDashboardAttribute = UpdateDashboardAttribute;
        service.DeleteDashboardAttribute = DeleteDashboardAttribute;
        service.GetDashboardById = GetDashboardById;
        service.AddDashboardWidget = AddDashboardWidget;
        service.UpdateDashboardWidget = UpdateDashboardWidget;
        service.UpdateDashboardWidgets = UpdateDashboardWidgets;
        service.DeleteDashboardWidget = DeleteDashboardWidget;
        service.SendDashboardByEmail = SendDashboardByEmail;
        service.GetWidgets = GetWidgets;
        service.CreateWidget = CreateWidget;
        service.UpdateWidget = UpdateWidget;
        service.DeleteWidget = DeleteWidget;
        service.ExecuteWidgetSQL = ExecuteWidgetSQL;

        return service;

        function GetDashboards(hidden) {
            var config = { params : {} };
            if(hidden)
                config.params.hidden = hidden;
         	return $http.get(API_URL + '/api/dashboards', config).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboards'));
        }

        function GetDashboardByTitle(title) {
            var config = { params : {} };
            if(title)
                config.params.title = title;
            return $http.get(API_URL + '/api/dashboards/title', config).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboards'));
        }

        function CreateDashboard(dashboard) {
            return $http.post(API_URL + '/api/dashboards', dashboard).then(UtilService.handleSuccess, UtilService.handleError('Unable to create dashboard'));
        }

        function UpdateDashboard(dashboard) {
            return $http.put(API_URL + '/api/dashboards', dashboard).then(UtilService.handleSuccess, UtilService.handleError('Unable to update dashboard'));
        }

        function DeleteDashboard(id) {
            return $http.delete(API_URL + '/api/dashboards/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete dashboard'));
        }

        function CreateDashboardAttribute(dashboardId, attribute) {
            return $http.post(API_URL + '/api/dashboards/' + dashboardId + '/attributes', attribute).then(UtilService.handleSuccess, UtilService.handleError('Unable to create dashboard attribute'));
        }

        function CreateDashboardAttributes(dashboardId, attributes) {
            return $http.post(API_URL + '/api/dashboards/' + dashboardId + '/attributes/queries', attributes).then(UtilService.handleSuccess, UtilService.handleError('Unable to create dashboard attributes'));
        }

        function UpdateDashboardAttribute(dashboardId, attribute) {
            return $http.put(API_URL + '/api/dashboards/' + dashboardId + '/attributes', attribute).then(UtilService.handleSuccess, UtilService.handleError('Unable to update dashboard attribute'));
        }

        function DeleteDashboardAttribute(dashboardId, attributeId) {
            return $http.delete(API_URL + '/api/dashboards/' + dashboardId + '/attributes/' + attributeId).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete dashboard attribute'));
        }

        function GetDashboardById(id) {
        	return $http.get(API_URL + '/api/dashboards/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboard'));
        }

        function AddDashboardWidget(dashboardId, widget) {
            return $http.post(API_URL + '/api/dashboards/' + dashboardId + '/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to add widget to dashboard'));
        }

        function UpdateDashboardWidget(dashboardId, widget) {
            return $http.put(API_URL + '/api/dashboards/' + dashboardId + '/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to update widget on dashboard'));
        }

        function UpdateDashboardWidgets(dashboardId, widgets) {
            return $http.put(API_URL + '/api/dashboards/' + dashboardId + '/widgets/all', widgets).then(UtilService.handleSuccess, UtilService.handleError('Unable to update widgets on dashboard'));
        }

        function DeleteDashboardWidget(dashboardId, widgetId) {
            return $http.delete(API_URL + '/api/dashboards/' + dashboardId + '/widgets/' + widgetId).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete widget from dashboard'));
        }

        function SendDashboardByEmail(email, projects) {
        	var config = {'headers' : { 'Access-Token' : $rootScope.globals.auth.accessToken } };
            return $http.post(API_URL + '/api/dashboards/email?projects=' + JSON.stringify(projects), email, config).then(UtilService.handleSuccess, UtilService.handleError('Unable to send dashboard by email'));
        }

        function GetWidgets() {
        	return $http.get(API_URL + '/api/widgets').then(UtilService.handleSuccess, UtilService.handleError('Unable to load widgets'));
        }

        function CreateWidget(widget) {
            return $http.post(API_URL + '/api/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to create widget'));
        }

        function UpdateWidget(widget) {
            return $http.put(API_URL + '/api/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to update widget'));
        }

        function DeleteWidget(id) {
            return $http.delete(API_URL + '/api/widgets/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete widget'));
        }

        function ExecuteWidgetSQL(params, sqlAdapter) {
        	return $http.post(API_URL + '/api/widgets/sql' + params, sqlAdapter).then(UtilService.handleSuccess, UtilService.handleError('Unable to exequte SQL'));
        }
    }
})();
