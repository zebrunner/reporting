'use strict';

ZafiraApp.controller('SettingsCtrl', [ '$scope', '$http','$location', '$route', '$modal', function($scope, $http, $location, $route, $modal) {
	
	$scope.listSettings = function(){
		$http.get('settings/list').success(function(data) {
			$scope.settings = data;
		}).error(function(data, status) {
			alert('Settings list is not retrieved!');
		});
	};
	
	$scope.openSettingsModal = function(setting){
		$modal.open({
			templateUrl : 'resources/templates/setting-details-modal.jsp',
			resolve : {
				'setting' : function(){
					return setting;
				}
			},
			controller : function($scope, $modalInstance, setting){
				
				$scope.setting = {};
				if(setting)
				{
					$scope.setting = setting;
				}
				
				$scope.create = function(){
					$http.post('settings', $scope.setting).success(function(data) {
						$modalInstance.close(0);
						$route.reload();
					}).error(function(data, status) {
						alert('Setting is not created!');
					});
				};
				
				$scope.update = function(setting){
					$http.put('settings', setting).success(function(data) {
						$modalInstance.close(0);
						$route.reload();
					}).error(function(data, status) {
						alert('Setting is not updated!');
					});
				};
				
				$scope.delete = function(setting){
					$http.delete('settings/' + setting.id).success(function(data) {
						$modalInstance.close(0);
						$route.reload();
					}).error(function(data, status) {
						alert('Setting is not deleted!');
					});
				};
				
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		});
	};
	
	(function init(){
		$scope.listSettings();
	})();
} ]);
