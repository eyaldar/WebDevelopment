'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var searchController = function($scope, Restangular, decodeService) {

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

		var equipmentArray = [];

		var searchData = {
			name : '',
			city : null,
			area : null,
			roomType : null,
			equipmentArray : equipmentArray,
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

		var buildRequestData = function() {
			var searchData = $scope.searchData;
			var requestData = {};

			if (searchData.name != '') {
				requestData.name = searchData.name;
			}

			if (searchData.area) {
				requestData.area_id = searchData.area.id;
			}

			if (searchData.roomType) {
				requestData.room_type_Id = searchData.roomType.id;
			}

			if (searchData.city) {
				requestData.city_id = searchData.city.id;
			}

			if (searchData.maxRoomRate > 0) {
				requestData.max_room_rate = searchData.maxRoomRate;
			}

			if (searchData.endTime - searchData.startTime > 0) {
				requestData.start_time = searchData.startTimeText;
				requestData.end_time = searchData.endTimeText;
			}

			if (searchData.equipmentArray && equipmentArray.length > 0) {
				requestData.equipment_type = [];

				angular.forEach(searchData.equipmentArray,
						function(value, key) {
							requestData.equipment_type.push(value.type.id);
						});
			}

			return requestData;
		}

		var formFuncs = {
			toggleStartTimePrompt : function(value) {
				formMessages.showStartTimePrompt = value;
			},
			toggleEndTimePrompt : function(value) {
				formMessages.showEndTimePrompt = value;
			},
			clear : function(form) {
				searchData.name = '';
				searchData.roomType = null;
				searchData.area = null;
				searchData.city = null;
				searchData.startTimeText = '';
				searchData.endTimeText = '';
				searchData.maxRoomRate = 0;
				searchData.equipmentArray.splice(0,
						searchData.equipmentArray.length);

				formFuncs.search();
			},
			search : function(form) {
				var requestData = buildRequestData();
				Restangular
						.all("studios")
						.getList(requestData)
						.then(
								function(studios) {
									formMessages.showSubmittedError = false;

									var items = $scope.items;

						            $scope.getCityNames(studios);
									
									items.splice(0, items.length);
									items.push.apply(items, studios);
									$scope.isLoading = false;

								},
								function(response) {
									$scope.errorText = "Error occured on server side! couldn't load studios.";

									formMessages.showSubmittedError = true;
									$scope.isLoading = false;
								});

				$scope.isLoading = true;
			}
		};

		var onAreaChanged = function() {
			var selectedArea = $scope.searchData.area;
			if (selectedArea) {

				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.cities, {
							"area_id" : selectedArea.id
						}, function(result) {
							$scope.cities = result;
						}, function(result) {
							console.log(result);
							$scope.cities = [];
							$scope.searchData.city = null;
						});

			} else {
				$scope.cities = [];
				$scope.searchData.city = null;
			}
		}

		$scope.areas = [];
		$scope.cities = [];
		$scope.roomTypes = [];
		$scope.nowTime = $scope.initDate();
		$scope.onAreaChanged = onAreaChanged;
		$scope.messages = formMessages;
		$scope.funcs = formFuncs;
		$scope.accordion = accordion;
		$scope.slider = slider;
		$scope.searchData = searchData;

		decodeService.getDecode(decodeService.decodeTypes.areas, function(
				result) {
			$scope.areas = result;
		}, function(result) {
			console.log(result);
		});
		
		decodeService.getDecode(decodeService.decodeTypes.roomTypes, function(
				result) {
			$scope.roomTypes = result;
		}, function(result) {
			console.log(result);
		});
	};

	musicRoom.controller('SearchController', searchController);
}());