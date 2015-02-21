'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioListController = function($scope, Restangular) {
    	$scope.items = {};
    
        var studiosService = Restangular.all("studios");
        
        studiosService.getList().then(function(studios) {
            $scope.items = studios;
        });
    };

    musicRoom.controller('StudioListController', studioListController);
}());