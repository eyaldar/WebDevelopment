'use strict';

(function() {
    var musicRoom = angular.module('musicRoom', ['restangular', 'ui.router', 'musicRoom']);
    musicRoom.config(function($stateProvider, $urlRouterProvider){
    	
    	
    	
    	$urlRouterProvider.otherwise('/home');
    	
        $stateProvider
        	.state('home', {
                url: "/home",
                views: {
                    "view": {
                    	templateUrl: "pages/main_page.htm"
                    }
                }
        	})
            .state('bands', {
                url: "/bands",
                views: {
                    "view": {
                    	templateUrl: "pages/bands.htm",
                		controller: "BandListController"
                    }
                }
            })
            .state('studios', {
                url: "/studios",
                views: {
                    "view": {
                    	templateUrl: "pages/studios.htm",
                		controller: "StudioListController"
                    }
                }
            })            
            .state('login', {
                url: "/login",
                views: {
                    "view": {
                    	templateUrl: "pages/login.htm",
                		controller: "LoginController"
                    }
                }
            });
        });
}());