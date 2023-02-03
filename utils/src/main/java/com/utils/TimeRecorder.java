package com.utils;

import java.util.ArrayList;
import java.util.List;

public class TimeRecorder {
	private static class Record {
		long time;
		String msg;

		Record(long time, String msg) {
			this.time = time;
			this.msg = msg;
		}
	}

	private List<Record> records = new ArrayList<>();

	public TimeRecorder() {
	}

	/**
	 * 开始
	 */
	public void start() {
		start("");
	}

	/**
	 * 开始
	 * 
	 * @param msg
	 */
	public void start(String msg) {
		records.clear();
		records.add(new Record(TimeManager.getSystemTimestampLong(), msg));
	}

	/**
	 * 打点
	 */
	public void clock() {
		clock("");
	}

	/**
	 * 打点
	 * 
	 * @param msg
	 */
	public void clock(String msg) {
		records.add(new Record(TimeManager.getSystemTimestampLong(), msg));
	}

	/**
	 * 打印结果
	 * 
	 * @param showLine
	 *            是否显示行号
	 */
	public void print(boolean showLine) {
		if (records.size() == 0) {
			System.out.println("TimeRecorder has not started");
			return;
		}
		Record startRecord = records.get(0);
		long lastTime = startRecord.time;
		System.out.println((showLine ? ("[0]<start, ") : "<start, ") + startRecord.time + ">" + startRecord.msg);
		for (int i = 1; i < records.size(); i++) {
			Record record = records.get(i);
			System.out.println(
					(showLine ? ("[" + i + "]<") : "<") + (record.time - lastTime) + ", " + record.time + ">"
							+ record.msg);
			lastTime = record.time;
		}
	}
}
