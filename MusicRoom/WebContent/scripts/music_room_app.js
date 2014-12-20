'use strict';

(function() {
    var musicRoom = angular.module('musicRoom', ['restangular', 'ui.router', 'musicRoom']);
    musicRoom.config(function($stateProvider, $urlRouterProvider){
    	$urlRouterProvider.otherwise('');
    	
        $stateProvider
        	.state('home', {
                url: "home",
                views: {
                    "view": {
                    	templateUrl: "pages/main_page.htm"
                    }
                }
        	})
            .state('bands', {
                url: "bands",
                views: {
                    "view": {
                    	templateUrl: "templates/band_list.htm",
                		controller: "BandListController"
                    }
                }
            })
            .state('studios', {
                url: "studios",
                views: {
                    "view": {
                    	templateUrl: "templates/studio_list.htm",
                		controller: "StudioListController"
                    }
                }
            });
        });
}());