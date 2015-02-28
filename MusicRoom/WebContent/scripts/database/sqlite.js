"use strict";

(function()
{
	
var musicRoom = angular.module("musicRoom");

musicRoom.factory("$sqlite",
	function($q) {
		var isOpen = false;
		return {
			isOpen: isOpen,
			openDatabase: function(dbName, version, desc, size) {
				try {
					var db = openDatabase(dbName, version, desc, size);
					if (!window.openDatabase) {
						throw "Browser does not support sqlite";
					} else {
						isOpen = true;
					}
					return {
						db: db,
						executeQuery: function(query, values) {
							var deferred = $q.defer();
							db.transaction(function(tx) {
								tx.executeSql(query, values, function(tx, results) {
									deferred.resolve(results);
								}, function(tx, e){
									console.log("There has been an error: " + e.message);
									deferred.reject();
								});
							});
							return deferred.promise;
						},
						insert: function(tableName, fields, replace) {
							var query = (typeof replace === "boolean" && replace) ? "INSERT OR REPLACE" : "INSERT";
							query += " INTO {tableName} ({fields}) VALUES({values});";
							var queryFieldNames = "";
							var queryValues = "";
							var queryFields = [];
							for (var field in fields) {
								queryFieldNames += (Object.keys(fields)[Object.keys(fields).length - 1] == field) ? "`" + field + "`" : "`" + field + "`, ";
								queryValues += (Object.keys(fields)[Object.keys(fields).length - 1] == field) ? "?" : "?, ";
								queryFields.push(fields[field]);
							}
							return this.executeQuery(this.replace(query, {
								"{tableName}": tableName,
								"{fields}": queryFieldNames,
								"{values}": queryValues
							}), queryFields);
						},
						update: function(tableName, fields, whereClause) {
							var query = "UPDATE {tableName} SET {update} WHERE {where}; ";
							var queryValues = "";
							var queryFields = [];
							for (var field in fields) {
								queryValues += (Object.keys(fields)[Object.keys(fields).length - 1] == field) ? "`" + field + "`= ?" : "`" + field + "`= ?,";
								queryFields.push(fields[field]);
							}
							var whereResult = this.whereClause(whereClause);
							return this.executeQuery(this.replace(query, {
								"{tableName}": tableName,
								"{update}": queryValues,
								"{where}": whereResult.query
							}), queryFields.concat(whereResult.values));
						},
						del: function(tableName, whereClause) {
							if (whereClause){
								var query = "DELETE FROM {tableName} WHERE {where}; ";
								var whereResult = this.whereClause(whereClause);
							}else{
								var query = "DELETE FROM `{tableName}`;";
								var whereResult = {values : []};
							}
							return this.executeQuery(this.replace(query, {
								"{tableName}": tableName,
								"{where}": whereResult.query
							}), whereResult.values);
						},
						select: function(tableName, whereClause) {
							var query = "SELECT * FROM {tableName} WHERE {where}; ";
							var whereResult = this.whereClause(whereClause);
							return this.executeQuery(this.replace(query, {
								"{tableName}": tableName,
								"{where}": whereResult.query
							}), whereResult.values);
						},
						selectAll: function(tableName) {
							return this.executeQuery("SELECT * FROM " + tableName + "; ", []);
						},
						whereClause: function(fields) {
							var query = "";
							var values = [];
							for (var field in fields) {
								if(typeof fields[field] !== "undefined" && typeof fields[field] !== "object" && typeof fields[field] === "string" && !fields[field].match(/NULL/ig)) values.push(fields[field]);
								else if(typeof fields[field] !== "undefined" && typeof fields[field] !== "object" && typeof fields[field] === "number") values.push(fields[field]);
								else if(typeof fields[field]["value"] !== "undefined" && typeof fields[field] === "object" && (typeof fields[field]["value"] === "number" || !fields[field]["value"].match(/NULL/ig))) values.push(fields[field]["value"]);
								query += (typeof fields[field] === "object") ? 
												(typeof fields[field]["union"] === "undefined") ? 
													(typeof fields[field]["value"] === "string" && fields[field]["value"].match(/NULL/ig)) ? 
														field + " " + fields[field]["value"] : 
														(typeof fields[field]["operator"] !== "undefined")?
															field + fields[field]["operator"] + " ? " : 
															field + " = ?" : 
													(typeof fields[field]["value"] === "string" && fields[field]["value"].match(/NULL/ig)) ? 
															field + " " + fields[field]["value"] + " " + fields[field]["union"] + " " : 
															(typeof fields[field]["operator"] !== "undefined") ? 
																field + " " + fields[field]["operator"] + " ? " + fields[field]["union"] + " " : 
																field + " = ? " + fields[field]["union"] + " " : 
												(typeof fields[field] === "string" && fields[field].match(/NULL/ig)) ? 
													field + " " + fields[field] : 
													field + " = ?"
							}
							return {query:query,values:values};
						},
						replace: function(a, c) {
							for (var b in c) {
								a = a.replace(new RegExp(b, "ig"), c[b])
							}
							return a;
						},
						dropTable: function(tableName) {
							return this.executeQuery("DROP TABLE IF EXISTS " + tableName + "; ", []);
						},
					};
				} catch (err) {
					console.error(err);
				}
			}
		}
	}
);
})();