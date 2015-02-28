'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioListController = function($scope, Restangular, decodeService) {
    	$scope.items = {};
    	$scope.isLoading = false;
    	
        var studiosService = Restangular.all("studios");
        
        $scope.getCityNames = function(items) {
            angular.forEach(items, function(studio, index) {
				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.cities, {
							"id" : studio.city_id
						}, function(result) {
							items[index].city = result[0];
						});
            });
        };
        
        studiosService.getList().then(function(studios) {
            $scope.items = studios;
            $scope.getCityNames($scope.items);
            
            $scope.isLoading = false;
        },	function(reason) {
        	$scope.isLoading = false;
        });
        
        $scope.isLoading = true;
    };

    musicRoom.controller('StudioListController', studioListController);
}());