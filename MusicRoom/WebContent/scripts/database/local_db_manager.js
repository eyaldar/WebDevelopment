'use strict';

(function() {

	var musicRoom = angular.module("musicRoom");

	musicRoom
			.service(
					'localDbManager',
					function(DB_CONFIG, Restangular, $sqlite, $q) {
						var self = this;

						var createDB = function() {
							angular.forEach(DB_CONFIG.tables, function(table) {
								var columns = [];
								var foreignKeys = [];
								var foreignKeySection = '';
								var primaryKeySection = '';

								angular.forEach(table.columns,
										function(column) {
											columns.push(column.name + ' '
													+ column.type);
										});

								angular.forEach(table.foreignKeys, function(
										foreignKeyData) {
									foreignKeys.push('Foreign Key ('
											+ foreignKeyData.field
											+ ') references '
											+ foreignKeyData.parentTable + '('
											+ foreignKeyData.foreignKey
											+ ') on delete cascade');
								});

								if (foreignKeys.length > 0) {
									foreignKeySection = ',' + foreignKeySection
											+ foreignKeys.join(',');
								}

								if (table.primaryKeys.length > 0) {
									primaryKeySection = ',' + ' primary key '
											+ '(' + table.primaryKeys.join(',')
											+ ')';
								}

								var query = 'CREATE TABLE IF NOT EXISTS '
										+ table.name + ' (' + columns.join(',')
										+ primaryKeySection + foreignKeySection
										+ ')';

								self.query(query);
								console.log('Table ' + table.name
										+ ' initialized');
							});

							angular.forEach(DB_CONFIG.indices, function(index) {
								self.query(index);
							});
						}
						
						var initDatabase = function(shouldCreate) {
							if(shouldCreate) {
								createDB();
							}
							
							populateDecodeTables();
							
							var lastUpdate = new Date();
							self.dbService.insert('db_metadata', {
								id: 1, 
								last_update: lastUpdate.format("yyyy-mm-dd HH:MM:ss")
							}, true);
						}
						
						self.init = function() {
							self.dbService = $sqlite.openDatabase(
									DB_CONFIG.name, '1.0', DB_CONFIG.name,
									65536);
							self.db = self.dbService.db;
							
							// Check whether db initialization is required.
							self.query("SELECT * FROM db_metadata", []).then(function(result) {
								var metadata = self.fetch(result);
								var now = new Date();
								var lastUpdate = new Date(metadata.last_update);
								
								// if the last populate occured more than 24 hours ago.
								if(((now - lastUpdate) / (3600*1000)) > 24) {
									initDatabase(false)
								}
							}, function(result) {
								initDatabase(true);
							});
						};

						self.query = function(query, bindings) {
							bindings = typeof bindings !== 'undefined' ? bindings
									: [];
							var deferred = $q.defer();

							self.db.transaction(function(transaction) {
								transaction.executeSql(query, bindings,
										function(transaction, result) {
											deferred.resolve(result);
										}, function(transaction, error) {
											deferred.reject(error);
										});
							});

							return deferred.promise;
						};

						self.fetchAll = function(result) {
							var output = [];

							for (var i = 0; i < result.rows.length; i++) {
								output.push(result.rows.item(i));
							}

							return output;
						};

						self.fetch = function(result) {
							return result.rows.item(0);
						};

						var populateDecodeTables = function() {

							var areasService = Restangular.all("areas");

							areasService
									.getList()
									.then(
											function(areas) {

												for (var i = 0; i < areas.length; i++) {
													var areasFields = {};
													areasFields["id"] = areas[i].id;
													areasFields["name"] = areas[i].name;
													self.dbService.insert(
															'areas',
															areasFields,
															true);

													var citiesService = Restangular
															.several("cities",
																	areas[i].id);

													citiesService
															.getList()
															.then(
																	function(
																			cities) {
																		for (var j = 0; j < cities.length; j++) {
																			var citiesFields = {};
																			citiesFields["id"] = cities[j].id;
																			citiesFields["name"] = cities[j].name;
																			citiesFields["area_id"] = cities[j].area_id;
																			self.dbService
																					.insert(
																							'cities',
																							citiesFields, true);
																		}
																	});
												}
											});

							var roomTypesService = Restangular
									.all("room_types");

							roomTypesService
									.getList()
									.then(
											function(types) {
												for (var i = 0; i < types.length; i++) {
													var roomTypesFields = {};
													roomTypesFields["id"] = types[i].id;
													roomTypesFields["name"] = types[i].name;
													self.dbService.insert(
															'room_types',
															roomTypesFields,
															true);
												}
											});

							var userTypesService = Restangular
									.all("user_types");

							userTypesService
									.getList()
									.then(
											function(types) {
												for (var i = 0; i < types.length; i++) {
													var userTypesFields = {};
													userTypesFields["id"] = types[i].id;
													userTypesFields["description"] = types[i].name;
													self.dbService.insert(
															'user_types',
															userTypesFields,
															true);
												}
											});

							var equipmentCategoriesService = Restangular
									.all("equipment_categories");

							equipmentCategoriesService
									.getList()
									.then(
											function(cats) {
												for (var i = 0; i < cats.length; i++) {
													var categoriesFields = {};
													categoriesFields["id"] = cats[i].id;
													categoriesFields["name"] = cats[i].name;
													self.dbService
															.insert(
																	'equipment_categories',
																	categoriesFields,
																	true);

													var equipmentTypesService = Restangular
															.several(
																	"equipment_types",
																	cats[i].id);

													equipmentTypesService
															.getList()
															.then(
																	function(
																			types) {
																		for (var j = 0; j < types.length; j++) {
																			var typesFields = {};
																			typesFields["id"] = types[j].id;
																			typesFields["name"] = types[j].name;
																			typesFields["category_id"] = types[j].category_id;
																			self.dbService
																					.insert(
																							'equipment_types',
																							typesFields,
																							true);
																		}
																	});
												}
											});
						};
					});
})();