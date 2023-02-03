package com.utils;

import com.logger.JLogger;

import java.util.ArrayList;
import java.util.List;

public class JediFuncs {
	public static String formatSql(String sql, Object... args) {
		return String.format(sql, args);
	}
	public static String glueParams(char glueChar,Object ...args) {
		if (args == null || args.length == 0) {
			return "";
		}
		StringBuilder ret = new StringBuilder(String.valueOf(args[0]));
		for (int i = 1; i < args.length; i++) {
			ret.append(glueChar).append(String.valueOf(args[i]));
		}
		return ret.toString();
	}

	/**
	 * 去除尾数，只保留两位有效数字
	 * @param oriInt
	 * @return
	 */
	public static int removeTheMantissaInt(int oriInt) {
		int zeroNum = 0;
		while (oriInt > 100) {
			oriInt /= 10;
			zeroNum ++;
		}
		while (zeroNum>0) {
			oriInt *= 10;
			zeroNum --;
		}
		return oriInt;
	}
	/**
	 * 将字符串转换为纯数字的字符串，主要用于防止sql注入
	 */
	public static String toSqlIntStr(String sql) {
		if (sql.isEmpty()) {
			return "";
		}
		String [] sqlArr = sql.split("[,]");
		StringBuilder retSql = new StringBuilder();
		for (String element : sqlArr) {
			try {
				int intNum = JediCast.toInt(element);
				if (retSql.length()!=0) {
					retSql.append(",");
				}
				retSql.append(intNum);
			} catch (Exception e) {
				JLogger.error(String.format("error str : %s", sql), e);
			}
		}
		return retSql.toString();
	}

	/**
	 * 将字符串分隔成int的list
	 * 
	 * @param params
	 * @return
	 */
	public static List<Integer> toIntList(String params) {
		List<Integer> list = new ArrayList<>();
		if (params == null || params.isEmpty()) {
			return list;
		}
		String[] paramsArr = params.split("[,]");
		for (String element : paramsArr) {
			int intNum = JediCast.toInt(element);
			list.add(intNum);
		}
		return list;
	}
}