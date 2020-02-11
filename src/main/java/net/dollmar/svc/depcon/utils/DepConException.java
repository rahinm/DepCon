package net.dollmar.svc.depcon.utils;


public class DepConException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4042229320393117083L;

	private int code;
	
	public DepConException(String msg) {
		super(msg);
	}
	
	public DepConException(int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public DepConException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public DepConException(int code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}