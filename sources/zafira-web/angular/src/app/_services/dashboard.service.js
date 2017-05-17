(function () {
    'use strict';
 
    angular
        .module('app.services')
        .factory('DashboardService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', DashboardService])
 
    function DashboardService($http, $cookies, $rootScope, UtilService, API_URL) {
        
    	var service = {};
 
        service.GetDashboards = GetDashboards;
        service.GetDashboardById = GetDashboardById;
        service.GetWidgets = GetWidgets;
        service.ExecuteWidgetSQL = ExecuteWidgetSQL;
 
        return service;
 
        function GetDashboards(userId) {
        	return $http.get(API_URL + '/api/dashboards' + (userId != null ? '?userId=' + userId : '')).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboards'));
        }
        
        function GetDashboardById(id) {
        	return $http.get(API_URL + '/api/dashboards/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to load dashboard'));
        }
        
        function GetWidgets() {
        	return $http.get(API_URL + '/api/widgets').then(UtilService.handleSuccess, UtilService.handleError('Unable to load widgets'));
        }
        
        function ExecuteWidgetSQL(params, sqlAdapter) {
        	return $http.post(API_URL + '/api/widgets/sql' + params, sqlAdapter).then(UtilService.handleSuccess, UtilService.handleError('Unable to exequte SQL'));
        }
 
    }
})();