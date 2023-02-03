package com.utils;
//不需要翻译的创建工厂

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class LangFactory {
	

	public static String text(String key,List<Object> args)
	{
	  return toMarkText(key, args);
	}
	public static String toMarkText(String key, List<Object> args) {
		JSONObject obj = new JSONObject();
		obj.put("langKey", key);
		obj.put("langArgs", args);
		return FastJsonUtils.toJSONString(obj);
	}
}
