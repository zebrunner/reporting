'use strict';

ZafiraApp.controller('DevicesCtrl', [ '$scope', '$http','$location', '$route', '$modal', function($scope, $http, $location, $route, $modal) {
	
	$scope.listDevices = function(){
		$http.get('devices/list').then(function successCallback(data) {
			$scope.devices = data.data;
		}, function errorCallbackerror(data) {
			alertify.error('Devices list is not retrieved!');
		});
	};
	
	$scope.syncDevices = function(){
		$http.put('devices/sync').then(function successCallback(data) {
			$route.reload();
		}, function errorCallback(data) {
			alertify.error('Devices not synced!');
		});
	};
	
	$scope.openDeviceModal = function(device){
		$modal.open({
			templateUrl : 'resources/templates/device-details-modal.jsp',
			resolve : {
				'device' : function(){
					return device;
				}
			},
			controller : function($scope, $modalInstance, device){
				
				$scope.device = { "enabled" : true, "lastStatus" : true };
				if(device)
				{
					$scope.device = device;
				}
				
				$scope.create = function(){
					$http.post('devices', $scope.device).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
					}, function errorCallback(data) {
						alertify.error('Device is not created!');
					});
				};
				
				$scope.update = function(device){
					$http.put('devices', device).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
					}, function errorCallback(data) {
						alertify.error('Device is not updated!');
					});
				};
				
				$scope.delete = function(device){
					$http.delete('devices/' + device.id).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
					}, function errorCallback(data) {
						alertify.error('Device is not deleted!');
					});
				};
				
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		}).result.then(function(data) {
        }, function () {
        });
	};
	
	(function init(){
		$scope.listDevices();
	})();
} ]);
