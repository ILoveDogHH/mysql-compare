package mysql_comparer;

import com.logger.JLogger;

public class Logger {
	public static void log(String msg, Object... args) {
		JLogger.info(msg, args);
		JLogger.debug(msg, args);
	}
}
