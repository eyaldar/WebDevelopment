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

		var onAreaChanged = function() {
			console.log(searchData.area);
		}

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
			
			if(searchData.equipmentArray && equipmentArray.length > 0) {
				requestData.equipment_type = [];
				
				angular.forEach(searchData.equipmentArray, function(value, key) {
					equipment_type.push(value.type.id);
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
				searchData.equipmentArray.splice(0, searchData.equipmentArray.length);
				
				formFuncs.search();
			},
			search : function(form) {
				var requestData = buildRequestData();
				Restangular.all("studios").getList(requestData).then(function(studios) {
					formMessages.showSubmittedError = false;
					
					var items = $scope.items;
					
					items.splice(0, items.length);
					items.push.apply(items, studios);
					$scope.isLoading = false;
					
				}, function(response) {
					$scope.errorText = "Error occured on server side! couldn't load studios.";

					formMessages.showSubmittedError = true;
					$scope.isLoading = false;
				});
				
				$scope.isLoading = true;
			}
		};

		$scope.nowTime = $scope.initDate();
		$scope.onAreaChanged = onAreaChanged;
		$scope.messages = formMessages;
		$scope.funcs = formFuncs;
		$scope.accordion = accordion;
		$scope.slider = slider;
		$scope.searchData = searchData;

		$scope.areas = [ {
			id : '1',
			name : 'A'
		}, {
			id : '2',
			name : 'B'
		} ];

		$scope.cities = [ {
			id : '1',
			name : 'C'
		}, {
			id : '2',
			name : 'D'
		} ];

		$scope.roomTypes = [ {
			id : '1',
			name : 'E'
		}, {
			id : '2',
			name : 'F'
		} ];
	};

	musicRoom.controller('SearchController', searchController);
}());