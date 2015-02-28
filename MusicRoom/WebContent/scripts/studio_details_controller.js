'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioDetailsController = function($scope, Restangular, decodeService, $stateParams) {
    	
        var studioService = Restangular.one("studios", $stateParams.id);
        
        studioService.get().then(function(studio) {
            $scope.studio = studio;
            
			decodeService.getDecodeWithParameters(
					decodeService.decodeTypes.cities, {
						"id" : studio.city_id
					}, function(result) {
						studio.city = result[0];
					});
        });
    };

    musicRoom.controller('StudioDetailsController', studioDetailsController);
}());