package com.dao.database.mysql;

import com.alibaba.fastjson.JSONArray;
import com.dao.database.mysql.MysqlDatabaseInArrayObject.MysqlDatabaseInObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MysqlDatabaseInArrayObject extends ArrayList<MysqlDatabaseInObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 89272584684022153L;

	public static class MysqlDatabaseInObject extends JSONArray {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7507320958509201700L;

		public MysqlDatabaseInObject() {
			super();
		}

		public MysqlDatabaseInObject(Object... objects) {
			super();
			for (Object object : objects) {
				add(object);
			}
		}
	}

	public MysqlDatabaseInArrayObject(List<MysqlDatabaseInObject> list) {
		super(new ArrayList<>(list));
	}

	public MysqlDatabaseInArrayObject(MysqlDatabaseInObject... list) {
		super(new ArrayList<>(Arrays.asList(list)));
	}
}
