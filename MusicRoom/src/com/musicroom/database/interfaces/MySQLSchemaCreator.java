package com.musicroom.database.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface MySQLSchemaCreator {
	void createDB(Connection connection) throws SQLException;
	
	MySQLDataBasePopulator getPopulator();
}
