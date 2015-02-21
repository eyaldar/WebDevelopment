'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var bandDetailsController = function($scope, Restangular, $stateParams) {
    	
        var bandService = Restangular.one("bands", $stateParams.id);
        
        bandService.get().then(function(band) {
            $scope.band = band;
        });
    };

    musicRoom.controller('BandDetailsController', bandDetailsController);
}());