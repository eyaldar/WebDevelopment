'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var roomScheduleController = function($scope, Restangular, decodeService) {

		var roomService = Restangular.all("studios/schedule")
				.all($scope.room.id);
		$scope.rooms = null;
		roomService.getList().then(function(roomSchedule) {
			$scope.roomSchedule = roomSchedule;

			angular.forEach(roomSchedule, function(room, index) {
				var bandService = Restangular.one("bands", room.band_id);
				var roomIndex = index;
				
				bandService.get().then(function(band) {
					$scope.roomSchedule[roomIndex].bandName = band.name;
				});
			});
		});

	};

	musicRoom.controller('RoomScheduleController', roomScheduleController);
}());