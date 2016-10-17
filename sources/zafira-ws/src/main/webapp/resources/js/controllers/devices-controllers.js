'use strict';

ZafiraApp.controller('DevicesCtrl', [ '$scope', '$http','$location', '$route', '$modal', function($scope, $http, $location, $route, $modal) {
	
	$scope.listDevices = function(){
		$http.get('devices/list').success(function(data) {
			$scope.devices = data;
		}).error(function(data, status) {
			alert('Devices list is not retrieved!');
		});
	};
	
	$scope.syncDevices = function(){
		$http.put('devices/sync').success(function(data) {
			$route.reload();
		}).error(function(data, status) {
			alert('Devices not synced!');
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
					$http.post('devices', $scope.device).success(function(data) {
						$modalInstance.close(0);
						$route.reload();
					}).error(function(data, status) {
						alert('Device is not created!');
					});
				};
				
				$scope.update = function(device){
					$http.put('devices', device).success(function(data) {
						$modalInstance.close(0);
						$route.reload();
					}).error(function(data, status) {
						alert('Device is not updated!');
					});
				};
				
				$scope.delete = function(device){
					$http.delete('devices/' + device.id).success(function(data) {
						$modalInstance.close(0);
						$route.reload();
					}).error(function(data, status) {
						alert('Device is not deleted!');
					});
				};
				
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		});
	};
	
	(function init(){
		$scope.listDevices();
	})();
} ]);
