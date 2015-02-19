'use strict';

var openDB = function (){
    try{
         if(window.openDatabase){
	         var shortName = 'musicRoomDB';
	         var version = '1.0';
	         var displayName = 'musicRoomDB';
	         var maxSize = 65536; // in bytes
	         db = openDatabase(shortName, version, displayName, maxSize);
	         
	         // test query to check if the db exists. if not - it goes to the error callback and creates it
	         executeQuery('select * from MY_DATA', function(){}, createDB, true);
         }
    }
    catch(e){
         alert(e);
    }
}

var createDB = function(){
	db.transaction(function(tx){
		tx.executeSql('CREATE TABLE AREAS('
					+ 'ID 			INTEGER PRIMARY KEY,'
					+ 'AREA_NAME 	TEXT NOT NULL);',[],function(tx,result){},function(tx,error){});
		
		
		tx.executeSql('CREATE TABLE CITIES('
					+ 'ID           INTEGER PRIMARY KEY,'
					+ 'CITY_NAME   	TEXT NOT NULL,'
					+ 'AREA_ID      INTEGER NOT NULL,'
					+ 'Foreign Key (AREA_ID) references AREAS(ID) on delete cascade);',[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE USER_TYPES('
					+ 'ID 			INTEGER PRIMARY KEY,'
					+ 'DESCRIPTION 	TEXT NOT NULL);',[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE MY_DATA('
					+ 'MY_ID 			INTEGER,'
					+ 'MY_NAME 			TEXT NOT NULL,'
					+ 'MY_PASSWORD 		TEXT NOT NULL,'
					+ 'MY_USER_TYPE_ID	INTEGER NOT NULL,'
					+ 'BAND_NAME		TEXT NOT NULL,'
					+ 'BAND_LOGO_URL	TEXT NOT NULL,'
					+ 'BAND_GENRE		TEXT NOT NULL,'
					+ 'STUDIO_ID 		INTEGER NOT NULL,'
					+ 'Foreign Key (USER_TYPE_ID) references USER_TYPES(ID) on delete cascade,',
					[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE ROOM_TYPES('
					+ 'ID 				INTEGER PRIMARY KEY,'
					+ 'ROOM_TYPE_NAME 	TEXT NOT NULL);',[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE EQUIPMENT_CATEGORIES('
					+ 'ID 				INTEGER PRIMARY KEY,'
					+ 'EQUIP_CAT_NAME 	TEXT NOT NULL);',[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE EQUIPMENT_TYPES('
					+ 'ID 				INTEGER PRIMARY KEY,'
					+ 'EQUIPMENT_NAME 	TEXT NOT NULL,'
					+ 'CATEGORY_ID 		INTEGER NOT NULL,'
					+ 'Foreign Key (CATEGORY_ID) references EQUIPMENT_CATEGORIES(ID) on delete cascade);',
					[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE STUDIOS('
					+ 'ID 				INTEGER PRIMARY KEY,'
					+ 'STUDIO_NAME	 	TEXT NOT NULL,'
					+ 'CITY_ID	 		INTEGER NOT NULL,'
					+ 'ADDRESS	 		TEXT NOT NULL,'
					+ 'EMAIL	 		TEXT NOT NULL,'
					+ 'CONTACT_NAME		TEXT NOT NULL,'
					+ 'SITE_URL			TEXT NOT NULL,'
					+ 'FACEBOOK_PAGE	TEXT NOT NULL,'
					+ 'PHONE			TEXT NOT NULL,'
					+ 'USER_ID			INTEGER NOT NULL,'
					+ 'EXTRA_DETAILS	TEXT NOT NULL,'
					+ 'LOGO_URL			TEXT NOT NULL,'
					+ 'LAST_UPDATE 		INTEGER NOT NULL'
					+ 'AVG_RATING 		REAL NOT NULL'
					+ 'Foreign Key (CITY_ID) references CITIES(ID) on delete cascade,',
					[],function(tx,result){},function(tx,error){});

		tx.executeSql('CREATE TABLE ROOMS('
				+ '    ID 				INTEGER PRIMARY KEY,'
				+ '	   STUDIO_ID	 	INTEGER NOT NULL,'
				+ '	   RATE		 		INTEGER NOT NULL,'
				+ '	   ROOM_NAME 		TEXT NOT NULL,'
				+ '	   EXTRA_DETAILS	TEXT NOT NULL,'
				+ '	   Foreign Key (STUDIO_ID) references STUDIOS(ID) on delete cascade);',
				[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE ROOM_ROOM_TYPES('
				+ '    ROOM_ID		INTEGER NOT NULL,'
				+ '	   TYPE_ID	 	INTEGER NOT NULL,'
				+ '	   PRIMARY KEY (ROOM_ID, TYPE_ID),'
				+ '	   Foreign Key (ROOM_ID) references ROOMS(ID) on delete cascade,'
				+ '	   Foreign Key (TYPE_ID) references ROOM_TYPES(ID) on delete cascade);',
				[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE BANDS('
				+ '    ID			INTEGER PRIMARY KEY,'
				+ '	   BAND_NAME 	TEXT NOT NULL,'
				+ '	   LOGO_URL	 	TEXT NOT NULL,'
				+ '	   GENRE	 	TEXT NOT NULL,'
				,[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE BAND_MEMBERS('
				+ '    ID			INTEGER PRIMARY KEY,'
				+ '	   MEMBER_NAME 	TEXT NOT NULL,'
				+ '	   ROLE		 	TEXT NOT NULL,'
				+ '	   PICTURE_URL 	TEXT NOT NULL,'
				+ '	   BAND_ID	 	INTEGER NOT NULL,'
				+ '	   Foreign Key (BAND_ID) references BAND_MEMBERS(ID) on delete cascade);', +
				[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE MEMBER_INSTRUMENT('
				+ '    MEMBER_ID			INTEGER NOT NULL,'
				+ '	   EQUIPMENT_TYPE_ID 	INTEGER NOT NULL,'
				+ '	   PRIMARY KEY (MEMBER_ID, EQUIPMENT_TYPE_ID),'
				+ '	   Foreign Key (EQUIPMENT_TYPE_ID) references EQUIPMENT_TYPES(ID) on delete cascade,'
				+ '	   Foreign Key (MEMBER_ID) references BAND_MEMBERS(ID) on delete cascade);'
				,[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE ROOM_EQUIPMENT('
				+ '    ROOM_ID				INTEGER NOT NULL,'
				+ '	   EQUIPMENT_TYPE_ID 	INTEGER NOT NULL,'
				+ '	   MODEL	 			TEXT NOT NULL,'
				+ '	   MANUFACTURER			TEXT NOT NULL,'
				+ '	   QUANTITY	 			INTEGER NOT NULL,'
				+ '	   PRIMARY KEY (ROOM_ID, EQUIPMENT_TYPE_ID),'
				+ '	   Foreign Key (EQUIPMENT_TYPE_ID) references EQUIPMENT_TYPES(ID) on delete cascade,'
				+ '	   Foreign Key (ROOM_ID) references ROOMS(ID) on delete cascade);'
				,[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE ROOM_SCHEDULE('
				+ '    ROOM_ID				INTEGER NOT NULL,'
				+ '	   BAND_ID			 	INTEGER NOT NULL,'
				+ '	   START_TIME 			INTEGER NOT NULL,'
				+ '	   END_TIME				INTEGER NOT NULL,'
				+ '	   PRIMARY KEY (ROOM_ID, START_TIME, END_TIME),'
				+ '	   Foreign Key (BAND_ID) references BANDS(ID) on delete cascade,'
				+ '	   Foreign Key (ROOM_ID) references ROOMS(ID) on delete cascade);',
				[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE TABLE MY_QUERIES('
				+ '    QUERY				TEXT PRIMARY KEY,'
				+ '	   LAST_SERVER_CHECK 	INTEGER NOT NULL);',
				[],function(tx,result){},function(tx,error){});
		
		tx.executeSql('CREATE INDEX STD_CT_IDX 		ON STUDIOS 			(CITY_ID)',[],function(tx,result){},function(tx,error){});
		tx.executeSql('CREATE INDEX CT_AREA_IDX 	ON CITIES 			(AREA_ID)',[],function(tx,result){},function(tx,error){});
		tx.executeSql('CREATE INDEX STD_NAME_IDX 	ON STUDIOS 			(STUDIO_NAME)',[],function(tx,result){},function(tx,error){});
		tx.executeSql('CREATE INDEX ROM_STD_IDX 	ON ROOMS 			(STUDIO_ID)',[],function(tx,result){},function(tx,error){});
		tx.executeSql('CREATE INDEX ROM_RATE_IDX 	ON ROOMS 			(RATE)',[],function(tx,result){},function(tx,error){});
		tx.executeSql('CREATE INDEX EQP_CAT_IDX 	ON EQUIPMENT_TYPES 	(CATEGORY_ID)',[],function(tx,result){},function(tx,error){});
		
		populateDB();
	});
}

var populateDB = function(){
	
	Restangular.setBaseUrl("rest/");
    var areasService = Restangular.all("areas");
    
    areasService.getList().then(function(areas) {
    	for (var i = 0; i < areas.length; i++) {
    		executeQuery("insert into AREAS values " + areas[i].id +"," + areas[i].name, null, null, true);
    		
    	    var citiesService = Restangular.all("cities", areas[i].id);
    	    
    	    citiesService.getList().then(function(cities) {
    	    	for (var j = 0; j < cities.length; j++) {
    	    		executeQuery("insert into CITIES values " + cities[j].id +"," + cities[j].name + ',' + areas[i].id, null, null, true);
    	    	}
    	    });
    	}
    });
    
    var room_typesService = Restangular.all("room_types");
    
    room_typesService.getList().then(function(types) {
    	for (var i = 0; i < types.length; i++) {
    		executeQuery("insert into ROOM_TYPES values " + types[i].id +"," + types[i].name, null, null, true);
    	}
    });
    
    var user_typesService = Restangular.all("user_types");
    
    user_typesService.getList().then(function(types) {
    	for (var i = 0; i < types.length; i++) {
    		executeQuery("insert into USER_TYPES values " + types[i].id +"," + types[i].name, null, null, true);
    	}
    });
    
    var equipment_categoriesService = Restangular.all("equipment_categories");
    
    equipment_categoriesService.getList().then(function(cats) {
    	for (var i = 0; i < cats.length; i++) {
    		executeQuery("insert into EQUIPMENT_CATEGORIES values " + cats[i].id +"," + cats[i].name, null, null, true);
    		
    	    var equipment_typesService = Restangular.all("equipment_types", cats[i].id);
    	    
    	    equipment_typesService.getList().then(function(types) {
    	    	for (var j = 0; j < types.length; j++) {
    	    		executeQuery("insert into EQUIPMENT_TYPES values " + types[j].id +"," + types[j].name + ',' + cats[i].id, null, null, true);
    	    	}
    	    });
    	}
    });
}

var executeQuery = function (query,callback,callbackerror,isOpen){
	try {
		if (!isOpen) {
			openDB();
		}
		
        if(window.openDatabase){
        	db.transaction(function(tx){
        		tx.executeSql(query,[],
        			function(tx,result){
	        			if(typeof(callback) == "function"){
	        				callback(result);
	        			}
	        			else if(callback != undefined){
	        				eval(callback+"(result)");
	        			}
	        		},
        			function(tx,error){
	        			if(typeof(callbackerror) == "function"){
	        				callbackerror(error);
	        			}
	        			else if(callbackerror != undefined){
	        				eval(callbackerror+"(error)");
	        			}
        			});
        	});
        }
    }catch(e){}
}

var musicRoom = angular.module("musicRoom");

musicRoom.factory("db_service",[function(){

	var factory = {};
		
	return factory;
	
}]);