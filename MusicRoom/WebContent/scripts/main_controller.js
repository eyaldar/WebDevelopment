'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    var mainController = function($rootScope, $scope, Restangular, $state) {
    	$rootScope.loggedUser = null;
    	
        Restangular.setBaseUrl("rest/");
        var userStateService = Restangular.one("users");
        
        $rootScope.checkLoginState = function() {
        	
	        userStateService.one("state").get().then(function(result) {
	        	console.log(result);
	        	$rootScope.loggedUser = result.loggedUser;
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
    };

    musicRoom.controller('MainController', mainController);
}());