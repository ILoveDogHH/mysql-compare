package com.utils.ip;

/**
 * ip查询中出错
 */
public class IpQueryError extends IpException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3389323918362959258L;
	
	public IpQueryError(String msg, Exception e) {
		super(msg, e);
	}
	
	public IpQueryError(String msg) {
		super(msg);
	}

	public IpQueryError(Exception e) {
		super(e);
	}

	public IpQueryError() {
		super();
	}
}
