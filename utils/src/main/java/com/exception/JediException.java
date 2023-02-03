package com.exception;

public class JediException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8828605607460799115L;

	public JediException(String string, Exception e) {
		super(string, e);
	}
	
	public JediException(String string) {
		super(string);
	}

	public JediException(Exception e) {
		super(e);
	}

	public JediException() {
		super();
	}
}
