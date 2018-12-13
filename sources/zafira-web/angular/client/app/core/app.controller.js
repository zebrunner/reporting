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

	        $rootScope.$on("event:auth-loginSuccess", function(ev, payload){
                AuthService.SetCredentials(payload.auth);
                $scope.initSession();
                UserService.initCurrentUser()
                    .then(function(user) {
                        var bufferedRequests = httpBuffer.getBuffer();

                        $scope.main.skin = user.theme;
                        if (bufferedRequests && bufferedRequests.length) {
                            $window.location.href = bufferedRequests[0].location;
                        } else {
                            if (payload.referrer) {
                               $state.go(payload.referrer);
                            } else {
                                $state.go('dashboard', {id: user.id});
                            }
                        }
                    });
	        });

            $rootScope.$on('event:auth-loginRequired', function() {
		        	if ($rootScope.globals.auth && $rootScope.globals.auth.refreshToken) {
	                    AuthService.RefreshToken($rootScope.globals.auth.refreshToken)
	                        .then(function (rs) {
	                            if (rs.success) {
	                                AuthService.SetCredentials(rs.data);
	                                AuthIntercepter.loginConfirmed();
	                            } else if ($state.current.name !== 'signup') {
	                                AuthIntercepter.loginCancelled();
	                                AuthService.ClearCredentials();
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
                    var currentUser;

                    $scope.initSession();

                    currentUser = UserService.getCurrentUser();
                    if (!currentUser) {
                        UserService.initCurrentUser()
                            .then(function (user) {
                                $scope.main.skin = user.theme;
                            });
                    } else {
                        $scope.main.skin = currentUser.theme;
                    }
	            }
                 getVersion();
	        })();
	    }
})();
