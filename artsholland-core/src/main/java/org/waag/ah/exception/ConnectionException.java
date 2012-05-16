package org.waag.ah.exception;

/**
 * @author Raoul Wissink <raoul@raoul.net>
 * @todo Figure out correct base class to use here.
 */
@SuppressWarnings("serial")
public class ConnectionException extends Exception {

	public ConnectionException(String message, Exception e) {
		super(message, e);
	}
}
