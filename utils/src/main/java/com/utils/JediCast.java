package com.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dao.database.base.DBRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author cc
 * 专门用来处理类型转换的
 *
 */
public class JediCast {
	/**
	 * 强制转为int
	 * @param obj
	 * @return
	 */
	public static int toInt (Object obj) {
        if(obj == null) {
            return 0;
        } 
        if (obj instanceof String) {
        	if (((String)obj).isEmpty()) {
				return 0;
			}
            return Integer.parseInt((String) obj);
        }else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return (int) obj;
    }
	
    /** 
     * 强制转为long
     * @param obj
     * @return
     */
    public static long toLong (Object obj) {
		if(obj == null) {
			return 0L;
		}else if(obj instanceof Number) {
			return ((Number) obj).longValue();
		}else if(obj instanceof String) {
			return Long.parseLong((String) obj);
		}
		return (long) obj;
    }
    
    /**
     * 强制转为double
     * @param obj
     * @return
     */
    public static double toDouble (Object obj) {
		if(obj == null) {
			return 0D;
		}else if(obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}else if(obj instanceof String) {
			return Double.parseDouble((String) obj);
		}
		return (double) obj;
    }

    /**
     * 强制转为boolean
     * @param obj
     * @return
     */
    public static boolean toBoolean (Object obj) {
        if(obj == null) {
            return false;
        }else if(obj instanceof Boolean) {
            return ((Boolean) obj) ? true : false;
        }else if(obj instanceof String) {
            return Boolean.parseBoolean((String) obj);
        }
        return (boolean) obj;
    }
    
    /**
     * 强制转为String
     * @param obj
     * @return
     */
    public static String toString (Object obj) {
    	if(obj == null) {
			return "";
    	}
		return String.valueOf(obj);
    }
    
    /**
     * 把JSONArray转化为List<DBRecord>
     * @param jsonArr
     * @return
     * @throws JSONException
     */
    public static List<DBRecord> JSONArrayToListDBRecord(JSONArray jsonArr) throws JSONException{
        List<DBRecord> list=new ArrayList<>();
        for(int i=0;i<jsonArr.size();i++){
            JSONObject jsObj=jsonArr.getJSONObject(i);
            DBRecord record=new DBRecord();
			record.putAll(jsObj);
			list.add(record);
        }
        return list;
    }
    
	/**
	 * 把JSONArray转化为List<DBRecord>
	 * 
	 * @param jsonArr
	 * @return
	 * @throws JSONException
	 */
	public static List<HashMap<String, Object>> JSONArrayToListHashMap(JSONArray jsonArr) throws JSONException {
		List<HashMap<String, Object>> list = new ArrayList<>();
		for (int i = 0; i < jsonArr.size(); i++) {
			JSONObject jsObj = jsonArr.getJSONObject(i);
			HashMap<String, Object> record = new HashMap<>();
			record.putAll(jsObj);
			list.add(record);
		}
		return list;
	}

	/**
	 * 把一个JSONObject转为DBRecord
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 */
    public static DBRecord JSONObjectToDBRecord(JSONObject jsonObj) throws JSONException{
    	DBRecord record=new DBRecord();
		record.putAll(jsonObj);
        return record;
    }
    
    
    public static List<Object> stringToList(String str){
    	if(str.isEmpty()) {
    		return new ArrayList<>();
    	}
    	String[] strs = str.split(",");
    	List<Object> list = new ArrayList<>();
    	for(String s : strs) {
    		list.add(s);
    	}
    	return list;
    }
    
    public static List<Integer> stringToIntList(String str){
    	if(str.isEmpty()) {
    		return new ArrayList<>();
    	}
    	String[] strs = str.split(",");
    	List<Integer> list = new ArrayList<>();
    	for(String s : strs) {
    		list.add(Integer.parseInt(s));
    	}
    	return list;
    }
    
    public static List<Integer> stringToIntList(String str, String regex){
    	if(str.isEmpty() || regex.isEmpty()) {
    		return new ArrayList<>();
    	}
    	String[] strs = str.split(regex);
    	List<Integer> list = new ArrayList<>();
    	for(String s : strs) {
    		list.add(Integer.parseInt(s));
    	}
    	return list;
    }
    
}
