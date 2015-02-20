'use strict';

(function() {
	var musicRoom = angular.module('musicRoom', [ 'restangular', 'ui.router',
			'ui.bootstrap', 'ngMessages', 'ngAnimate', 'musicRoom' ]);
	musicRoom.config(function($stateProvider, $urlRouterProvider) {

		$urlRouterProvider.otherwise('/home');

		var bandsState = {
			url : "/bands",
			views : {
				"view" : {
					templateUrl : "pages/bands.htm",
					controller : "BandListController"
				}
			}
		};

		$stateProvider.state('home', {
			url : "/home",
			views : {
				"view" : {
					templateUrl : "pages/main_page.htm"
				}
			}
		}).state('bands', bandsState).state('bandDetails', {
			url : "/band/:id",
			views : {
				"view" : {
					templateUrl : "pages/band_details.htm",
					controller : "BandDetailsController"
				}
			}
		}).state('studios', {
			url : "/studios",
			views : {
				"view" : {
					templateUrl : "pages/studios.htm",
					controller : "StudioListController"
				}
			}
		}).state('login', {
			url : "/login",
			views : {
				"view" : {
					templateUrl : "pages/login.htm",
					controller : "LoginController"
				}
			}
		}).state('signup', {
			url : "/signup",
			views : {
				"view" : {
					templateUrl : "pages/signup.htm"
				}
			}
		}).state('bandSignup', {
			url : "/bandSignup",
			views : {
				"view" : {
					templateUrl : "pages/bandSignup.htm",
					controller : "BandSignupController"
				}
			}
		}).state('studioSignup', {
			url : "/studioSignup",
			views : {
				"view" : {
					templateUrl : "pages/studioSignup.htm",
					controller : "StudioSignupController"
				}
			}
		}).state('roomDetails', {
			url : "/studios/:studioId/:id",
			views : {
				"view" : {
					templateUrl : "pages/room_details.htm",
					controller : "RoomDetailsController",
				}
			}
		}).state('studioDetails', {
			url : "/studios/:id",
			views : {
				"view" : {
					templateUrl : "pages/studio_details.htm",
					controller : "StudioDetailsController"
				}
			}
		});
	});
}());