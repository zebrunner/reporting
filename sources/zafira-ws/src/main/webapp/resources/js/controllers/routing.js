ZafiraApp.constant('ROUTES', [  
{
	url : '/dashboards',
	config : {
		templateUrl : 'dashboards'
	}
},
{
	url : '/certification',
	config : {
		templateUrl : 'certification'
	}
},
{
	url : '/users',
	config : {
		templateUrl : 'users/index'
	}
},
{
	url : '/users/profile',
	config : {
		templateUrl : 'users/profile'
	}
},
{
	url : '/tests/runs',
	config : {
		templateUrl : 'tests/runs/index'
	}
},
{
	url : '/tests/cases',
	config : {
		templateUrl : 'tests/cases/index'
	}
},
{
	url : '/tests/cases/:id/metrics',
	config : {
		templateUrl : 'tests/cases/metrics'
	}
},
{
	url : '/tests/runs/:ids/compare',
	config : {
		templateUrl : 'tests/runs/compare'
	}
},
{
	url : '/settings',
	config : {
		templateUrl : 'settings/index'
	}
},
{
	url : '/devices',
	config : {
		templateUrl : 'devices/index'
	}
},
{
	url : '/uainspections',
	config : {
		templateUrl : 'uainspections/index'
	}
},
{
	url : '/',
	config : {
		redirectTo: 'dashboards'
	}
},
{
	url : '/views/:id',
	config : {
		templateUrl : 'views/index'
	}
}]);

ZafiraApp.config([ '$routeProvider', 'ROUTES',
	function($routeProvider, ROUTES) {
		angular.forEach(ROUTES, function(value) {
			$routeProvider.when(value.url, value.config);
		});
		$routeProvider.otherwise({
			templateUrl: 'errors/404'
		});
	}
]);