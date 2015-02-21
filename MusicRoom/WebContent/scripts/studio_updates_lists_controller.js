'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioUpdatesListController = function($rootScope, $scope, Restangular) {
    	
        Restangular.setBaseUrl("rest/studios");
        var mostViewedService = Restangular.all("mostviewed");
        var recentService = Restangular.all("recent");
        
        mostViewedService.getList().then(function(studios) {
            $scope.mostViewedStudios = studios;
        });
        
        recentService.getList().then(function(studios) {
            $scope.recentStudios = studios;
        });
    };

    musicRoom.controller('StudioUpdatesListController', studioUpdatesListController);
}());