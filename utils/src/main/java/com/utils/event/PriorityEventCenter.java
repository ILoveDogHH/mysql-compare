package com.utils.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityEventCenter implements EventCenter {
	private ConcurrentHashMap<String, CopyOnWriteArrayList<PriorityEventListener>> listeners = new ConcurrentHashMap<>();
	private AtomicInteger listenerIndex = new AtomicInteger(0);

	private class PriorityEventListener {
		/**
		 * 处理器名字
		 */
		private String name;
		/**
		 * 事件监听器
		 */
		private EventListener listener;
		/**
		 * 优先级
		 */
		private int priority;

		private PriorityEventListener(String name, EventListener listener, int priority) {
			this.name = name;
			this.listener = listener;
			this.priority = priority;
		}
	}

	@Override
	public String addEventListener(String eventName, EventListener listener) {
		return addEventListener(eventName, listener, 0);
	}

	/**
	 * 添加事件监听器
	 * 
	 * @param eventName
	 *            感兴趣的事件名称
	 * @param listener
	 *            监听器（回调）
	 * @param priority
	 *            监听器优先级，整型, 默认为0. 对于同一事件感兴趣的多个监听器，优先级越高（大），则越优先处理
	 * @return 处理器的名字
	 */
	public String addEventListener(String eventName, EventListener listener, int priority) {
		listeners.putIfAbsent(eventName, new CopyOnWriteArrayList<>());
		listenerIndex.incrementAndGet();
		String listenerName = "listener_" + listenerIndex.incrementAndGet();
		PriorityEventListener pListener = new PriorityEventListener(listenerName, listener, priority);
		// 监听器的一个table，里面加入不同监听器名称的结构体
		CopyOnWriteArrayList<PriorityEventListener> list = listeners.get(eventName);
		boolean found = false;
		// 按优先级排序，优先级越大，排列越靠前；同优先级则新加入的排列在最后
		for (int index = 0; index < list.size(); index++) {
			PriorityEventListener l = list.get(index);
			if (pListener.priority > l.priority) {
				found = true;
				// 在list指定index位置中插入值为d的元素 在一个监听器结构体中加入结构体d
				list.add(index, pListener);
				break;
			}
		}

		if (!found) {
			list.add(pListener);
		}

		return listenerName;
	}

	@Override
	public void dispatchEvent(String eventName, Event event) {
		if (!listeners.containsKey(eventName)) {
			return;
		}
		event.setName(eventName);

		for (PriorityEventListener v : listeners.get(eventName)) {
			v.listener.onListen(event);
			// 事件被吞没，不再向下传递
			if (event.isStop()) {
				break;
			}
		}
	}

	@Override
	public void removeEventListener(String eventName, String listenerName) {
		List<PriorityEventListener> list = listeners.get(eventName);
		if (listenerName == null || list == null) {
			return;
		}

		int found = -1;
		for (int k = 0; k < list.size(); k++) {
			PriorityEventListener l = list.get(k);
			if (listenerName.equals(l.name)) {
				found = k;
				break;
			}
		}

		if (found >= 0) {
			list.remove(found);
		}
	}

	@Override
	public void removeAllEventListenersForEvent(String eventName) {
		listeners.remove(eventName);
	}

	@Override
	public void removeAllEventListeners() {
		listeners.clear();
		listenerIndex.set(0);
	}
}
