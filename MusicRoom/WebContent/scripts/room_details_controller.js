'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var roomDetailsController = function($scope, $rootScope, Restangular, ngDialog, decodeService, 
			localDbManager, $stateParams) {

		var roomService = Restangular.one("studios", $stateParams.studioId)
				.one($stateParams.id);

		roomService.get().then(function(room) {
			$scope.room = room;
			$scope.room.roomTypes = [];
			
			angular.forEach($scope.room.room_type, function(roomTypeId) {
				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.roomTypes, {
							"id" : roomTypeId
						}, function(result) {
							$scope.room.roomTypes.push(result[0]);
						});
			});
			
            angular.forEach($scope.room.equipment, function(equipment, index) {
				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.equipmentTypes, {
							"id" : equipment.equipment_type_id
						}, function(result) {
							$scope.room.equipment[index].equipment_type = result[0];
						});
            });

		});

		var orderData = {
			nowTime : $scope.initDate(),
			startTime : $scope.initDate(),
			endTime : $scope.initDate(),
			startTimeText : "",
			endTimeText : ""
		};

		var formMessages = {
			showStartTimePrompt : false,
			showEndTimePrompt : false,
			showSubmittedPrompt: false,
			showSubmittedError: false
		};

		var formFuncs = {
			toggleStartTimePrompt : function(value) {
				formMessages.showStartTimePrompt = value;
			},
			toggleEndTimePrompt : function(value) {
				formMessages.showEndTimePrompt = value;
			},
			orderRoom : function(form) {
				Restangular.all("studios/schedule").post({
					roomId: $scope.room.id,
					start : orderData.startTimeText,
					end : orderData.endTimeText
				}).then(function(response) {
					formMessages.showSubmittedPrompt = true;
					formMessages.showSubmittedError = false;
				}, function(response) {
					$scope.errorText = response.data.error;
					
					formMessages.showSubmittedPrompt = false;
					formMessages.showSubmittedError = true;
				});
			}
		};

        // ADD ROOM DIALOG
        $scope.openRoomScheduleDialog = function() {
			var addRoomDialog = ngDialog.open({
				template : 'templates/room_schedule.htm',
				className : 'ngdialog-theme-plain',
				controller: 'RoomScheduleController',
				scope : $scope
			});
		};
		
		$scope.messages = formMessages;
		$scope.funcs = formFuncs;
		$scope.orderData = orderData;
		$scope.showOrderForm = $rootScope.logged && $rootScope.userTypeId === 2;
		$scope.showRoomSchedule = $rootScope.logged && $rootScope.userTypeId === 1;
	};

	musicRoom.controller('RoomDetailsController', roomDetailsController);
}())