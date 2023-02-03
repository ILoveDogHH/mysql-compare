package com.utils.translate;

public class TranslateException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7660993108355906556L;

	public TranslateException(String string, Exception e) {
		super(string,e);
	}
}
