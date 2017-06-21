(function () {
    'use strict';

    angular.module('app')
        .controller('AppCtrl', [ '$scope', '$rootScope', '$state', '$cookies', '$document', 'appConfig', 'AuthService', 'UserService', 'DashboardService', 'ConfigService', 'AuthIntercepter', AppCtrl]); // overall control
	    function AppCtrl($scope, $rootScope, $state, $cookies, $document, appConfig, AuthService, UserService, DashboardService, ConfigService, AuthIntercepter) {
	
	        $scope.pageTransitionOpts = appConfig.pageTransitionOpts;
	        $scope.main = appConfig.main;
	        $scope.color = appConfig.color;
	
	        $scope.$watch('main', function(newVal, oldVal) {
	        	
	            if (newVal.menu === 'horizontal' && oldVal.menu === 'vertical') {
	                $rootScope.$broadcast('nav:reset');
	            }
	            if (newVal.fixedHeader === false && newVal.fixedSidebar === true) {
	                if (oldVal.fixedHeader === false && oldVal.fixedSidebar === false) {
	                    $scope.main.fixedHeader = true;
	                    $scope.main.fixedSidebar = true;
	                }
	                if (oldVal.fixedHeader === true && oldVal.fixedSidebar === true) {
	                    $scope.main.fixedHeader = false;
	                    $scope.main.fixedSidebar = false;
	                }
	            }
	            if (newVal.fixedSidebar === true) {
	                $scope.main.fixedHeader = true;
	            }
	            if (newVal.fixedHeader === false) {
	                $scope.main.fixedSidebar = false;
	            }
	        }, true);
	
	
	        $rootScope.$on("$stateChangeSuccess", function (event, currentRoute, previousRoute) {
	            $document.scrollTo(0, 0);
	        });
	        
	        $rootScope.$on("event:auth-loginSuccess", function(ev, auth){
	        	AuthService.SetCredentials(auth);
	        	$scope.initCommonData();
	        });
	        
	        $scope.initCommonData = function()
	        {
	        	if(AuthService.IsLoggedIn())
	        	{
	        		UserService.getUserProfile()
		        		 .then(
		        		  function (rs) {
			              if(rs.success)
			              {
			            	  $rootScope.currentUser = rs.data;
			            	  $cookies.putObject('currentUser', $rootScope.currentUser);
			              }
		       		});
	        		
	        		DashboardService.GetDashboards("USER_PERFORMANCE").then(function(rs) {
		                if(rs.success && rs.data.length > 0)
		                {
		                	$rootScope.pefrDashboardId = rs.data[0].id;
		                }
		            });
	        	}
	        	
	        	ConfigService.getConfig("version").then(function(rs) {
	                if(rs.success)
	                {
	                    $rootScope.version = rs.data;
	                }
	            });
	        };
	        
	        
	        $rootScope.$on('event:auth-loginRequired', function() 
	        {
	        	if($rootScope.globals.auth != null && $rootScope.globals.auth.refreshToken != null)
	        	{
	        		AuthService.RefreshToken($rootScope.globals.auth.refreshToken)
	        		.then(
		            function (rs) {
		            	if(rs.success)
		            	{
		            		$rootScope.$broadcast('event:auth-loginSuccess', rs.data);
		            		AuthIntercepter.loginConfirmed();
		            	}
		            	else
		            	{
		            		$state.go("signin");
		            		AuthIntercepter.loginCancelled();
		            	}
		            });
	        	}
	        	else
	        	{
	        		$state.go("signin");
	        	}
	        });
	        
	        (function initController() {
	        	$scope.initCommonData();
	        })();
	    }

})(); 