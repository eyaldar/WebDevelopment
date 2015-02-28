'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var requiredEquipmentController = function($scope, Restangular, decodeService) {
		
		decodeService.getDecode(
				decodeService.decodeTypes.equipmentCategories, function(result) {
					$scope.equipmentCategories = result;
				}, function(result) {
					console.log(result);
					$scope.equipmentCategories = [];
				});
		
		$scope.equipmentCategories = [];
		$scope.equipmentTypes = [];
		
		$scope.selected = {
			selectedCategory : null,
			selectedType : null
		}

		$scope.selectedItems = [];
		$scope.onCategoryChanged = function() {
			var selectedCategory = $scope.selected.selectedCategory;
			if (selectedCategory) {

				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.equipmentTypes, {
							"category_id" : selectedCategory.id
						}, function(result) {
							$scope.equipmentTypes = result;
						}, function(result) {
							console.log(result);
							$scope.equipmentTypes = [];
							$scope.selected.selectedType = null;
						});

			} else {
				$scope.equipmentTypes = [];
				$scope.selected.selectedType = null;
			}
		}

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