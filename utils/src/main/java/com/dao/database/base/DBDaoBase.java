package com.dao.database.base;

import com.dao.base.DaoBase;
import com.dao.base.DaoException;

import java.util.List;

/**
 * @author cc
 *
 */
public class DBDaoBase implements DaoBase {
	private DBInterface db;
	
	public DBDaoBase(DBInterface db) {
		this.db=db;
	}
	
	public DBDaoBase(DBDaoBase dao) {
		db=dao.db;
	}
	
	/**
	 * 获取数据库名，用于log
	 * @return
	 */
	public final String getDBName() {
		return db.getDBName();
	}
	
	/**
	 * 数据库select操作, 获取多条数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final List<DBRecord> sql_fetch_rows(String sql, Object... msgs) throws DaoException {
		return db.sql_fetch_rows(sql, msgs);
	}
	
	/**
	 * 数据库select操作, 获取多条数据, 并将每行第一列数据转为int返回
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final List<Integer> sql_fetch_rows_int(String sql, Object... msgs)
			throws DaoException {
		return db.sql_fetch_rows_int(sql, msgs);
	}

    /**
	 * 数据库select操作, 获取多条数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final DBRecord sql_fetch_one(String sql, Object... msgs) throws DaoException {
		return db.sql_fetch_one(sql, msgs);
	}
	
	/**
	 * 数据库select操作, 返回单行的指定列数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final Object sql_fetch_one_cell(String sql, Object... msgs) throws DaoException {
		return db.sql_fetch_one_cell(sql, msgs);
	}
	
    /**
	 * 数据库select操作, 返回单行的指定列数据, 并转为String类型数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final String sql_fetch_one_cell_string(String sql, Object... msgs) throws DaoException {
		return db.sql_fetch_one_cell_string(sql, msgs);
	}
	
    /**
	 * 数据库select操作, 返回单行的指定列数据, 并转为int类型数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final int sql_fetch_one_cell_int(String sql, Object... msgs) throws DaoException {
		return db.sql_fetch_one_cell_int(sql, msgs);
	}
	
	/**
	 * 数据库select操作, 返回单行的指定列数据, 并转为int类型数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final long sql_fetch_one_cell_long(String sql, Object... msgs) throws DaoException {
		return db.sql_fetch_one_cell_long(sql, msgs);
	}
	
    /**
	 * 数据库update操作, 返回执行的行数
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final Integer sql_update(String sql, Object... msgs) throws DaoException {
		return db.sql_update(sql, msgs);
	}
	
    /**
	 * 数据库insert操作, 返回插入之后生成的key值
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final Integer sql_insert(String sql, Object... msgs) throws DaoException {
		return db.sql_insert(sql, msgs);
	}
	
    /**
	 * 检测执行之后是否有数据, 会实际执行
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	protected final boolean sql_check(String sql, Object... msgs) throws DaoException {
		return db.sql_check(sql, msgs);
	}
	
	protected final int[] batchUpdate(String sql, List<List<Object>> listMsgs)
			throws DaoException {
		return db.batchUpdate(sql, listMsgs);
	}

	@Override
	public void destory() {
		db.destory();
	}
}
