package com.dao.database.redis;

import com.dao.database.base.RedisInterface;
import com.logger.JLogger;
import com.utils.config.ConfigReader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ccheng
 *
 */
public class RedisDatabase implements RedisInterface {
	protected JedisPool connPool;
	protected int db;
	
	protected RedisDatabase() {
	}
	
	/**
	 * @param redisConfig 数据库配置
	 * @throws ClassNotFoundException
	 */
	public RedisDatabase(RedisDatabaseConfig redisConfig) {
		init(redisConfig);
	}

	/**
	 * @param reader
	 * @param field
	 */
	public RedisDatabase(ConfigReader reader, String field) {
		init(new RedisDatabaseConfig(reader, field));
	}

	protected void init(RedisDatabaseConfig redisConfig) {
		int redisEnabled = redisConfig.getEnable();
		if (redisEnabled == 1) {
			db = redisConfig.getDb();
			connPool = new JedisPool(redisConfig.getConfig(), redisConfig.getHost(), redisConfig.getPort(),
					redisConfig.getTimeout(), redisConfig.getPassword(), redisConfig.getUseSSL());
		} else {
			connPool = null;
		}
	}


	public boolean isOpen() {
		return connPool != null;
	}
	
	public Jedis getResource() {
		Jedis jedis_conn = connPool.getResource();
		jedis_conn.select(db);
		return jedis_conn;
	}
	
	/**
	 * 关闭statement, resultset, 将connection返回线程池
	 * 
	 * @param conn
	 * @param st
	 * @param res
	 */
	protected void safeClose(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception e) {
			JLogger.error("Failed to safeClose", e);
		}
	}

	/**
	 * 关闭statement, resultset, 将connection返回线程池
	 * 
	 * @param conn
	 * @param st
	 * @param res
	 */
	protected void safeDestory(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception e) {
			JLogger.error("Failed to safeDestory", e);
		}
	}

	@Override
	public void destory() {
		try {
			if (connPool != null) {
				connPool.close();
			}
		} catch (Exception e) {
			JLogger.error("Failed to close conntion pool", e);
		}
	}
}
