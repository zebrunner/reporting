(function () {
    'use strict';

    angular.module('app')
        .controller('AppCtrl', [ '$scope', '$rootScope', '$state', '$window', '$cookies', '$document', '$http', 'appConfig', 'AuthService', 'UserService', 'DashboardService', 'SettingsService', 'ConfigService', 'AuthIntercepter', 'UtilService', AppCtrl]); // overall control
	    function AppCtrl($scope, $rootScope, $state, $window, $cookies, $document, $http, appConfig, AuthService, UserService, DashboardService, SettingsService, ConfigService, AuthIntercepter, UtilService) {

	        $scope.pageTransitionOpts = appConfig.pageTransitionOpts;
	        $scope.main = appConfig.main;
	        $scope.color = appConfig.color;
	        $rootScope.darkThemes = ['11', '21', '31', '22'];
	        $rootScope.currentOffset = 0;

	        // ************** Integrations **************

	        $rootScope.jenkins  = { enabled : false };
	        $rootScope.jira     = { enabled : false };
	        $rootScope.rabbitmq = { enabled : false };

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

            $scope.setOffset = function (event) {
	              $rootScope.currentOffset = 0;
	              var bottomHeight = $window.innerHeight - event.target.clientHeight - event.clientY;
	              if(bottomHeight < 400) {
	                  $rootScope.currentOffset = -250 + bottomHeight;
	              }
            };

	        $scope.initSession = function()
	        {
	            $scope.loadCompanyLogo();

                $scope.initUserProfile();

		   		DashboardService.GetDashboardByTitle("User Performance").then(function(rs) {
	               if(rs.success)
	               {
	               		$rootScope.pefrDashboardId = rs.data.id;
	               }
		        });

		   		AuthService.GenerateAccessToken().then(function (rs) {
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
                        $rootScope.tools = {};
                        rs.data.forEach(function(tool) {
                            $rootScope.tools[tool] = false;
                            SettingsService.isToolConnected(tool).then(function(rs) {
                                if(rs.success)
                                {
                                    $rootScope.tools[tool] = rs.data;
                                    $rootScope.$broadcast("event:settings-toolsInitialized", tool);
                                }
                            });
                        });
                    }
                });

	        };

	        $rootScope.$on('event:settings-toolsInitialized', function (event, data) {

	        		if(data == "RABBITMQ")
	        		{
	        			SettingsService.getSettingByTool("RABBITMQ").then(function(rs) {
	        	            var settings = UtilService.settingsAsMap(rs.data);
	        	            $rootScope.rabbitmq.enabled = settings["RABBITMQ_ENABLED"];
	        	            $rootScope.rabbitmq.user = settings["RABBITMQ_USER"];
	        	            $rootScope.rabbitmq.pass = settings["RABBITMQ_PASSWORD"];
	        	            $rootScope.rabbitmq.ws = settings["RABBITMQ_WS"];
	        	        });
	        		}

	        		if(data == "JIRA")
	        		{
	        			SettingsService.getSettingByTool("JIRA").then(function(rs) {
	        	            var settings = UtilService.settingsAsMap(rs.data);
	        	            $rootScope.jira.enabled = settings["JIRA_ENABLED"];
	        	            $rootScope.jira.url = settings["JIRA_URL"];
	        	        });
	        		}

	        		if(data == "JENKINS")
	        		{
	        			SettingsService.getSettingByTool("JENKINS").then(function(rs) {
	        	            var settings = UtilService.settingsAsMap(rs.data);
	        	            $rootScope.jenkins.enabled = settings["JENKINS_ENABLED"];
	        	            $rootScope.jenkins.url = settings["JENKINS_URL"];
	        	        });
	        		}
            });

            $scope.loadCompanyLogo = function () {
                SettingsService.getSettingValueByName('COMPANY_LOGO_URL').then(function(rs) {
                    if(rs.success)
                    {
                        $rootScope.companyLogo = rs.data;
                    }
                    else
                    {
                    }
                });
            };

            $scope.initUserProfile = function (){
                UserService.getUserProfile().then(function (rs) {
                    if(rs.success)
                    {
                        $rootScope.currentUser = rs.data;
                        $rootScope.isAdmin = rs.data.roles.indexOf('ROLE_ADMIN') >= 0;
                        var userPreferences = $rootScope.currentUser.preferences;
                        if (userPreferences && userPreferences.length !=0) {
                            $scope.setDefaultPreferences(userPreferences);
                        }
                        else {
                            UserService.getDefaultPreferences().then(function(rs){
                                if(rs.success)
                                {
                                    $scope.setDefaultPreferences(rs.data);
                                }
                             });
                        }
                    }
                });
             };

            $scope.setDefaultPreferences = function(userPreferences){
                for (var i = 0; i < userPreferences.length; i++){
                    if (userPreferences[i].name === 'DEFAULT_DASHBOARD'){
                        $rootScope.defaultDashboard = userPreferences[i].value;
                    }
                    else if (userPreferences[i].name === 'REFRESH_INTERVAL'){
                        $rootScope.refreshInterval = userPreferences[i].value;
                    }
                    else if (userPreferences[i].name === 'THEME'){
                        $scope.main.skin = userPreferences[i].value;
                    }
                }
                $rootScope.$broadcast("event:defaultPreferencesInitialized");
            };

            $rootScope.$on("$stateChangeSuccess", function (event, currentRoute, previousRoute) {
	            $document.scrollTo(0, 0);
	        });

	        $rootScope.$on("event:auth-loginSuccess", function(ev, auth){
	        	AuthService.SetCredentials(auth);
	        	$scope.initSession();
	        });

            $rootScope.$on("event:preferencesReset", function () {
                $scope.initUserProfile();
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
