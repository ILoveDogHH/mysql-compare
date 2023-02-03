package com.dao.database.base;

import com.dao.base.DaoException;

import java.util.List;

public interface DBInterface {
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
	List<DBRecord> sql_fetch_rows(String sql, Object... msgs) throws DaoException;
	
	/**
	 * 数据库select操作, 获取多条数据Int型数据
	 * 
	 * @param sql
	 *            要执行的语句.
	 * @param msgs
	 *            参数列表
	 * @return
	 * @throws DaoException
	 */
	List<Integer> sql_fetch_rows_int(String sql, Object... msgs) throws DaoException;

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
	DBRecord sql_fetch_one(String sql, Object... msgs) throws DaoException;
	
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
	Object sql_fetch_one_cell(String sql, Object... msgs) throws DaoException;
    
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
	String sql_fetch_one_cell_string(String sql, Object... msgs) throws DaoException;
    
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
	int sql_fetch_one_cell_int(String sql, Object... msgs) throws DaoException;
	
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
	long sql_fetch_one_cell_long(String sql, Object... msgs) throws DaoException;
    
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
	int sql_update(String sql, Object... msgs) throws DaoException;
    
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
	int sql_insert(String sql, Object... msgs) throws DaoException;
    
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
	boolean sql_check(String sql, Object... msgs) throws DaoException;

	/**
	 * 批量更新数据
	 * 
	 * @param sql
	 * @param listMsgs
	 * @return
	 * @throws DaoException
	 */
	int[] batchUpdate(String sql, List<List<Object>> listMsgs) throws DaoException;
	/**
	 * 获取数据库名，用于log
	 * @return
	 */
	String getDBName();
	
	/**
	 * 关闭数据库
	 */
	void destory();
}
