package com.utils;

import com.alibaba.fastjson.JSONArray;

import java.util.Collection;

public class PhpArrayUtils {
	public static boolean in_string(Object element, String value) {
		if (element == null || value.equals("")) {
			return false;
		}
		String strElement = element.toString();
		String[] elements = value.split("[,]");
		for (String e : elements) {
			if (strElement.equals(e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param arrays
	 * @return
	 */
	public static JSONArray array_merge(Collection<?>... arrays) {
		JSONArray ret = new JSONArray();
		for (Collection<?> array : arrays) {
			ret.addAll(array);
		}
		return ret;
	}
}
