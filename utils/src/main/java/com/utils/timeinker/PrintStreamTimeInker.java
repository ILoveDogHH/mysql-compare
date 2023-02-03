package com.utils.timeinker;

import java.io.PrintStream;

public class PrintStreamTimeInker extends TimeInker<PrintStream> {
	public PrintStreamTimeInker(String name) {
		this(name, System.out);
	}

	public PrintStreamTimeInker(String name, PrintStream out) {
		super(name, out);
	}

	@Override
	public void println(String message) {
		out.println(message);
	}
}
