'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var bandDetailsController = function($scope, Restangular, decodeService, $stateParams) {

		var bandService = Restangular.one("bands", $stateParams.id);

		bandService.get().then(
				function(band) {
					$scope.band = band;

					angular.forEach(band.band_members, function(bandMember,
							innerIndex) {
						bandMember.instrumentObjs = [];

						angular.forEach(bandMember.instruments, function(
								instrument, key) {
							var bm = bandMember;

							decodeService.getDecodeWithParameters(
									decodeService.decodeTypes.equipmentTypes, {
										"id" : instrument
									}, function(result) {
										bm.instrumentObjs.push(result[0]);
									});
						});
					});
				});
	};

	musicRoom.controller('BandDetailsController', bandDetailsController);
}());