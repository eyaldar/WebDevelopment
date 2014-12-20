'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var bandListController = function($scope, Restangular) {
    	$scope.items = {};
    	
        Restangular.setBaseUrl("rest/");
        var bandsService = Restangular.all("bands");
        
        bandsService.getList().then(function(bands) {
            $scope.items = bands;
        });
    };

    musicRoom.controller('BandListController', bandListController);
}());