(function() {
	var musicRoom = angular.module("musicRoom");

	musicRoom.factory("decodeService", function(localDbManager, Restangular) {

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
				if (localDbManager.isOpen) {
					localDbManager.dbService.selectAll(decodeType).then(
							function(result) {
								items = localDbManager.fetchAll(result)
								callback(items);
							}, failure);
				} else {
					Restangular.all(decodeType).getList().then(
							function(result) {
								callback(result);
							});
				}
			},
			getDecodeWithParameters : function(decodeType, params, callback,
					failure) {
				if (localDbManager.isOpen) {
					localDbManager.dbService.select(decodeType, params).then(
							function(result) {
								items = localDbManager.fetchAll(result)
								callback(items);
							}, failure);
				} else {

					for ( var prop in params) {
						if (params.hasOwnProperty(prop)) {
							Restangular.several(decodeType, params[prop])
									.getList().then(function(result) {
										callback(result);
									});
						}
					}

				}
			}
		};
	});
})();