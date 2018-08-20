(function () {
    'use strict';

    angular.module('app')
        .controller('AppCtrl', [ '$scope', '$rootScope', '$templateCache', '$state', 'httpBuffer', '$location', '$window', '$cookies', '$document', '$http', '$q', 'appConfig', 'AuthService', 'UserService', 'DashboardService', 'SettingsService', 'ConfigService', 'AuthIntercepter', 'UtilService', 'ElasticsearchService', 'SettingProvider', AppCtrl]); // overall control
	    function AppCtrl($scope, $rootScope, $templateCache, $state, httpBuffer, $location, $window, $cookies, $document, $http, $q, appConfig, AuthService, UserService, DashboardService, SettingsService, ConfigService, AuthIntercepter, UtilService, ElasticsearchService, SettingProvider) {

	        $scope.pageTransitionOpts = appConfig.pageTransitionOpts;
	        $scope.main = appConfig.main;
	        $scope.color = appConfig.color;
	        $rootScope.darkThemes = ['11', '21', '31', '22'];
	        $rootScope.currentOffset = 0;
            $rootScope.companyLogo = {
                name: 'COMPANY_LOGO_URL',
                value: SettingProvider.getCompanyLogoURl() || ''
            };

	        // ************** Integrations **************

	        $rootScope.jenkins  = { enabled : false };
	        $rootScope.jira     = { enabled : false };
	        $rootScope.rabbitmq = { enabled : false };
	        $rootScope.google = { enabled : false };

            $scope.setOffset = function (event) {
	              $rootScope.currentOffset = 0;
	              var bottomHeight = $window.innerHeight - event.target.clientHeight - event.clientY;
	              if(bottomHeight < 400) {
	                  $rootScope.currentOffset = -250 + bottomHeight;
	              }
            };

	        $scope.initSession = function()
	        {

                SettingsService.getSettingTools().then(function(rs) {
                    if(rs.success)
                    {
                        $rootScope.tools = {};
                        rs.data.forEach(function(tool) {
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

	        $scope.initExtendedUserProfile = function () {
                return $q(function(resolve, reject) {
                    UserService.getExtendedUserProfile().then(function(rs) {
                        if(rs.success)
                        {
                            $rootScope.currentUser = rs.data["user"];
                            $rootScope.currentUser.isAdmin = $rootScope.currentUser.roles.indexOf('ROLE_ADMIN') >= 0;
                            $rootScope.setDefaultPreferences($rootScope.currentUser.preferences);
                            $rootScope.currentUser.defaultDashboardId= rs.data["defaultDashboardId"];
                            if($rootScope.currentUser.defaultDashboardId === null) {
                                alertify.warning("Default Dashboard is unavailable!");
                            }
                            $rootScope.currentUser.pefrDashboardId = rs.data["performanceDashboardId"];
                            if($rootScope.currentUser.pefrDashboardId === null) {
                                alertify.error("'User Performance' dashboard is unavailable!");
                            }
                            $rootScope.currentUser.personalDashboardId = rs.data["personalDashboardId"];
                            if($rootScope.currentUser.personalDashboardId === null) {
                                alertify.error("'Personal' dashboard is unavailable!");
                            }
                            $rootScope.currentUser.stabilityDashboardId = rs.data["stabilityDashboardId"];
                            resolve(rs.data['defaultDashboardId']);
                        } else {
                            reject(rs);
                        }
                    });
                })
            };

	        $rootScope.$on('event:settings-toolsInitialized', function (event, data) {

	            switch(data) {
                    case "RABBITMQ":
                        SettingsService.getSettingByTool("RABBITMQ").then(function(rs) {
                            var settings = UtilService.settingsAsMap(rs.data);
                            $rootScope.rabbitmq.enabled = settings["RABBITMQ_ENABLED"];
                            $rootScope.rabbitmq.user = settings["RABBITMQ_USER"];
                            $rootScope.rabbitmq.pass = settings["RABBITMQ_PASSWORD"];
                        });
                        break;
                    case "JIRA":
                        SettingsService.getSettingByTool("JIRA").then(function(rs) {
                            var settings = UtilService.settingsAsMap(rs.data);
                            $rootScope.jira.enabled = settings["JIRA_ENABLED"];
                            $rootScope.jira.url = settings["JIRA_URL"];
                        });
                        break;
                    case "JENKINS":
                        SettingsService.getSettingByTool("JENKINS").then(function(rs) {
                            var settings = UtilService.settingsAsMap(rs.data);
                            $rootScope.jenkins.enabled = settings["JENKINS_ENABLED"];
                            $rootScope.jenkins.url = settings["JENKINS_URL"];
                        });
                        break;
                    case "GOOGLE":
                        SettingsService.getSettingByTool("GOOGLE").then(function(rs) {
                            var settings = UtilService.settingsAsMap(rs.data);
                            $rootScope.google.enabled = settings["GOOGLE_ENABLED"];
                        });
                        break;
                    default:
                        break;
                }
            });

            $rootScope.setDefaultPreferences = function(userPreferences){
                userPreferences.forEach(function(userPreference) {
                    switch(userPreference.name) {
                        case 'DEFAULT_DASHBOARD':
                            $rootScope.currentUser.defaultDashboard = userPreference.value;
                            break;
                        case 'REFRESH_INTERVAL':
                            $rootScope.currentUser.refreshInterval = userPreference.value;
                            break;
                        case 'THEME':
                            $rootScope.currentUser.theme = userPreference.value;
                            $scope.main.skin = userPreference.value;
                            break;
                        default:
                            break;
                    }
                });
            };

            $rootScope.$on("$stateChangeSuccess", function (event, currentRoute, previousRoute) {
	            $document.scrollTo(0, 0);
	        });

	        $rootScope.$on("event:auth-loginSuccess", function(ev, auth){
	        		AuthService.SetCredentials(auth);
                $scope.initSession();
                $scope.initExtendedUserProfile().then(function(rs) {
                    var bufferedRequests = httpBuffer.getBuffer();
                    if(bufferedRequests && bufferedRequests.length) {
                        $window.location.href = bufferedRequests[0].location;
                    } else {
                        $state.go('dashboard', {id: rs});
                    }
                }, function (rs) {
                })
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

            function getVersion() {
                return $q(function (resolve, reject) {
                    ConfigService.getConfig("version").then(function(rs) {
                        if(rs.success)
                        {
                            $rootScope.version = rs.data;
                            resolve(rs.data);
                        } else {
                            reject(rs.message);
                        }
                    });
                });
            };

            function clearCache(version) {
                var v = $cookies.get('version');
                if(v !== version) {
                    $cookies.put('version', version);
                    $templateCache.removeAll();
                }
            };

	        (function initController() {

	        	// keep user logged in after page refresh
//	              var authorization = $cookies.get('Access-Token');
//                if(authorization) {
//                    AuthService.SetCredentials({'accessToken': authorization, 'type': 'Bearer'});
//                }

                $rootScope.globals = $rootScope.globals && $rootScope.globals.auth ? $rootScope.globals : $cookies.getObject('globals') || {};
	            if ($rootScope.globals.auth)
	            {
	            	//$http.defaults.headers.common['Authorization'] = $rootScope.globals.auth.type + " " + $rootScope.globals.auth.accessToken;
                    $scope.initSession();
                    $scope.initExtendedUserProfile().then(function (rs) {
                        if(['dashboards'].indexOf($state.current.name) >= 0) {
                            $state.go('dashboard', {id: rs});
                        }
                    });
	            }

                SettingsService.getCompanyLogo().then(function(rs) {
                    if(rs.success)
                    {
                        if(! $rootScope.companyLogo.value || $rootScope.companyLogo.value != rs.data) {
                            $rootScope.companyLogo.value = rs.data;
                            SettingProvider.setCompanyLogoURL($rootScope.companyLogo.value);
                        }
                    }
                });

                 getVersion().then(function (rs) {
                     //clearCache(rs.service);
                 });
	        })();
	    }
})();
