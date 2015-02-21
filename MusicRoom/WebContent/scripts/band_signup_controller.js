'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var bandSignupController = function($scope, Restangular) {
    	
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
				$rootScope.checkLoginState();
				$state.go('home');
				// TODO: add "Signed up successfully!"
			}, function(reason) {
				$scope.showSubmittedError = true;

				if (reason.data.error) {
					$scope.errorText = reason.data.error;
				} else {
					$scope.errorText = "Error occured!"
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
        
        // BAND MEMBERS
        ctrl.newMember = {};
        ctrl.newMember.instruments = [];
        ctrl.signupForm.band.band_members = [];
        
        ctrl.addMember = function () {
        	ctrl.signupForm.band.band_members.push(ctrl.newMember);
        	ctrl.newMember = {};
        	ctrl.newMember.instruments = [];
        };
        
        // BAND MEMBER ROLES
        var roles = ["Singer", "Guitar", "Bass", "Drummer", "Keyboards", "Brass Instruments", "Violin", "Other"];
        //var roles = [1, 2, 3, 4, 5, 6];
        ctrl.roles = roles;
        
        // TODO: INSTRUMENTS
        
        // CONTROLLER OBJECT
        $scope.ctrl = ctrl;
    };

    musicRoom.controller('BandSignupController', bandSignupController);
}());


/*
// MESSAGES
var showMessages = function (field) {
    return ctrl.signupForm[field].$touched || ctrl.signupForm.$submitted
};

ctrl.showMessages = showMessages;

// ERRORS
var hasErrorClass = function (field) {
    return ctrl.signupForm[field].$touched 
        && ctrl.signupForm[field].$invalid;
};

ctrl.hasErrorClass = hasErrorClass;
*/

// SUBMIT
/*$scope.signup = function() {
	singupService.post('', $scope.credentials).then(function(result) {
		$rootScope.checkLoginState();
		$state.go('home');
	},
	function(reason) {
		$scope.errorText = reason.data.error;
	});
}*/