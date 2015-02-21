package com.musicroom.database;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

	private static JedisPool pool;

	public static Jedis getConnection() {
		if (pool == null) {
			pool = new JedisPool("localhost");
		}

		return pool.getResource();
	}
	
	public static void returnResource(Jedis resource) {
		pool.returnResource(resource);
	}
}
