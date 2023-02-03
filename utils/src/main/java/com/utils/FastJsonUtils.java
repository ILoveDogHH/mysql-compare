package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FastJsonUtils {
	/**
	 * SerializerFeature.WriteNonStringKeyAsString
	 * 
	 * @param object
	 * @return
	 */
	public static String toJSONString(Object object) {
		return JSON.toJSONString(object, SerializerFeature.WriteNonStringKeyAsString,
				SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * dump
	 * 
	 * @param value
	 * @return
	 */
	public static String dump(Object value) {
		StringBuilder out = new StringBuilder();
		_dump(out, value);
		return out.toString();
	}

	private static final String INDENTATION_SIGN = "  ";

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 */
	private static void _appendIndentationSign(StringBuilder out, int indent, StringBuilder prefix) {
		for (int i = 0; i < indent; i++) {
			out.append(INDENTATION_SIGN);
		}
		_appendPrefix(out, prefix);
	}

	/**
	 * @param out
	 * @param prefix
	 */
	private static void _appendPrefix(StringBuilder out, StringBuilder prefix) {
		if (prefix != null) {
			out.append(prefix);
		}
	}

	/**
	 * @param out
	 * @param value
	 */
	private static void _dumpSimple(StringBuilder out, Object value) {
		if (value == null) {
			_dumpNull(out, 0, null);
		} else if (value instanceof String) {
			_dumpString(out, 0, null, (String) value);
		} else if (value instanceof Number) {
			_dumpNumber(out, 0, null, (Number) value);
		} else if (value instanceof Boolean) {
			_dumpBoolean(out, 0, null, (Boolean) value);
		} else {
			_dumpObject(out, 0, null, value);
		}
	}

	/**
	 * @param out
	 * @param value
	 */
	private static void _dump(StringBuilder out, Object value) {
		if (value == null) {
			_dumpNull(out, 0, null);
		} else if (value instanceof String) {
			_dumpString(out, 0, null, (String) value);
		} else if (value instanceof Number) {
			_dumpNumber(out, 0, null, (Number) value);
		} else if (value instanceof Boolean) {
			_dumpBoolean(out, 0, null, (Boolean) value);
		} else if (value instanceof List) {
			_dumpCollection(out, 0, null, (Collection<?>) value);
		} else if (value instanceof Map) {
			_dumpMap(out, 0, null, (Map<?, ?>) value);
		} else {
			_dumpObject(out, 0, null, value);
		}
	}
	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 */
	private static void _dumpNull(StringBuilder out, int indent, StringBuilder prefix) {
		_appendIndentationSign(out, indent, prefix);
		out.append("null");
	}

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 * @param value
	 */
	private static void _dumpString(StringBuilder out, int indent, StringBuilder prefix, String value) {
		_appendIndentationSign(out, indent, prefix);
		out.append("\"");
		value = value.replace("\0", "\\\\0");
		value = value.replace("\r", "\\r");
		value = value.replace("\n", "\\n");
		value = value.replace("\"", "\\\"");
		value = value.replace("\'", "\\\'");
		out.append(value);
		out.append("\"");
	}

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 * @param value
	 */
	private static void _dumpNumber(StringBuilder out, int indent, StringBuilder prefix, Number value) {
		_appendIndentationSign(out, indent, prefix);
		out.append(value);
	}

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 * @param value
	 */
	private static void _dumpBoolean(StringBuilder out, int indent, StringBuilder prefix, Boolean value) {
		_appendIndentationSign(out, indent, prefix);
		out.append(value);
	}

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 * @param value
	 */
	private static void _dumpObject(StringBuilder out, int indent, StringBuilder prefix, Object value) {
		_appendIndentationSign(out, indent, prefix);
		out.append("std::__class__").append(value.getClass()).append("__value__").append(value);
	}

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 * @param array
	 */
	private static void _dumpCollection(StringBuilder out, int indent, StringBuilder prefix, Collection<?> array) {
		_appendIndentationSign(out, indent, prefix);
		out.append("[");
		if (array.size() > 0) {
			out.append("\n");
			for (Object value : array) {
				if (value == null) {
					_dumpNull(out, indent + 1, null);
				} else if (value instanceof String) {
					_dumpString(out, indent + 1, null, (String) value);
				} else if (value instanceof Number) {
					_dumpNumber(out, indent + 1, null, (Number) value);
				} else if (value instanceof Boolean) {
					_dumpBoolean(out, indent + 1, null, (Boolean) value);
				} else if (value instanceof JSONArray) {
					_dumpCollection(out, indent + 1, null, (JSONArray) value);
				} else if (value instanceof JSONObject) {
					_dumpMap(out, indent + 1, null, (JSONObject) value);
				} else {
					_dumpObject(out, indent + 1, null, value);
				}
				out.append(",\n");
			}
			out.deleteCharAt(out.length() - 2);
			_appendIndentationSign(out, indent, null);
		}
		out.append("]");
	}

	/**
	 * @param out
	 * @param indent
	 * @param prefix
	 * @param object
	 */
	private static void _dumpMap(StringBuilder out, int indent, StringBuilder prefix, Map<?, ?> object) {
		_appendIndentationSign(out, indent, prefix);
		out.append("{");
		if (object.size() > 0) {
			out.append("\n");
			StringBuilder keyOut = new StringBuilder();
			for (Object key : object.keySet()) {
				keyOut.delete(0, keyOut.length());
				_dumpSimple(keyOut, key);
				keyOut.append(": ");
				Object value = object.get(key);
				if (value == null) {
					_dumpNull(out, indent + 1, keyOut);
				} else if (value instanceof String) {
					_dumpString(out, indent + 1, keyOut, (String) value);
				} else if (value instanceof Number) {
					_dumpNumber(out, indent + 1, keyOut, (Number) value);
				} else if (value instanceof Boolean) {
					_dumpBoolean(out, indent + 1, keyOut, (Boolean) value);
				} else if (value instanceof JSONArray) {
					_dumpCollection(out, indent + 1, keyOut, (JSONArray) value);
				} else if (value instanceof JSONObject) {
					_dumpMap(out, indent + 1, keyOut, (JSONObject) value);
				} else {
					_dumpObject(out, indent + 1, keyOut, value);
				}
				out.append(",\n");
			}
			out.deleteCharAt(out.length() - 2);
			_appendIndentationSign(out, indent, null);
		}
		out.append("}");
	}
}
