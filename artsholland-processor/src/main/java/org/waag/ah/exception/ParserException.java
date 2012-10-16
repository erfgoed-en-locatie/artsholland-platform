package org.waag.ah.exception;

public class ParserException extends Exception {
	private static final long serialVersionUID = -9092619489901587459L;
	
	public ParserException(String message) {
		super(message);
	}
	
	public ParserException(String message, Exception e) {
		super(message, e);
	}
	
	public ParserException(Throwable e) {
		super(e);
	}
}
