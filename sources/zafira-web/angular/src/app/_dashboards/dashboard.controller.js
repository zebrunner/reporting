(function () {
    'use strict';
 
    angular
        .module('app.dashboard')
        .controller('DashboardController', ['$scope', '$location', '$stateParams', 'UtilService', 'DashboardService', DashboardController])
 
    function DashboardController($scope, $location, $stateParams, UtilService, DashboardService) {
 
    	$scope.dashboardId = $location.search().id;
		$scope.currentUserId = $location.search().userId;
	
		$scope.loadDashboardData = function(dashboard) {
			for(var i = 0; i < dashboard.widgets.length; i++)
			{
				if('sql' != dashboard.widgets[i].type)
				{
					$scope.loadWidget(dashboard.title, dashboard.widgets[i], dashboard.attributes);
				}
			}
		};
	
		$scope.loadWidget = function(dashboardName, widget, attributes) {
			var sqlAdapter = {'sql' : widget.sql, 'attributes' : attributes};
//			var params = ProjectProvider.getProjectQueryParam();
			var params = "?dashboardName=" + dashboardName;
			if($scope.currentUserId != null)
			{
				params = params + "&currentUserId=" + $scope.currentUserId;
			}
			DashboardService.ExecuteWidgetSQL(params, sqlAdapter).then(function(rs) {
                if(rs.success)
                {
                	var data = rs.data;
                	for(var j = 0; j < data.length; j++)
    				{
    					if(data[j].CREATED_AT)
    					{
    						data[j].CREATED_AT = new Date(data[j].CREATED_AT);
    					}
    				}
    	
    				if('sql' != widget.type)
    				{
    					widget.model = JSON.parse(widget.model);
    					widget.data = {};
    					widget.data.dataset = data;
    				}
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
		};
		
		$scope.asString = function(value) {
			if(value != null)
			{
				value = value.toString();
			}
			return value;
		};
		
		$scope.sort = {
	        column: null,
	        descending: false
	    };

	    $scope.changeSorting = function(column) {
	        var sort = $scope.sort;
	        if (sort.column == column) {
	            sort.descending = !sort.descending;
	        } else {
	            sort.column = column;
	            sort.descending = false;
	        }
	    };
	
		(function init(){
			DashboardService.GetDashboardById($stateParams.id).then(function(rs) {
                if(rs.success)
                {
                	$scope.dashboard = rs.data;
                	$scope.loadDashboardData($scope.dashboard);
                }
                else
                {
                    alertify.error(rs.message);
                }
            });
		})();
    }
 
})();