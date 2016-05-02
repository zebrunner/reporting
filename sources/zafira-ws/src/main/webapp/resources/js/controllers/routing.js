ZafiraApp.constant('ROUTES', [  
{
	url : '/dashboard',
	config : {
		templateUrl : 'dashboard'
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
