(function () {
    'use strict';

    angular.module('app')
        .controller('AppCtrl', [ '$scope', '$rootScope', '$state', '$cookies', '$document', '$http', 'appConfig', 'AuthService', 'UserService', 'DashboardService', 'SettingsService', 'ConfigService', 'AuthIntercepter', AppCtrl]); // overall control
	    function AppCtrl($scope, $rootScope, $state, $cookies, $document, $http, appConfig, AuthService, UserService, DashboardService, SettingsService, ConfigService, AuthIntercepter) {

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


	        $scope.initSession = function()
	        {
	        	UserService.getUserProfile().then(function (rs) {
                    if(rs.success)
                    {
                        $rootScope.currentUser = rs.data;
                        var userPreferences = $rootScope.currentUser.preferences;
		            	if (userPreferences.length !== 0) {
                            setDefaultPreferences(userPreferences);
		            	}
                        else {
                            DashboardService.getDefaultPreferences().then(function(rs){
                                if(rs.success)
                                {
                                    setDefaultPreferences(rs.data);
                                }

                            });
                        }
                    }
		       	});

                var setDefaultPreferences = function(userPreferences){
                    for (var i = 0; i < userPreferences.length; i++){
                        if (userPreferences[i].name === 'DEFAULT_DASHBOARD'){
                            $rootScope.defaultDashboard = userPreferences[i].value;
                        }
                        else if (userPreferences[i].name === 'REFRESH_INTERVAL'){
                            $rootScope.refreshInterval = userPreferences[i].value;
                        }
                    }
                };

		   		DashboardService.GetDashboardByTitle("User Performance").then(function(rs) {
	               if(rs.success)
	               {
	               		$rootScope.pefrDashboardId = rs.data.id;
	               }
		        });

		   		AuthService.GenerateAccessToken()
	        		.then(
		            function (rs) {
	            	if(rs.success)
	            	{
	            		$rootScope.accessToken = rs.data.token;
	            	}
	            });

	        	ConfigService.getConfig("version").then(function(rs) {
	                if(rs.success)
	                {
	                    $rootScope.version = rs.data;
	                }
	            });

                SettingsService.getSettingTools().then(function(rs) {
                    if(rs.success)
                    {
                        $rootScope.tools = rs.data;
                        $rootScope.$broadcast("event:settings-toolsInitialized", rs.data);
                    }
                });

                /*$rootScope.pushNotification = function (title, bodyText, timeout) {
                    Push.create(title, {
                        body: bodyText,
                        icon: 'favicon.ico',
                        timeout: timeout,
                        onClick: function () {
                            window.focus();
                            this.close();
                        }
                    });
                };*/
	        };

	        $rootScope.$on("$stateChangeSuccess", function (event, currentRoute, previousRoute) {
	            $document.scrollTo(0, 0);
	        });

	        $rootScope.$on("event:auth-loginSuccess", function(ev, auth){
	        	AuthService.SetCredentials(auth);
	        	$scope.initSession();
	        });


            $rootScope.$on('event:auth-loginRequired', function()
	        {
	        	if($cookies.get('Access-Token'))
	            {
	            	$rootScope.globals = { 'auth' : { 'refreshToken' : $cookies.get('Access-Token')}}
	            }

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

	        	// keep user logged in after page refresh
	            $rootScope.globals = $cookies.getObject('globals') || {};

	            if ($rootScope.globals.auth)
	            {
	            	$http.defaults.headers.common['Authorization'] = $rootScope.globals.auth.type + " " + $rootScope.globals.auth.accessToken;
	            }

	        	$scope.initSession();
	        })();
	    }
})();
