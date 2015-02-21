'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var manageAccountController = function($rootScope, $scope, Restangular,
			ngDialog, $state) {

		var manageService = Restangular.one("users");
		$scope.errorText = '';
		$scope.newCredentials = {
			password : ''
		};

		var inputType = {
			showNewPassword : false,
			showConfirmNewPassword : false
		};

		var formMessages = {
			showSubmittedPrompt : false,
			showSubmittedError : false,
			showNewPasswordPrompt : false,
			showConfirmNewPasswordPrompt : false,
		};

		var formFuncs = {
			toggleNewPasswordPrompt : function(value) {
				formMessages.showNewPasswordPrompt = value;
			},
			toggleConfirmNewPasswordPrompt : function(value) {
				formMessages.showConfirmNewPasswordPrompt = value;
			},
			getPasswordType : function(field) {
				var isText = false;
				if (field == 'newPassword') {
					isText = inputType.showNewPassword;
				} else {
					isText = inputType.showConfirmNewPassword;
				}

				return isText ? 'text' : 'password';
			}
		};

		$scope.update = function() {
			manageService.password = $scope.newCredentials.password;

			manageService.put().then(function(response) {
				formMessages.showSubmittedPrompt = true;
				formMessages.showSubmittedError = false;

			}, function(response) {
				$scope.errorText = response.data.error;

				formMessages.showSubmittedPrompt = false;
				formMessages.showSubmittedError = true;
			});
		};
		var deleteUser = function() {
			ngDialog.closeAll();

			Restangular.one('users', $scope.loggedUserId).remove().then(function(response) {
				$rootScope.checkLoginState();
				$state.go('home');
			}, function(response) {
				$scope.errorText = response.data.error;

				formMessages.showSubmittedError = true;
			});
		};

		$scope.openConfirm = function() {
			$scope.confirmDialog = {
				header : "Are you sure?",
				message : "Are you sure you want to delete your user?",
				onConfirm : deleteUser
			};

			var test = ngDialog.open({
				template : 'templates/confirm_dialog.htm',
				className : 'ngdialog-theme-plain',
				scope : $scope,

			});
		};

		$scope.funcs = formFuncs;
		$scope.inputType = inputType;
		$scope.messages = formMessages;
	};

	musicRoom.controller('ManageAccountController', manageAccountController);
}());