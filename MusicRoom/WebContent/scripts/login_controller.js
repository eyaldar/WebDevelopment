'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var loginController = function($rootScope, $scope, Restangular, $state) {

		var loginService = Restangular.one("users");
		$scope.errorText = '';
		$scope.credentials = {
			name : '',
			password : ''
		};

		$scope.login = function() {
			loginService.post('', $scope.credentials).then(function(result) {
				$rootScope.checkLoginState();
				$state.go('home');
			}, function(reason) {
				$scope.errorText = reason.data.error;
			});
		};

		var formMessages = {
			showUserNamePrompt : false,
			showPasswordPrompt : false,
		};

		var formFuncs = {
			toggleUserNamePrompt : function(value) {
				formMessages.showUserNamePrompt = value;
			},
			togglePasswordPrompt : function(value) {
				formMessages.showPasswordPrompt = value;
			}
		};
		
		$scope.funcs = formFuncs;
		$scope.messages = formMessages;
	};

	musicRoom.controller('LoginController', loginController);
}());