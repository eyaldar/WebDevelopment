'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var mainController = function($rootScope, $scope, Restangular, localDbManager, myDataService, $state) {

		var userStateService = Restangular.one("users");
		
		$scope.logout = function() {
			userStateService.one("logout").get().then(function(result) {
				$rootScope.checkLoginState(true);
				$state.go('home');
			});
		};

		$rootScope.checkLoginState = function(wasChanged) {
			myDataService.getState(wasChanged, function(result) {
				if (result.user) {
					$rootScope.loggedUserId = result.user.id;
				}

				$rootScope.loggedUserName = result.name;
				$rootScope.logged = result.logged;
			}, null);
		};

		$rootScope.checkLoginState(false);
		$rootScope.maxRating = 5;
		$rootScope.noSelectionText = '-Any-';

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

		$rootScope.initDate = function() {
			var date = new Date();
			var roundedMinutes = 15 * Math.round(date.getMinutes() / 15);
			date.setMinutes(roundedMinutes, 0, 0);

			return date;
		};
	};

	musicRoom.controller('MainController', mainController);
}());