package com.utils.ip;

import com.exception.JediException;

public class IpException extends JediException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1758948985336244629L;
	
	public IpException(String msg, Exception e) {
		super(msg, e);
	}
	
	public IpException(String msg) {
		super(msg);
	}

	public IpException(Exception e) {
		super(e);
	}

	public IpException() {
		super();
	}
}
