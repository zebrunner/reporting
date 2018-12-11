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

            $rootScope.footerSlice = {
                robo: {
                    url: 'app/_auth/robo-footer.html'
                },
                copyright: {
                    url: 'app/_auth/copyright-footer.html'
                }
            };

            var UNANIMATED_STATES = ['signin', 'signup', 'forgotPassword', 'resetPassword'];

            $scope.isAnimated = function() {
                return UNANIMATED_STATES.indexOf($state.current.name) == -1;
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

            $rootScope.$on("$stateChangeStart", function (event, currentRoute, previousRoute) {
                var loginRequired = currentRoute.data && currentRoute.data.requireLogin;

                //Redirect to login page if authorization is required and user is not authorized
                if (loginRequired && !AuthService.IsLoggedIn()) {
                    event.preventDefault();
                    $state.go('signin', {referrer: currentRoute.name});
                }
            });

            $rootScope.$on("$stateChangeSuccess", function (event, currentRoute, previousRoute) {
	            $document.scrollTo(0, 0);
	        });

	        $rootScope.$on("event:auth-loginSuccess", function(ev, payload){
                AuthService.SetCredentials(payload.auth);
                $scope.initSession();
                $scope.initExtendedUserProfile().then(function(rs) {
                    var bufferedRequests = httpBuffer.getBuffer();

                    if (bufferedRequests && bufferedRequests.length) {
                        $window.location.href = bufferedRequests[0].location;
                    } else {
                        if (payload.referrer) {
                           $state.go(payload.referrer);
                        } else {
                            $state.go('dashboard', {id: rs});
                        }
                    }
                }, function (rs) {
                });
	        });

            $rootScope.$on('event:auth-loginRequired', function() {
                // TODO: @Boniara: is this correct or old code?
                if ($cookies.get('Access-Token')) {
	            	$rootScope.globals = { 'auth' : { 'refreshToken' : $cookies.get('Access-Token')}};
	            }

	        	if ($rootScope.globals.auth && $rootScope.globals.auth.refreshToken) {
                    AuthService.RefreshToken($rootScope.globals.auth.refreshToken)
                        .then(function (rs) {
                            if (rs.success) {
                                AuthService.SetCredentials(rs.data);
                                AuthIntercepter.loginConfirmed();
                            } else if ($state.current.name !== 'signup') {
                                AuthIntercepter.loginCancelled();
                                $state.go("signin", {referrer: $state.current.name});
                            }
                        });
	        	} else if ($state.current.name !== 'signup') {
                    $state.go('signin', {referrer: $state.current.name});
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
	            //TODO: @Boniara: do we need this code right below and is it correct (using 'Access-Token')?
	            // Used for dashboard emails
	            var authorization = $cookies.get('Access-Token');
                if(authorization) {
                    AuthService.SetCredentials({'accessToken': authorization, 'type': 'Bearer'});
                }

                SettingsService.getCompanyLogo()
                    .then(function(rs) {
                        if (rs.success) {
                            if (!$rootScope.companyLogo.value || $rootScope.companyLogo.value !== rs.data) {
                                $rootScope.companyLogo.value = rs.data.value;
                                $rootScope.companyLogo.id = rs.data.id;
                                SettingProvider.setCompanyLogoURL($rootScope.companyLogo.value);
                            }
                        }
                    });
                $rootScope.globals = $rootScope.globals && $rootScope.globals.auth ? $rootScope.globals : $cookies.getObject('globals') || {};
	            if ($rootScope.globals.auth) {
                    $scope.initSession();
                    $scope.initExtendedUserProfile()
                    .then(function (rs) {
                        if ($state.current.name === 'dashboards') {
                            $state.go('dashboard', {id: rs});
                        }
                    });
	            }
                 getVersion();
	        })();
	    }
})();
