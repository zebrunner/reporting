'use strict';

ZafiraApp.controller('SettingsCtrl', [ '$scope', '$http','$location', '$route', '$modal', function($scope, $http, $location, $route, $modal) {
	
	$scope.listSettings = function(){
		$http.get('settings/list').then(function successCallback(data) {
			$scope.settings = data.data;
		}, function errorCallback(data) {
			alertify.error('Settings list is not retrieved!');
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
					$http.post('settings', $scope.setting).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
					}, function errorCallback(data) {
						alertify.error('Setting is not created!');
					});
				};
				
				$scope.update = function(setting){
					$http.put('settings', setting).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
					}, function errorCallback(data) {
						alertify.error('Setting is not updated!');
					});
				};
				
				$scope.delete = function(setting){
					$http.delete('settings/' + setting.id).then(function successCallback(data) {
						$modalInstance.close(0);
						$route.reload();
					}, function errorCallback(data) {
						alertify.error('Setting is not deleted!');
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
		$scope.listSettings();
	})();
} ]);
