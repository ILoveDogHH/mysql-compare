package com.utils.language;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangMark {
    private List<Object> args;
    private String key;
    private static Pattern pattern = Pattern.compile("LangKey\\((.+?)\\)_LangArgs\\((.*)\\)");
    /**
     * @param lang, json格式
     */
    public LangMark(String lang){
    	Matcher matcher = pattern.matcher(lang);
    	if(matcher.find()&&matcher.groupCount()>=2){
    		key=matcher.group(1);
            String[] localArgs=matcher.group(2).split(",");
            args = new ArrayList<>();
            for (String localArg : localArgs) {
            	args.add(localArg);
            }
    	}
		else {
			try {
				JSONObject text = JSON.parseObject(lang);
				if (text == null) {
					throw new JSONException(lang + " is not jsonstring");
				}
				if (!text.containsKey("langKey")) {
					throw new JSONException(lang + "do not contains key[langKey]");
				}
				key = text.getString("langKey");
				args = JSONArray2List(text.getJSONArray("langArgs"));
			} catch (JSONException | IllegalArgumentException | NullPointerException e) {
				key = lang;
				args = new ArrayList<>();
			}
    	}
    }
    
    
	public List<Object> getArgs() {
		return args;
	}


	public void setArgs(List<Object> args) {
		this.args = args;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	private List<Object> JSONArray2List(JSONArray array) {
		if (array == null) {
			return new ArrayList<>();
		}
    	return array.toJavaList(Object.class);
    }
	
    public static boolean isSame(String lang){
    	Matcher matcher = pattern.matcher(lang);
    	if(matcher.find()&&matcher.groupCount()>=2){
    		String key=matcher.group(1);
    		boolean equals = StringUtils.equals(key, lang);
          return equals;
    	}
		else {
			try {
				JSONObject text = JSON.parseObject(lang);
				if (text == null) {
					throw new JSONException(lang + " is not jsonstring");
				}
				if (!text.containsKey("langKey")) {
					throw new JSONException(lang + "do not contains key[langKey]");
				}
				String key = text.getString("langKey");
				boolean equals = StringUtils.equals(key, lang);
				return equals;
			} catch (JSONException | IllegalArgumentException | NullPointerException e) {
				return true;
			}
    	}
    }
}