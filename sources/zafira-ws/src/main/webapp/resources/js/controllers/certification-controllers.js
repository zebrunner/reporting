'use strict';

ZafiraApp.controller('CertificationCtrl', [ '$scope', '$http','$location', '$route', '$modal', function($scope, $http, $location, $route, $modal) {
	
	$scope.certificationDetails = null;
	
	$scope.upstreamJobId = $location.search().upstreamJobId;
	$scope.upstreamJobBuildNumber = $location.search().upstreamJobBuildNumber;
	
	$scope.loadCertificationDetails = function(){
		$http.get('certification/details?upstreamJobId=' + $scope.upstreamJobId + "&upstreamJobBuildNumber=" + $scope.upstreamJobBuildNumber).then(function successCallback(data){
			$scope.certificationDetails = data.data;
		}, function errorCallback(data){
			alertify.error('Certification details not loaded!');
		});
	};
	
	(function init(){
		$scope.loadCertificationDetails();
	})();
} ]);
