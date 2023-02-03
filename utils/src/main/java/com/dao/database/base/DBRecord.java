package com.dao.database.base;

import com.utils.JediCast;

import java.util.HashMap;
import java.util.Map;

public class DBRecord extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static boolean isDebugMode = false;
	
	private static final long serialVersionUID = -8025885102764594716L;
	
	public DBRecord(Map<String, Object> hashMap) {
		super(hashMap);
	}
	public DBRecord() {
		super();
	}
	
	public static void setDebugMode(boolean mode) {
		isDebugMode = mode;
	}
	
	/**
	 * 获取该列的值, 并强制转为int
	 * @param column
	 * @return
	 */
	public int getInt(String column){
		if (isDebugMode && this.get(column) == null) {
			throw new NullPointerException();
		}
		else if (!isDebugMode && this.get(column) == null) {
			//JLogger.debug("DBRecord NullPointerException:\n {}", ErrorMessage.getErrorDetail(new Exception("Stack trace")));
			//JLogger.info("DBRecord NullPointerException:\n {}", ErrorMessage.getErrorDetail(new Exception("Stack trace")));
		}
		
		return JediCast.toInt(this.get(column));
	}
	
	public int getInt(String column, int default_value){
		if (this.get(column) == null) {
			return default_value;
		}
		return JediCast.toInt(this.get(column));
	}
	
	/**
	 * 获取该列的值, 并强制转为double
	 * @param column
	 * @return
	 */
	public double getDouble(String column){
		return JediCast.toDouble(this.getString(column));
	}
	
	public double getDouble(String column, Double default_value){
		if (this.get(column) == null) {
			return default_value;
		}
		return JediCast.toDouble(this.getString(column));
	}
	
	/**
	 * 获取该列的值, 并强制转为long
	 * @param column
	 * @return
	 */
	public long getLong(String column){
		if (isDebugMode && this.get(column) == null) {
			throw new NullPointerException();
		}
		else if (!isDebugMode && this.get(column) == null) {
			//JLogger.debug("DBRecord NullPointerException:\n {}", ErrorMessage.getErrorDetail(new Exception("Stack trace")));
			//JLogger.info("DBRecord NullPointerException:\n {}", ErrorMessage.getErrorDetail(new Exception("Stack trace")));
		}
		return JediCast.toLong(this.get(column));
	}
	public long getLong(String column, long default_value){
		if (this.get(column) == null) {
			return default_value;
		}
		return JediCast.toLong(this.get(column));
	}
	/**
	 * 获取该列的值, 并强制转为String
	 * @param column
	 * @return
	 */
	public String getString(String column){
		if (isDebugMode && this.get(column) == null) {
			throw new NullPointerException();
		}
		else if (!isDebugMode && this.get(column) == null) {
			//JLogger.debug("DBRecord NullPointerException:\n {}", ErrorMessage.getErrorDetail(new Exception("Stack trace")));
			//JLogger.info("DBRecord NullPointerException:\n {}", ErrorMessage.getErrorDetail(new Exception("Stack trace")));
		}
		return JediCast.toString(this.get(column));
	}
	public String getString(String column, String default_value){
		if (this.get(column) == null) {
			return default_value;
		}
		
		return JediCast.toString(this.get(column));
	}
	
	@Override
	public Object put(String key,Object value){
	    return super.put(key, value);
	}
	public void putAll(DBRecord map){
	    super.putAll(map);
	}

	/**
	 * 拷贝一个map到一个新的DBRecord中.</br>
	 * 其中所有的key/value都会重新拷贝(浅拷贝)一次</br>
	 * 
	 * @param map
	 * @return
	 */
	public static DBRecord copyDBRecord(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		DBRecord result = new DBRecord();
		result.putAll(map);
		return result;
	}

	/**
	 * 拷贝一个map中部分key到一个新的DBRecord中.</br>
	 * 其中key/value都会重新拷贝(浅拷贝)一次</br>
	 * 
	 * @param map
	 * @return
	 */
	public static DBRecord copySubDBRecord(Map<String, Object> map, String... columns) {
		if (map == null) {
			return null;
		}
		DBRecord result = new DBRecord();
		for (String column : columns) {
			if (map.containsKey(column)) {
				result.put(column, map.get(column));
			}
		}
		return result;
	}
}

