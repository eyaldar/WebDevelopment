'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var bandSignupController = function($rootScope, $scope, Restangular, $state, ngDialog, decodeService) {
    	
    	// INIT
    	var ctrl = this;
    	ctrl.signupForm = {}; // This will be passed to the server
    	ctrl.signupForm.user = {};
    	ctrl.signupForm.band = {};
    	
    	var bandSignupService = Restangular.one("bands");
    	
    	// ERRORS
    	$scope.showSubmittedError = false;
		$scope.errorText = '';
    	
		// SIGNUP FUNC
		$scope.signup = function() {
			bandSignupService.post('', ctrl.signupForm).then(function(result) {			
				$state.go('login');
				// TODO: add "Signed up successfully!"
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
        
        // GENRES
        var genres = ["Rock", "Pop", "Hip-Hop", "Jazz", "Folk", "Country", "Metal"];
        ctrl.genres = genres;
        
        var initNewMember = function() {
        	ctrl.newMember = {};
            ctrl.newMember.instruments = [];
            ctrl.newMember.instrumentObjs = [];
        };
        ctrl.initNewMember = initNewMember;
        
        // BAND MEMBERS
        ctrl.initNewMember();
        ctrl.signupForm.band.band_members = [];
        
        ctrl.addMember = function () {
        	ctrl.signupForm.band.band_members.push(ctrl.newMember);
        	ctrl.initNewMember();
        	
        	ngDialog.closeAll();
        };
        
        // BAND MEMBER ROLES
        var roles = ["Singer", "Guitar", "Bass", "Drummer", "Keyboards", "Brass Instruments", "Violin", "Other"];
        //var roles = [1, 2, 3, 4, 5, 6];
        ctrl.roles = roles;
       
        // INSTRUMENTS
        ctrl.instrument = undefined;
        var onInstrumentChanged = function() {
			var selectedInstrument = ctrl.instrument;
			if (selectedInstrument) {

				decodeService.getDecodeWithParameters(
						decodeService.decodeTypes.equipmentTypes, {
							"id" : selectedInstrument.id
						}, function(result) {
							ctrl.newMember.instruments = [];
							ctrl.newMember.instruments.push(result[0].id);
							
							ctrl.newMember.instrumentObjs = [];
							ctrl.newMember.instrumentObjs.push(result[0]);
						});

			}
		}
        
        ctrl.onInstrumentChanged = onInstrumentChanged;
        
        decodeService.getDecode(decodeService.decodeTypes.equipmentTypes, function(
				result) {
        	ctrl.instruments = result;
		}, function(result) {
			console.log(result);
		});
        
        // ADD MEMBER DIALOG
        ctrl.openAddMemberDialog = function() {
			var dialog = ngDialog.open({
				template : 'templates/add_band_member.htm',
				className : 'ngdialog-theme-plain',
				scope : $scope
			});
		};
        
		// CONTROLLER OBJECT
        $scope.ctrl = ctrl;
    };

    musicRoom.controller('BandSignupController', bandSignupController);
}());