package com.dao.base;

import com.exception.JediException;

public class DaoException extends JediException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2310994891945126231L;

	public DaoException(String string, Exception e) {
		super(string, e);
	}

	public DaoException(String string) {
		super(string);
	}

	public DaoException(Exception e) {
		super(e);
	}

	public DaoException() {
		super();
	}
}
