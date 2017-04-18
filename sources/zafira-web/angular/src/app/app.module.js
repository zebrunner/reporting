(function () {
    'use strict';

    angular.module('app', [
        // Core modules
         'app.core'
        // Custom Feature modules
        ,'app.chart'
        ,'app.ui'
        ,'app.ui.form'
        ,'app.ui.form.validation'
        ,'app.page'
        ,'app.table'
        ,'app.services'
        ,'app.auth'
        ,'app.user'
        // 3rd party feature modules
        ,'md.data.table'
    ])
    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
    ])
    .run(['$rootScope', '$location', '$cookies', '$http',
            function($rootScope, $location, $cookies, $http)
            {
		        	// keep user logged in after page refresh
		            $rootScope.globals = $cookies.getObject('globals') || {};
		            if ($rootScope.globals.auth) {
		            	$http.defaults.headers.common['Authorization'] = $rootScope.globals.auth.type + " " + $rootScope.globals.auth.accessToken;
		            }
		     
		            $rootScope.$on('$locationChangeStart', function (event, next, current) {
		                // redirect to login page if not logged in and trying to access a restricted page
		                var restrictedPage = $.inArray($location.path(), ['/signin']) === -1;
		                var loggedIn = $rootScope.globals.auth;
		                if (restrictedPage && !loggedIn) 
		                {
		                    $location.path('/signin');
		                }
		            });
            }
      ]);
})();