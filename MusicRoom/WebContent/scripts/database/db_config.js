angular.module('musicRoom.config', [])
.constant('DB_CONFIG', {
    name: 'musicRoomDB',
    tables: [
        {
        	name: 'db_metadata', 
        	columns: [
        	    {name: 'id', type: 'integer not null'},    
        	    {name: 'last_update', type: 'text not null'},
            ],
    		primaryKeys: ['id'],
			foreignKeys: []
        },
		{
            name: 'areas',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'name', type: 'text not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: []
        },
		{
            name: 'cities',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'name', type: 'text not null'},
				{name: 'area_id', type: 'integer not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: [
				{ field: 'area_id', parentTable: 'areas', foreignKey: 'id' }
			]
        },
		{
            name: 'room_types',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'name', type: 'text not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: []
        },
		{
            name: 'user_types',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'description', type: 'text not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: []
        },
		{
            name: 'equipment_categories',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'name', type: 'text not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: []
        },
		{
            name: 'equipment_types',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'name', type: 'text not null'},
				{name: 'category_id', type: 'integer not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: [
				{ field: 'category_id', parentTable: 'equipment_categories', foreignKey: 'id' }
			]
        },
		{
            name: 'my_data',
            columns: [
                {name: 'user_id', type: 'integer'},
                {name: 'name', type: 'text not null'},
				{name: 'user_type_id', type: 'integer not null'},
				{name: 'last_update', type: 'text not null'}
            ],
			primaryKeys: [],
			foreignKeys: [
				{ field: 'user_type_id', parentTable: 'user_types', foreignKey: 'id' }
			]
        },
		{
            name: 'studios',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'studio_name', type: 'text not null'},
				{name: 'city_id', type: 'integer not null'},
				{name: 'address', type: 'text not null'},
				{name: 'email', type: 'text not null'},
				{name: 'contact_name', type: 'text not null'},
				{name: 'site_url', type: 'text not null'},
				{name: 'facebook_page', type: 'text not null'},
				{name: 'phone', type: 'text not null'},
				{name: 'extra_details', type: 'text not null'},
				{name: 'logo_url', type: 'text not null'},
				{name: 'avg_rating', type: 'real not null'},
				{name: 'last_update', type: 'text not null'}
            ],
			primaryKeys: ['id'],
			foreignKeys: [
				{ field: 'city_id', parentTable: 'cities', foreignKey: 'id' }
			]
        },
		{
            name: 'rooms',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'studio_id', type: 'integer not null'},
				{name: 'rate', type: 'integer not null'},
				{name: 'room_name', type: 'text not null'},
				{name: 'extra_details', type: 'text not null'},
				{name: 'last_update', type: 'text not null'}
            ],
			primaryKeys: ['id'],
			foreignKeys: [
				{ field: 'studio_id', parentTable: 'studios', foreignKey: 'id' }
			]
        },
		{
            name: 'room_room_types',
            columns: [
                {name: 'room_id', type: 'integer key'},
                {name: 'type_id', type: 'integer not null'},
            ],
			primaryKeys: ['room_id', 'type_id'],
			foreignKeys: [
				{ field: 'room_id', parentTable: 'rooms', foreignKey: 'id' },
				{ field: 'type_id', parentTable: 'room_types', foreignKey: 'id' }
			]
        },
		{
            name: 'bands',
            columns: [
                {name: 'id', type: 'integer not null'},
				{name: 'band_name', type: 'text not null'},
                {name: 'logo_url', type: 'text not null'},
				{name: 'genre', type: 'text not null'},
				{name: 'last_update', type: 'timestamp not null'}
            ],
			primaryKeys: ['id'],
			foreignKeys: []
        },
		{
            name: 'band_members',
            columns: [
                {name: 'id', type: 'integer not null'},
                {name: 'member_name', type: 'text not null'},
				{name: 'role', type: 'text not null'},
				{name: 'pictue', type: 'text not null'},
				{name: 'band_id', type: 'integer not null'},
            ],
			primaryKeys: ['id'],
			foreignKeys: [
				{ field: 'band_id', parentTable: 'bands', foreignKey: 'id' }
			]
        },
		{
            name: 'member_instruments',
            columns: [
                {name: 'member_id', type: 'integer not null'},
                {name: 'equipment_type_id', type: 'integer not null'},
            ],
			primaryKeys: ['member_id', 'equipment_type_id'],
			foreignKeys: [
				{ field: 'member_id', parentTable: 'band_members', foreignKey: 'id' },
				{ field: 'equipment_type_id', parentTable: 'equipment_types', foreignKey: 'id' }
			]
        },
		{
            name: 'room_equipment',
            columns: [
                {name: 'room_id', type: 'integer not null'},
                {name: 'equipment_type_id', type: 'integer not null'},
				{name: 'model', type: 'text not null'},
				{name: 'manufacturer', type: 'text not null'},
				{name: 'quantity', type: 'integer not null'},
            ],
			primaryKeys: ['room_id', 'equipment_type_id'],
			foreignKeys: [
				{ field: 'room_id', parentTable: 'rooms', foreignKey: 'id' },
				{ field: 'equipment_type_id', parentTable: 'equipment_types', foreignKey: 'id' }
			]
        },
		{
            name: 'room_schedule',
            columns: [
                {name: 'room_id', type: 'integer not null'},
				{name: 'band_id', type: 'integer not null'},
                {name: 'band_name', type: 'text not null'},
				{name: 'start_time', type: 'text not null'},
				{name: 'end_time', type: 'text not null'},
            ],
			primaryKeys: ['room_id', 'start_time', 'end_time'],
			foreignKeys: [
				{ field: 'band_id', parentTable: 'bands', foreignKey: 'id' },
				{ field: 'room_id', parentTable: 'rooms', foreignKey: 'id' }
			]
        },
    ],
    indices: {
		studiosCities : 'CREATE INDEX STD_CT_IDX ON STUDIOS (CITY_ID)',
		areasCities : 'CREATE INDEX CT_AREA_IDX ON CITIES (AREA_ID)',
		studioName : 'CREATE INDEX STD_NAME_IDX ON STUDIOS (STUDIO_NAME)',
		roomsStudios : 'CREATE INDEX ROM_STD_IDX ON ROOMS (STUDIO_ID)',
		roomRate : 'CREATE INDEX ROM_RATE_IDX ON ROOMS (RATE)',
		equipmentTypesCategories : 'CREATE INDEX EQP_CAT_IDX ON EQUIPMENT_TYPES (CATEGORY_ID)'
    }
});