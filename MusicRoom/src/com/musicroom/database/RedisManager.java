package com.musicroom.database;

import redis.clients.jedis.Jedis;

public class RedisManager {

	private static Jedis connection;

	public static Jedis getConnection() {
		if (connection == null) {
			connection = new Jedis("localhost");
		}

		return connection;
	}
}
