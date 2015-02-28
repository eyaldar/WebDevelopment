(function() {
	var musicRoom = angular.module("musicRoom");

	musicRoom.service("myDataService", function(localDbManager, Restangular) {
		var self = this;

		var tableName = "my_data";
		var userStateService = Restangular.one("users");

		var getStateFromServer = function(callback, failure) {
			userStateService.one("state").get().then(function(result) {
				self.updateState(result, null, null);

				if (typeof callback === "function") {
					callback(result);
				}
			}, function(result) {

				if (typeof callback === "function") {
					failure(result);
				}
			});
		}

		self.getState = function(wasChanged, callback, failure) {
			if (localDbManager.isOpen && !wasChanged) {
				localDbManager.dbService.selectAll(tableName).then(
						function(result) {
							if (result.rows.length > 0) {
								var state = localDbManager.fetch(result);

								var now = new Date();
								var lastUpdate = new Date(state.last_update);

								// Check whether last update is less than half an hour
								if (((now - lastUpdate) / (3600 * 1000)) > 0.5) {
									getStateFromServer(callback, failure);
								} else {
									state.logged = (state.is_logged === "true") ? true
											: false;
									if (state.logged) {
										state.user = {
											id : state.user_id,
											user_type_id : state.user_type_id
										};
									}

									if (typeof callback === "function") {
										callback(state);
									}
								}
							} else {
								getStateFromServer(callback, failure);
							}
						}, function(result) {
							console.log(result);
						});
			} else {
				getStateFromServer(callback, failure)
			}
		};

		self.updateState = function(state) {
			if (localDbManager.isOpen) {

				if (!state.logged) {
					state.user = {
						id : null,
						user_type_id : 1
					};
				}

				localDbManager.dbService.insert('my_data', {
					id : 1,
					is_logged : state.logged,
					user_id : state.user.id,
					name : state.name,
					user_type_id : state.user.user_type_id,
					last_update : (new Date()).format("yyyy-mm-dd HH:MM:ss")
				}, true);
			}
		};
	});
})();