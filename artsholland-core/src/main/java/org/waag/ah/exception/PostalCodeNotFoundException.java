package org.waag.ah.exception;

@SuppressWarnings("serial")
public class PostalCodeNotFoundException extends Exception {
	public PostalCodeNotFoundException() {
		super("Postal code not found maar dat geeft niet.");
	}
}
