'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioListController = function($scope, Restangular) {
    	$scope.items = {};
    	$scope.isLoading = false;
    	
        var studiosService = Restangular.all("studios");
        
        
        studiosService.getList().then(function(studios) {
            $scope.items = studios;
            $scope.isLoading = false;
        },	function(reason) {
        	$scope.isLoading = false;
        });
        
        $scope.isLoading = true;
    };

    musicRoom.controller('StudioListController', studioListController);
}());