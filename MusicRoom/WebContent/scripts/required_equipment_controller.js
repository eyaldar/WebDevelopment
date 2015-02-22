'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var requiredEquipmentController = function($scope, Restangular) {

		var equipmentCategories = [ {
			id : '1',
			name : 'B'
		}, {
			id : '2',
			name : 'C'
		} ];

		var equipmentTypes = [ {
			id : '1',
			name : 'A'
		}, {
			id : '2',
			name : 'B'
		} ];

		$scope.equipmentCategories = equipmentCategories;
		$scope.equipmentTypes = equipmentTypes;

		$scope.selected = {
			selectedCategory : null,
			selectedType : null
		}

		$scope.selectedItems = [];
		$scope.onCategoryChanged = function() {
			console.log($scope.selected.selectedCategory);
		};

		$scope.onAdd = function() {
			if ($scope.selected.selectedCategory
					&& $scope.selected.selectedType) {
				var equipment = {
					category : $scope.selected.selectedCategory,
					type : $scope.selected.selectedType,
					selected : false,
				};

				$scope.searchData.equipmentArray.push(equipment);
			}
		};
		$scope.onRemove = function() {
			var equipmentArray = $scope.searchData.equipmentArray;

			angular.forEach($scope.selectedItems, function(value, key) {
				var index = equipmentArray.indexOf(value);

				if (index !== -1) {
					equipmentArray.splice(index, 1);
				}
			})
		};
	};

	musicRoom.controller('RequiredEquipmentController',
			requiredEquipmentController);
}());