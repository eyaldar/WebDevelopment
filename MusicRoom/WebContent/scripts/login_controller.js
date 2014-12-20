'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var loginController = function($rootScope, $scope, Restangular, $state) {
    	
    	var loginService = Restangular.one("users");
    	$scope.errorText = '';
    	$scope.credentials = {
    			name: '',
    			password: ''
    	};

    	$scope.login = function() {
    		loginService.post('', $scope.credentials).then(function(result) {
    			$rootScope.checkLoginState();
    			$state.go('home');
    		},
    		function(reason) {
    			$scope.errorText = reason.data.error;
    		});
    	}
    };

    musicRoom.controller('LoginController', loginController);
}());