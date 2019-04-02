(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('DashboardService', ['$httpMock', '$cookies', '$rootScope', '$location', 'UtilService', '$httpParamSerializer', 'API_URL', DashboardService])

    function DashboardService($httpMock, $cookies, $rootScope, $location, UtilService, $httpParamSerializer, API_URL) {

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
        service.ExecuteWidgetTemplateSQL = ExecuteWidgetTemplateSQL;

        service.GetWidgetTemplates = GetWidgetTemplates;

        return service;

        function GetDashboards(hidden) {
            var config = { params : {} };
            if(hidden)
                config.params.hidden = hidden;
         	return $httpMock.get(API_URL + '/api/dashboards', config).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboards'));
        }

        function GetDashboardByTitle(title) {
            var config = { params : {} };
            if(title)
                config.params.title = title;
            return $httpMock.get(API_URL + '/api/dashboards/title', config).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboards'));
        }

        function CreateDashboard(dashboard) {
            return $httpMock.post(API_URL + '/api/dashboards', dashboard).then(UtilService.handleSuccess, UtilService.handleError('Unable to create dashboard'));
        }

        function UpdateDashboard(dashboard) {
            return $httpMock.put(API_URL + '/api/dashboards', dashboard).then(UtilService.handleSuccess, UtilService.handleError('Unable to update dashboard'));
        }

        function DeleteDashboard(id) {
            return $httpMock.delete(API_URL + '/api/dashboards/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete dashboard'));
        }

        function CreateDashboardAttribute(dashboardId, attribute) {
            return $httpMock.post(API_URL + '/api/dashboards/' + dashboardId + '/attributes', attribute).then(UtilService.handleSuccess, UtilService.handleError('Unable to create dashboard attribute'));
        }

        function CreateDashboardAttributes(dashboardId, attributes) {
            return $httpMock.post(API_URL + '/api/dashboards/' + dashboardId + '/attributes/queries', attributes).then(UtilService.handleSuccess, UtilService.handleError('Unable to create dashboard attributes'));
        }

        function UpdateDashboardAttribute(dashboardId, attribute) {
            return $httpMock.put(API_URL + '/api/dashboards/' + dashboardId + '/attributes', attribute).then(UtilService.handleSuccess, UtilService.handleError('Unable to update dashboard attribute'));
        }

        function DeleteDashboardAttribute(dashboardId, attributeId) {
            return $httpMock.delete(API_URL + '/api/dashboards/' + dashboardId + '/attributes/' + attributeId).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete dashboard attribute'));
        }

        function GetDashboardById(id) {
        	return $httpMock.get(API_URL + '/api/dashboards/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboard'));
        }

        function AddDashboardWidget(dashboardId, widget) {
            return $httpMock.post(API_URL + '/api/dashboards/' + dashboardId + '/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to add widget to dashboard'));
        }

        function UpdateDashboardWidget(dashboardId, widget) {
            return $httpMock.put(API_URL + '/api/dashboards/' + dashboardId + '/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to update widget on dashboard'));
        }

        function UpdateDashboardWidgets(dashboardId, widgets) {
            return $httpMock.put(API_URL + '/api/dashboards/' + dashboardId + '/widgets/all', widgets).then(UtilService.handleSuccess, UtilService.handleError('Unable to update widgets on dashboard'));
        }

        function DeleteDashboardWidget(dashboardId, widgetId) {
            return $httpMock.delete(API_URL + '/api/dashboards/' + dashboardId + '/widgets/' + widgetId).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete widget from dashboard'));
        }

        function SendDashboardByEmail(multipart, email) {
            var queryParams = $httpParamSerializer(email);
            return $httpMock.post(API_URL + '/api/upload/email?' + queryParams + '&file=', multipart, {headers: {'Content-Type': undefined}, transformRequest : angular.identity}).then(UtilService.handleSuccess, UtilService.handleError('Unable to send dashboard by email'));
        }

        function GetWidgets() {
        	return $httpMock.get(API_URL + '/api/widgets').then(UtilService.handleSuccess, UtilService.handleError('Unable to load widgets'));
        }

        function CreateWidget(widget) {
            return $httpMock.post(API_URL + '/api/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to create widget'));
        }

        function UpdateWidget(widget) {
            return $httpMock.put(API_URL + '/api/widgets', widget).then(UtilService.handleSuccess, UtilService.handleError('Unable to update widget'));
        }

        function DeleteWidget(id) {
            return $httpMock.delete(API_URL + '/api/widgets/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete widget'));
        }

        function ExecuteWidgetSQL(params, sqlAdapter) {
        	return $httpMock.post(API_URL + '/api/widgets/sql' + params, sqlAdapter).then(UtilService.handleSuccess, UtilService.handleError('Unable to execute SQL'));
        }

        function GetWidgetTemplates() {
            return $httpMock.get(API_URL + '/api/widgets/templates').then(UtilService.handleSuccess, UtilService.handleError('Unable to load widget templates'));
        };

        function ExecuteWidgetTemplateSQL(queryParams, sqlTemplateAdapter) {
            var url = UtilService.buildURL(API_URL + '/api/widgets/templates/sql', queryParams);
            return $httpMock.post(url, sqlTemplateAdapter).then(UtilService.handleSuccess, UtilService.handleError('Unable to execute SQL'));
        }
    }
})();
