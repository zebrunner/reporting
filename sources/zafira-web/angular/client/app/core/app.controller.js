(function () {
    'use strict';

    angular.module('app')
        .controller('AppCtrl', [ '$scope', '$rootScope', '$state', '$document', 'appConfig', 'AuthService', 'AuthIntercepter', AppCtrl]); // overall control
	    function AppCtrl($scope, $rootScope, $state, $document, appConfig, AuthService, AuthIntercepter) {
	
	        $scope.pageTransitionOpts = appConfig.pageTransitionOpts;
	        $scope.main = appConfig.main;
	        $scope.color = appConfig.color;
	
	        $scope.$watch('main', function(newVal, oldVal) {
	            // if (newVal.menu !== oldVal.menu || newVal.layout !== oldVal.layout) {
	            //     $rootScope.$broadcast('layout:changed');
	            // }
	
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
	        
	        $rootScope.$on('event:auth-loginRequired', function() 
	        {
	        	if($rootScope.globals.auth != null && $rootScope.globals.auth.refreshToken != null)
	        	{
	        		AuthService.RefreshToken($rootScope.globals.auth.refreshToken)
	        		.then(
		            function (rs) {
		            	if(rs.success)
		            	{
		            		AuthService.SetCredentials(rs.data);
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
	    }

})(); 