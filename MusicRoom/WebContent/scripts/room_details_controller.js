'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var roomDetailsController = function($scope, Restangular, $stateParams) {

		Restangular.setBaseUrl("rest/");
		var roomService = Restangular.one("studios", $stateParams.studioId)
				.one($stateParams.id);

		roomService.get().then(function(room) {
			$scope.room = room;
		});
		
		$scope.orderRoom = function() {
			
		};
	};

	musicRoom.controller('RoomDetailsController', roomDetailsController);
}());