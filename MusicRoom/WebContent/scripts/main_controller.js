'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var mainController = function($rootScope, $scope, Restangular, $state) {

		Restangular.setBaseUrl("rest/");
		
		var userStateService = Restangular.one("users");

		$rootScope.checkLoginState = function() {

			userStateService.one("state").get().then(function(result) {	
				if(result.user) {
					$rootScope.loggedUserId = result.user.id;
				}
				
				$rootScope.loggedUserName = result.name;
				$rootScope.logged = result.logged;
			});
		};

		$scope.logout = function() {
			userStateService.one("logout").get().then(function(result) {
				$rootScope.checkLoginState();
				$state.go('home');
			});
		};

		$rootScope.checkLoginState();

		$rootScope.maxRating = 5;

		$rootScope.formFuncs = {
			showMessages : function(form, field) {
				return form[field].$touched || form.$submitted;
			},
			hasErrorClass : function(form, field) {
				return form[field].$touched && form[field].$invalid;
			},
			hasSuccessClass : function(form, field) {
				return form[field].$touched && form[field].$valid;
			}
		};
	};

	musicRoom.controller('MainController', mainController);
}());