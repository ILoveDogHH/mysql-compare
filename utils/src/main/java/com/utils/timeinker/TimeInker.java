package com.utils.timeinker;

import com.utils.TimeManager;

public abstract class TimeInker<T> {
	protected String name;
	protected T out;
	protected long lastTime;
	protected long times;

	public TimeInker(String name, T out) {
		this.name = name;
		this.out = out;
		reset("start");
	}

	protected abstract void println(String message);

	private void reset(String message) {
		this.lastTime = TimeManager.getSystemTimestampLong();
		this.times = 0;
		if (out != null) {
			println(String.format("[%s]======%s======", name, message));
		}
	}

	public void reset() {
		reset("reset");
	}

	/**
	 * 打点
	 */
	public void dot() {
		dot("");
	}

	/**
	 * 打点
	 * 
	 * @param message
	 */
	public void dot(String message) {
		long curtime = TimeManager.getSystemTimestampLong();
		this.times++;
		if (out != null) {
			println(String.format("[%s] dots=%d, duration=%d, message=%s", name, this.times, curtime - this.lastTime,
					message));
		}
		this.lastTime = TimeManager.getSystemTimestampLong();
	}
}
