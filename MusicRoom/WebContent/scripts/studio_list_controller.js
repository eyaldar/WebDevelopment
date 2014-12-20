'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioListController = function($scope, Restangular) {
    	$scope.items = {};
    	
        Restangular.setBaseUrl("rest/");
        var bandsService = Restangular.all("studios");
        
        bandsService.getList().then(function(studios) {
            $scope.items = studios;
        });
    };

    musicRoom.controller('StudioListController', studioListController);
}());