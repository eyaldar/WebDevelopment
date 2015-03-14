'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var studioSignupController = function($rootScope, $scope, Restangular, decodeService, $state, ngDialog) {
    	
    	// INIT
    	var ctrl = this;
    	ctrl.signupForm = {}; // This will be passed to the server
    	ctrl.signupForm.user = {};
    	ctrl.signupForm.studio = {};
    	
    	var studioSignupService = Restangular.one("studios");
    	
    	// ERRORS
    	$scope.showSubmittedError = false;
		$scope.errorText = '';
    	
		// SIGNUP FUNC
		$scope.signup = function() {
			studioSignupService.post('', ctrl.signupForm).then(function(result) {			
				$state.go('login');
			}, function(reason) {
				$scope.showSubmittedError = true;

				if (reason.data.error) {
					$scope.errorText = reason.data.error;
				} else {
					$scope.errorText = "Error in signup occured!"
				}
			});
		};
		
		// PROMPTS
        var toggleUsernamePrompt = function (value) {
            ctrl.showUsernamePrompt = value;
        };
        ctrl.showUsernamePrompt = false;
        ctrl.toggleUsernamePrompt = toggleUsernamePrompt;
        
        var toggleLogoPrompt = function (value) {
            ctrl.showLogoPrompt = value;
        };
        ctrl.showLogoPrompt = false;
        ctrl.toggleLogoPrompt = toggleLogoPrompt;
        
        var togglePicturePrompt = function (value) {
            ctrl.showPicturePrompt = value;
        };
        ctrl.showPicturePrompt = false;
        ctrl.togglePicturePrompt = togglePicturePrompt;
        
        // PASSWORD
        ctrl.showPassword = false;
        var getPasswordType = function () {
            return ctrl.showPassword ? 'text' : 'password';
        };
        ctrl.getPasswordType = getPasswordType;
        
        // CITY & AREA
        var onAreaChanged = function() {
			var selectedArea = $scope.chosenArea;
			if (selectedArea) {
				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.cities, {
							"area_id" : selectedArea.id
						}, function(result) {
							$scope.cities = result;
						}, function(result) {
							console.log(result);
							$scope.cities = [];
							ctrl.signupForm.studio.city = null;
						});

			} else {
				$scope.cities = [];
				ctrl.signupForm.studio.city = null;
			}
		}
        $scope.onAreaChanged = onAreaChanged;
        
        // ROOMS
        var initNewRoom = function() {
        	ctrl.newRoom = {};
            ctrl.newRoom.room_type = [];
            ctrl.newRoom.equipment = [];
            ctrl.newRoom.rate = 0; // Initial rate is set to zero
        };
        ctrl.initNewRoom = initNewRoom;
        
        ctrl.initNewRoom();
        ctrl.signupForm.studio.rooms = [];
        
        ctrl.addRoom = function () {
        	ctrl.signupForm.studio.rooms.push(ctrl.newRoom);
        	ctrl.initNewRoom();
        	
        	ngDialog.closeAll();
        };
        
        // ADD ROOM DIALOG
        ctrl.openAddRoomDialog = function() {
			var addRoomDialog = ngDialog.open({
				template : 'templates/add_room_to_studio.htm',
				className : 'ngdialog-theme-plain',
				scope : $scope
			});
		};
        
		// EQUIPMENT
		ctrl.newItem = {};
        ctrl.newRoom.equipment = [];
        
        ctrl.addEquipmentItem = function () {
        	ctrl.newRoom.equipment.push(ctrl.newItem);
        	ctrl.newItem = {};
        	
        	ctrl.equipmentDialog.close(); // DEBUG
        };
		
		// ADD EQUIPMENT DIALOG
        ctrl.openAddEquipmentDialog = function() {
			var equipmentDialog = ngDialog.open({
				template : 'templates/add_equipment_to_room.htm',
				className : 'ngdialog-theme-plain',
				scope : $scope
			});
			ctrl.equipmentDialog = equipmentDialog;
		};
		
    	// DECODES
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
        
		decodeService.getDecode(
			decodeService.decodeTypes.equipmentCategories, function(result) {
				$scope.equipmentCategories = result;
			}, function(result) {
				console.log(result);
				$scope.equipmentCategories = [];
			});
		
		$scope.onCategoryChanged = function() {
			var selectedCategory = ctrl.newItem.category;
			if (selectedCategory) {

				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.equipmentTypes, {
							"category_id" : selectedCategory.id
						}, function(result) {
							$scope.equipmentTypes = result;
						}, function(result) {
							console.log(result);
							$scope.equipmentTypes = [];
							ctrl.newItem.type = null;
						});

			} else {
				$scope.equipmentTypes = [];
				ctrl.newItem.type = null;
			}
		}
		
		// CONTROLLER OBJECT
        $scope.ctrl = ctrl;
    };

    musicRoom.controller('StudioSignupController', studioSignupController);
}());