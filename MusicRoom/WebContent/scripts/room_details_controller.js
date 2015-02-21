'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var roomDetailsController = function($scope, Restangular, $stateParams) {

		var roomService = Restangular.one("studios", $stateParams.studioId)
				.one($stateParams.id);

		roomService.get().then(function(room) {
			$scope.room = room;
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

		$scope.messages = formMessages;
		$scope.funcs = formFuncs;
		$scope.orderData = orderData;
	};

	musicRoom.controller('RoomDetailsController', roomDetailsController);
}())