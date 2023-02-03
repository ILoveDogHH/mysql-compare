package com.dao.database.base;

import com.dao.base.DaoException;

public class DBInterfaceException extends DaoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -465842757282204358L;
	
	public DBInterfaceException(String string) {
        super(string);
    }
	
	public DBInterfaceException(String string, Exception e) {
		super(string,e);
	}

}