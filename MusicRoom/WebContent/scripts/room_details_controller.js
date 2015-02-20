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

		var initDate = function() {
			var date = new Date();
			var roundedMinutes = 15 * Math.round(date.getMinutes() / 15);
			date.setMinutes(roundedMinutes, 0, 0);

			return date;
		};

		var orderData = {
			nowTime : initDate(),
			startTime : initDate(),
			endTime : initDate(),
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
			showMessages : function(form, field) {
				return form[field].$touched || form.$submitted;
			},
			hasErrorClass : function(form, field) {
				return form[field].$touched && form[field].$invalid;
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