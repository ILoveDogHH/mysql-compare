package com.utils.ip;

/**
 * ip查询查询失败
 */
public class IpQueryFailed extends IpException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4346764350724568503L;
	
	public IpQueryFailed(String msg, Exception e) {
		super(msg, e);
	}
	
	public IpQueryFailed(String msg) {
		super(msg);
	}

	public IpQueryFailed(Exception e) {
		super(e);
	}

	public IpQueryFailed() {
		super();
	}
}
