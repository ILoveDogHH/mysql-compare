package com.utils.event;

import com.alibaba.fastjson.JSONObject;

public class Event {
	private String _name;
	private boolean _isStop = false;
	private JSONObject _data;

	public Event(JSONObject data) {
		_data = data;
	}

	/**
	 * 获取事件名字
	 * 
	 * @return
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 设置事件名
	 * 
	 * @param name
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * 吞没事件, 不再传递下去
	 */
	public void stop() {
		_isStop = true;
	}

	/**
	 * 该时间是否已经被吞没
	 * 
	 * @return
	 */
	public boolean isStop() {
		return _isStop;
	}


	/**
	 * 获取数据
	 * 
	 * @return
	 */
	public JSONObject getData() {
		return _data;
	}
}
