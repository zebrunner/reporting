(function () {
    'use strict';

    angular
        .module('app.certification')
        .controller('CertificationController', CertificationController);

    function CertificationController($scope, $rootScope, $cookies, $location, $state, $http, $mdConstant, $stateParams, CertificationService) {
        'ngInject';
    	
    	$scope.certificationDetails = null;
	
		(function init() {
			CertificationService.loadCertificationDetails($location.search().upstreamJobId, $location.search().upstreamJobBuildNumber)
        	.then(function (rs) {
        		if(rs.success)
        		{
        			$scope.certificationDetails = rs.data;
        		}
        		else
        		{
        			alertify.error(rs.message);
        		}
            });
        })();
    }

})();
