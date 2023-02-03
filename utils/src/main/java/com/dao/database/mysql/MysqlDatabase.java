package com.dao.database.mysql;

import com.dao.base.DaoException;
import com.dao.database.base.DBInterface;
import com.dao.database.base.DBInterfaceException;
import com.dao.database.base.DBRecord;
import com.logger.JLogger;
import com.utils.JediCast;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cc
 *
 */
public class MysqlDatabase implements DBInterface {
	protected ObjectPool<Connection> connPool;
	protected String dbName;
	protected String ip;
	protected int port;

	protected MysqlDatabase() {
	}
	
	/**
	 * @param mysqlConfig 数据库配置
	 * @throws ClassNotFoundException
	 */
	public MysqlDatabase(MysqlDatabaseConfig mysqlConfig) throws ClassNotFoundException {
		init(mysqlConfig);
	}
    
	protected void init(MysqlDatabaseConfig mysqlDatabaseConfig) throws ClassNotFoundException {
    	this.dbName = mysqlDatabaseConfig.getDbName();
    	this.ip = mysqlDatabaseConfig.getIp();
    	this.port = mysqlDatabaseConfig.getPort();
    	String url=mysqlDatabaseConfig.getUrl();
    	PoolableObjectFactory<Connection> mySqlPoolableObjectFactory = new MySqlPoolableObjectFactory(url);
    	GenericObjectPoolFactory<Connection> genericObjectPoolFactory = new GenericObjectPoolFactory<>(mySqlPoolableObjectFactory, mysqlDatabaseConfig.getPoolConfig());
    	ObjectPool<Connection> pool = genericObjectPoolFactory.createPool();
    	connPool = pool;
    }
    
    @Override
	public String getDBName() {
		return this.dbName;
	}
    
	/**
	 * 数据库select操作, 获取多条数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select * from table where col1=?,
	 *            col2=?)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public List<DBRecord> sql_fetch_rows(String sql, Object... msgs) throws DaoException {
        List<DBRecord> ret = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            ResultSetMetaData md = res.getMetaData();
            String cols_name;
            while (res.next()) {
                DBRecord data = new DBRecord();
                // 遍历此行的所有列
                for (int i = 0; i < md.getColumnCount(); i++) {
                	// 取出列名
                    cols_name = md.getColumnLabel(i + 1);
                    Object val = res.getObject(cols_name);
                    data.put(cols_name, val);
                }
                ret.add(data);
            }
        } catch (SQLException e) {
			throw new DaoException(String.format("%s:%d",ip, port),e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }

        return ret;
    }
	
    /**
	 * 数据库select操作, 获取多条数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select * from table where col1=?,
	 *            col2=?)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public List<Integer> sql_fetch_rows_int(String sql, Object... msgs) throws DaoException {
        List<Integer> ret = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            while (res.next()) {
               // 遍历此行的所有列
            	Object val = res.getObject(1);
            	ret.add(Integer.valueOf(String.valueOf(val)));
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }

        return ret;
    }
	

    /**
	 * 数据库select操作, 获取多条数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select * from table where col1=?, col2=?
	 *            limit 1)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public DBRecord sql_fetch_one(String sql, Object... msgs) throws DaoException {
        DBRecord data = new DBRecord();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            ResultSetMetaData md = res.getMetaData();
            String cols_name;
            if (res.next()) {
            	// 遍历此行的所有列
                for (int i = 0; i < md.getColumnCount(); i++)  {
                	// 取出列名
                    cols_name = md.getColumnLabel(i + 1); 
                    Object val = res.getObject(cols_name);
                    data.put(cols_name, val);
                }
                return data;
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }

        return data;
    }
    
    /**
	 * 数据库select操作, 返回单行的指定列数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select col1 from table where col1=?,
	 *            col2=? limit 1)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public Object sql_fetch_one_cell(String sql, Object... msgs) throws DaoException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            if (res.next()) {
                Object val = res.getObject(1);
                return val;
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
        return null;
    }
    
    /**
	 * 数据库select操作, 返回单行的指定列数据, 并转为String类型数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select col1 from table where col1=?,
	 *            col2=? limit 1)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public String sql_fetch_one_cell_string(String sql, Object... msgs) throws DaoException {
    	Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            if (res.next()) {
                Object val = res.getObject(1);
                return JediCast.toString(val);
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
        return "";
    }
    
    /**
	 * 数据库select操作, 返回单行的指定列数据, 并转为int类型数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select col1 from table where col1=?,
	 *            col2=? limit 1)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public int sql_fetch_one_cell_int(String sql, Object... msgs) throws DaoException {
    	Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            if (res.next()) {
                Object val = res.getObject(1);
                return JediCast.toInt(val);
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
        return 0;
    }

	/**
	 * 数据库select操作, 返回单行的指定列数据, 并转为int类型数据
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select col1 from table where col1=?,
	 *            col2=? limit 1)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public long sql_fetch_one_cell_long(String sql, Object... msgs) throws DaoException {
    	Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            if (res.next()) {
                Object val = res.getObject(1);
                return JediCast.toLong(val);
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
        return 0L;
    }
	
    /**
	 * 数据库update操作, 返回执行的行数
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:update table set col1=? where col1=?,
	 *            col2=?)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public int sql_update(String sql, Object... msgs) throws DaoException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql(), Statement.RETURN_GENERATED_KEYS);
            st = formatPrepareStatement(st, params.getMsgs());
        	return st.executeUpdate();
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
    }

    /**
	 * 数据库insert操作, 返回插入之后生成的key值
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:insert table (col1, col2) values (?, ?))
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public int sql_insert(String sql, Object... msgs) throws DaoException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql(), Statement.RETURN_GENERATED_KEYS);
            st = formatPrepareStatement(st, params.getMsgs());
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
				int id = rs.getInt(1);
                return id;
            } else {
                throw new DBInterfaceException("sql_insert has no autoincreament key id!", null);
            }
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
    }

    /**
	 * 检测执行之后是否有数据, 会实际执行
	 * 
	 * @param sql
	 *            要执行的语句. 所有可变参数替换为? (如:select col1 from table where col1=?,
	 *            col2=? limit 1)
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	@Override
	public boolean sql_check(String sql, Object... msgs) throws DaoException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet res = null;
        try {
            MysqlDatabaseParameter params = formatSqlParams(sql, msgs);
            conn = connPool.borrowObject();
            st = conn.prepareStatement(params.getSql());
            st = formatPrepareStatement(st, params.getMsgs());
            res = st.executeQuery();
            res.last();
            return res.getRow() > 0;
        } catch (SQLException e) {
			throw new DaoException(e);
        } catch (Exception e) {
            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
        } finally {
			safeClose(conn, st, res);
        }
    }
	
	/**
	 * 创建更新用的batch
	 * 
	 * @param sql
	 * @return
	 * @throws DaoException
	 */
	public MysqlDatabaseUpdateBatch createNoAutoExecuteUpdateBatch(String sql)
			throws DaoException {
		return new MysqlDatabaseUpdateBatch(sql, MysqlDatabaseUpdateBatch.DO_NOT_EXECUTE_AUTO);
	}

	/**
	 * 更新用的batch
	 *
	 */
	public class MysqlDatabaseUpdateBatch {
		public static final int DO_NOT_EXECUTE_AUTO = -1;
		public static final int DEFAULT_AUTO_EXECUTE_NUMBER = 5000;
		public int limit;
		private int currentLine;
		String sql;
		PreparedStatement st = null;
		Connection conn = null;
		private boolean autoCommit;

		private MysqlDatabaseUpdateBatch(String sql, int limit) throws DaoException {
			this.sql = sql;
			this.limit = limit;
			currentLine = 0;
			try {
				conn = connPool.borrowObject();
				autoCommit = conn.getAutoCommit();
				conn.setAutoCommit(false);
				st = conn.prepareStatement(sql);
			} catch (SQLException e) {
				throw new DaoException(e);
	        } catch (Exception e) {
	            throw new DBInterfaceException("Failed to borrow connection from the pool", e);
			}
		}

		/**
		 * 增加一条batch, 如果数量超出自动执行的数量, 会自动执行一次
		 * 
		 * @param msgs
		 * @throws DaoException
		 */
		public void addBatch(List<Object> msgs) throws DaoException {
			try {
				formatPrepareStatement(st, msgs);
				st.addBatch();
			} catch (SQLException e) {
				throw new DaoException(e);
			} catch (Exception e) {
				throw new DBInterfaceException("Failed to borrow connection from the pool", e);
			}
			try {
				currentLine++;
				if (limit != DO_NOT_EXECUTE_AUTO && currentLine >= limit) {
					execute();
				}
			} catch (DaoException e) {
				throw e;
			}
		}

		/**
		 * 执行
		 * 
		 * @return
		 * 
		 * @throws DaoException
		 */
		public int[] execute() throws DaoException {
			int ret[] = new int[0];
			if (currentLine > 0) {
				try {
					ret = st.executeBatch();
					st.clearBatch();
					conn.commit();
				} catch (SQLException e) {
					throw new DaoException(e);
				} catch (Exception e) {
					throw new DBInterfaceException("Failed to borrow connection from the pool", e);
				}
				currentLine = 0;
			}
			return ret;
		}

		public void close() {
			try {
				conn.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				JLogger.error("set autocommit error ", e);
			}
			safeClose(conn, st, null);
		}
	}

	@Override
	public int[] batchUpdate(String sql, List<List<Object>> listMsgs) throws DaoException {
		MysqlDatabaseUpdateBatch batch = null;
		int[] ret = new int[0];
		try {
			batch = createNoAutoExecuteUpdateBatch(sql);
			for (List<Object> msgs : listMsgs) {
				batch.addBatch(msgs);
			}
			ret = batch.execute();
		} catch (DaoException e) {
			throw e;
		} finally {
			if (batch != null) {
				batch.close();
			}
		}
		return ret;
	}

	/**
	 * 将sql和params转为MysqlDatabaseParameter
	 * 
	 * @param sql
	 * @param msgs
	 * @return
	 */
	protected MysqlDatabaseParameter formatSqlParams(String sql, Object... msgs) {
		MysqlDatabaseParameter param = new MysqlDatabaseParameter(sql, new ArrayList<>(Arrays.asList(msgs)));
		param.format();
		return param;
	}
	
	/**
	 * setObject
	 * 
	 * @param st
	 * @param msgs
	 * @return
	 * @throws DaoException
	 */
	protected PreparedStatement formatPrepareStatement(PreparedStatement st, List<Object> msgs) throws DaoException {
		int i=1;
		// 分类型插入
		try {
		for(Object msg:msgs){
			st.setObject(i++, msg);
    	}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		return st;
	}
	
	/**
	 * 关闭statement, resultset, 将connection返回线程池
	 * 
	 * @param conn
	 * @param st
	 * @param res
	 */
	protected void safeClose(Connection conn, Statement st, ResultSet res) {
		try {
			if (res != null && !res.isClosed()) {
				res.close();
			}
			if (st != null && !st.isClosed()) {
				st.close();
			}
			if (conn != null && !conn.isClosed()) {
				connPool.returnObject(conn);
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
	protected void safeDestory(Connection conn, Statement st, ResultSet res) {
		try {
			if (res != null && !res.isClosed()) {
				res.close();
			}
			if (st != null && !st.isClosed()) {
				st.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
			connPool.invalidateObject(conn);
		} catch (Exception e) {
			JLogger.error("Failed to safeClose", e);
		}
	}

	@Override
	public void destory() {
		try {
			connPool.close();
		} catch (Exception e) {
			JLogger.error("Failed to close conntion pool", e);
		}
	}
}
