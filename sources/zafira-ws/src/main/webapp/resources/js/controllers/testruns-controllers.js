'use strict';

ZafiraApp.controller('TestRunsCompareCtrl', [ '$scope', '$rootScope', '$http', '$routeParams', function($scope, $rootScope, $http, $routeParams) {
	
	$scope.initCompareMatrix = function(){
		$http.get('tests/runs/' + $routeParams.ids + '/compare').success(function(matrix) {
			$scope.matrix = matrix;
			$scope.testNames = [];
			for(var id in matrix)
			{
				for(var name in matrix[id])
				{
					$scope.testNames.push(name);
				}
				break;
			}
			$scope.testNames.sort()
		});
	};
	
	$scope.substring = function(text, size){
		return text.substring(0, size);
	};
	
	(function init(){
		$scope.initCompareMatrix();
	})();
} ]);