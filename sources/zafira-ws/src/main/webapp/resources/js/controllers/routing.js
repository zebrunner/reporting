ZafiraApp.constant('ROUTES', [  
{
	url : '/dashboard',
	config : {
		templateUrl : 'dashboard'
	}
},
{
	url : '/tests/runs/:ids/compare',
	config : {
		templateUrl : 'tests/runs/compare'
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
