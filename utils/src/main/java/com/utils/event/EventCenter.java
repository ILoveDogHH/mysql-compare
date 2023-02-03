package com.utils.event;

public interface EventCenter {
	/**
	 * 添加事件监听器
	 * 
	 * @param eventName
	 *            感兴趣的事件名称
	 * @param listener
	 *            监听器（回调）
	 * @return 监听器的名字
	 */
	String addEventListener(String eventName, EventListener listener);

	/**
	 * 分发事件
	 * 
	 * @param eventName
	 * @param event
	 */
	public void dispatchEvent(String eventName, Event event);

	/**
	 * 移除某个eventName的单个监听器
	 * 
	 * @param eventName
	 * @param listenerName
	 */
	public void removeEventListener(String eventName, String listenerName);

	/**
	 * 移除这个eventName的事件监听器
	 * 
	 * @param eventName
	 */
	public void removeAllEventListenersForEvent(String eventName);

	/**
	 * 移除所有事件监听器
	 */
	public void removeAllEventListeners();
}
