package com.dao.database.redis;

import com.utils.config.ConfigReader;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

// 目前使用默认设置，先测，待完善
public class RedisDatabaseConfig {
	private int enable;
	private String host;
	private int port;
	private int db;
	private String password = null;
	private boolean useSSL = false;
	private int timeout = Protocol.DEFAULT_TIMEOUT;

	private JedisPoolConfig config;

	public RedisDatabaseConfig(ConfigReader reader, String name) {
		enable = reader.getInt(name + ".enable");
		host = reader.getString(name + ".host");
		port = reader.getInt(name + ".port");
		db = reader.getInt(name + ".db");
		if (reader.containsKey(name + ".password")) {
			password = reader.getString(name + ".password");
		}
		if (reader.containsKey(name + ".timeout")) {
			timeout = reader.getInt(name + ".timeout");
		}
		if (reader.containsKey(name + ".useSSL")) {
			useSSL = reader.getBoolean(name + ".useSSL");
		}
		config = getDefaultConfig();
		if (reader.getBoolean(name + ".config")) {
			if (reader.containsKey(name + ".config.blockWhenExhausted")) {
				config.setBlockWhenExhausted(reader.getBoolean(name + ".config.blockWhenExhausted"));
			}
			if (reader.containsKey(name + ".config.evictionPolicyClassName")) {
				config.setEvictionPolicyClassName(reader.getString(name + ".config.evictionPolicyClassName"));
			}
			if (reader.containsKey(name + ".config.fairness")) {
				config.setFairness(reader.getBoolean(name + ".config.fairness"));
			}
			if (reader.containsKey(name + ".config.jmxEnabled")) {
				config.setJmxEnabled(reader.getBoolean(name + ".config.jmxEnabled"));
			}
			if (reader.containsKey(name + ".config.jmxNameBase")) {
				config.setJmxNameBase(reader.getString(name + ".config.jmxNameBase"));
			}
			if (reader.containsKey(name + ".config.jmxNamePrefix")) {
				config.setJmxNamePrefix(reader.getString(name + ".config.jmxNamePrefix"));
			}
			if (reader.containsKey(name + ".config.lifo")) {
				config.setLifo(reader.getBoolean(name + ".config.lifo"));
			}
			if (reader.containsKey(name + ".config.maxIdle")) {
				config.setMaxIdle(reader.getInt(name + ".config.maxIdle"));
			}
			if (reader.containsKey(name + ".config.maxTotal")) {
				config.setMaxTotal(reader.getInt(name + ".config.maxTotal"));
			}
			if (reader.containsKey(name + ".config.maxWaitMillis")) {
				config.setMaxWaitMillis(reader.getLong(name + ".config.maxWaitMillis"));
			}
			if (reader.containsKey(name + ".config.minEvictableIdleTimeMillis")) {
				config.setMinEvictableIdleTimeMillis(reader.getLong(name + ".config.minEvictableIdleTimeMillis"));
			}
			if (reader.containsKey(name + ".config.minIdle")) {
				config.setMinIdle(reader.getInt(name + ".config.minIdle"));
			}
			if (reader.containsKey(name + ".config.numTestsPerEvictionRun")) {
				config.setNumTestsPerEvictionRun(reader.getInt(name + ".config.numTestsPerEvictionRun"));
			}
			if (reader.containsKey(name + ".config.softMinEvictableIdleTimeMillis")) {
				config.setSoftMinEvictableIdleTimeMillis(
						reader.getLong(name + ".config.softMinEvictableIdleTimeMillis"));
			}
			if (reader.containsKey(name + ".config.testOnBorrow")) {
				config.setTestOnBorrow(reader.getBoolean(name + ".config.testOnBorrow"));
			}
			if (reader.containsKey(name + ".config.testOnCreate")) {
				config.setTestOnCreate(reader.getBoolean(name + ".config.testOnCreate"));
			}
			if (reader.containsKey(name + ".config.testOnReturn")) {
				config.setTestOnReturn(reader.getBoolean(name + ".config.testOnReturn"));
			}
			if (reader.containsKey(name + ".config.testWhileIdle")) {
				config.setTestWhileIdle(reader.getBoolean(name + ".config.testWhileIdle"));
			}
			if (reader.containsKey(name + ".config.timeBetweenEvictionRunsMillis")) {
				config.setTimeBetweenEvictionRunsMillis(reader.getLong(name + ".config.timeBetweenEvictionRunsMillis"));
			}
		}
	}

	public int getEnable() {
		return enable;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

	public int getDb() {
		return db;
	}

	public int getTimeout() {
		return timeout;
	}

	public boolean getUseSSL() {
		return useSSL;
	}

	public JedisPoolConfig getConfig() {
		return config;
	}

	/**
	 * 默认的config
	 * 
	 * @return
	 */
	private static JedisPoolConfig getDefaultConfig() {
		return new JedisPoolConfig();
	}
}
