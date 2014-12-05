package com.musicroom.database.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface MySQLDataBasePopulator {
	void initDBData(Connection connection) throws SQLException;
}
