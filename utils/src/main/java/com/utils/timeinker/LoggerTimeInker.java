package com.utils.timeinker;

import org.apache.logging.log4j.core.Logger;

public class LoggerTimeInker extends TimeInker<Logger> {
	public LoggerTimeInker(String name) {
		this(name, null);
	}

	public LoggerTimeInker(String name, Logger out) {
		super(name, out);
	}

	@Override
	public void println(String message) {
		out.info(message);
	}
}
