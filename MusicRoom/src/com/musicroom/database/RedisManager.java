package com.musicroom.database;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisManager {

	private static JedisPool pool;

	public static Jedis getConnection() {
		if (pool == null) {

			pool = new JedisPool("localhost");
		}

		return pool.getResource();
	}

	public static void returnResource(Jedis resource) {
		try {
			pool.returnResource(resource);
		} catch (JedisConnectionException ex) {
			if (resource != null) {
				pool.returnBrokenResource(resource);
			}
		}
	}
}
