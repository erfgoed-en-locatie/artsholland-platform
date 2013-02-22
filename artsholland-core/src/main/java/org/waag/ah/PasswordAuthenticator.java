package org.waag.ah;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class PasswordAuthenticator extends Authenticator {

	private String username;
	private String password;

	public PasswordAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}
}