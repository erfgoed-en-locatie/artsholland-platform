package org.waag.ah.exception;

@SuppressWarnings("serial")
public class PostalCodeNotFoundException extends Exception {
	public PostalCodeNotFoundException(String message) {
		super(message);
	}
	
	public PostalCodeNotFoundException() {
		super("Postal code not found maar dat geeft niet.");
	}
	
	public PostalCodeNotFoundException(Exception e) {
		super("Postal code not found maar dat geeft niet.", e);
	}
	
	public PostalCodeNotFoundException(String message, Exception e) {
		super(message, e);
	}
}
