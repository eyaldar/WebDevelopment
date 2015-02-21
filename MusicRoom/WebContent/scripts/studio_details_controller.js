'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioDetailsController = function($scope, Restangular, $stateParams) {
    	
        var studioService = Restangular.one("studios", $stateParams.id);
        
        studioService.get().then(function(studio) {
            $scope.studio = studio;
        });
    };

    musicRoom.controller('StudioDetailsController', studioDetailsController);
}());