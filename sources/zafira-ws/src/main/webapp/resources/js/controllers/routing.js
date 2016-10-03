ZafiraApp.constant('ROUTES', [  
{
	url : '/dashboards',
	config : {
		templateUrl : 'dashboards'
	}
},
{
	url : '/users',
	config : {
		templateUrl : 'users/index'
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
}]);

ZafiraApp.config([ '$routeProvider', 'ROUTES',
	function($routeProvider, ROUTES) {
		angular.forEach(ROUTES, function(value) {
			$routeProvider.when(value.url, value.config);
		});
		$routeProvider.otherwise({
			redirectTo : ROUTES[0].url
		});
	}
]);
