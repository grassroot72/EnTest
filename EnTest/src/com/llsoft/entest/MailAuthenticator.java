package com.llsoft.entest;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class MailAuthenticator extends Authenticator {
	
	String mUsername = null;
	String mPassword = null;

	
	public MailAuthenticator(String username, String password) {
		mUsername = username;
		mPassword = password;
	}    

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(mUsername, mPassword);
	}
	
}
