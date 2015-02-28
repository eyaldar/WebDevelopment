(function() {
	var musicRoom = angular.module("musicRoom");

	musicRoom.factory("decodeService", function(localDbManager, $sqlite) {

		return {
			decodeTypes : {
				areas : "areas",
				cities : "cities",
				userTypes : "user_types",
				roomTypes : "room_types",
				equipmentCategories : "equipment_categories",
				equipmentTypes : "equipment_types",
			},
			getDecode : function(decodeType, callback, failure) {
				localDbManager.dbService.selectAll(decodeType).then(
						function(result) {
							items = localDbManager.fetchAll(result)
							callback(items);
						}, failure);
			},
			getDecodeWithParameters : function(decodeType, params, callback,
					failure) {

				localDbManager.dbService.select(decodeType, params).then(
						function(result) {
							items = localDbManager.fetchAll(result)
							callback(items);
						}, failure);
			}
		};
	});
})();