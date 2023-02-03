package com.utils;

public class KeyGeneralUtil {

	/** 开始时间截 (2019-12-05) */
	private final static long timeSamp = 1575530440937L;


	/** 毫秒内序列(0~4095) */
	private static long sequence = 0L;

	/** 上次生成ID的时间截 */
	private static long lastTimestamp = -1L;

	/**
	 * 这里应该传入当前服务器id
	 */
	public static synchronized long nextId(int serverId) {
		long timestamp = System.currentTimeMillis();

		// 说明 时间发生了回退 这种情况是不应该发生的
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		// 说明这1s内发生了并发
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & 0x3ff;
			// 毫秒内序列溢出
			if (sequence == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		// 时间戳改变，毫秒内序列重置
		else {
			sequence = 0L;
		}

		// 上次生成ID的时间截
		lastTimestamp = timestamp;
		return (((timestamp - timeSamp) & 0xfffffffffL) << 27) // 占用63-27 占用36
				| (serverId & 0x3ff << 12) // 占15
				| sequence;// 占12
	}

	private static long tilNextMillis(long lastTimestamp) {
		long timestamp = System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}

}
