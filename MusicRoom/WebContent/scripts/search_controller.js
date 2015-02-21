'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var searchController = function($scope, Restangular) {

		var slider = {
			translate : function(value) {
				return value + ' NIS';
			}
		}

		var accordion = {
			isOpen : false,
			toggleIsOpen : function() {
				accordion.isOpen = !accordion.isOpen;
			}
		};

		var equipment_ids = [];

		var searchData = {
			name : '',
			cityId : '0',
			areaId : '0',
			roomTypeId : '0',
			equipmentIds : equipment_ids,
			maxRoomRate : 0,
			startTime : $scope.initDate(),
			startTimeText : '',
			endTime : $scope.initDate(),
			endTimeText : ''
		};

		var formMessages = {
			showStartTimePrompt : false,
			showEndTimePrompt : false,
			showSubmittedError : false
		};

		var formFuncs = {
			toggleStartTimePrompt : function(value) {
				formMessages.showStartTimePrompt = value;
			},
			toggleEndTimePrompt : function(value) {
				formMessages.showEndTimePrompt = value;
			},
			clear : function(form) {
				searchData.startTimeText =  '';
				searchData.endTimeText = '';
			},
			search : function(form) {
				Restangular.all("studios").getList().then(function(response) {
					formMessages.showSubmittedError = false;
				}, function(response) {
					$scope.errorText = response.data.error;

					formMessages.showSubmittedError = true;
				});
			}
		};

		$scope.nowTime = $scope.initDate();
		$scope.messages = formMessages;
		$scope.funcs = formFuncs;
		$scope.accordion = accordion;
		$scope.slider = slider;
		$scope.searchData = searchData;
	};

	musicRoom.controller('SearchController', searchController);
}());