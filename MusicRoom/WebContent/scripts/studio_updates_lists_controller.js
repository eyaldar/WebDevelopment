'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioUpdatesListController = function($rootScope, $scope, Restangular) {
    	
        var mostViewedService = Restangular.all("studios/mostviewed");
        var recentService = Restangular.all("studios/recent");
        
        mostViewedService.getList().then(function(studios) {
            $scope.mostViewedStudios = studios;
        });
        
        recentService.getList().then(function(studios) {
            $scope.recentStudios = studios;
        });
    };

    musicRoom.controller('StudioUpdatesListController', studioUpdatesListController);
}());