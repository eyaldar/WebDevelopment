'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioListController = function($scope, Restangular, decodeService) {
    	$scope.items = {};
    	$scope.isLoading = false;
    	
        var studiosService = Restangular.all("studios");
        
        
        studiosService.getList().then(function(studios) {
            $scope.items = studios;
            
            angular.forEach($scope.items, function(studio, index) {
				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.cities, {
							"id" : studio.city_id
						}, function(result) {
							$scope.items[index].city = result[0];
						});
            });
            
            $scope.isLoading = false;
        },	function(reason) {
        	$scope.isLoading = false;
        });
        
        $scope.isLoading = true;
    };

    musicRoom.controller('StudioListController', studioListController);
}());