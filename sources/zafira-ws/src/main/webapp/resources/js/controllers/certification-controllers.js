'use strict';

ZafiraApp.controller('CertificationCtrl', [ '$scope', '$http','$location', '$route', '$modal', function($scope, $http, $location, $route, $modal) {
	
	$scope.certificationDetails = null;
	
	$scope.upstreamJobId = $location.search().upstreamJobId;
	$scope.upstreamJobBuildNumber = $location.search().upstreamJobBuildNumber;
	
	$scope.loadCertificationDetails = function(){
		$http.get('certification/details?upstreamJobId=' + $scope.upstreamJobId + "&upstreamJobBuildNumber=" + $scope.upstreamJobBuildNumber).success(function(data) {
			$scope.certificationDetails = data
		}).error(function(data, status) {
			alert('Certification details not loaded!');
		});
	};
	
	(function init(){
		$scope.loadCertificationDetails();
	})();
} ]);
