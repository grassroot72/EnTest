package com.llsoft.entest;

import java.io.File;
import java.util.Properties;


public class MailReceiverInfo {

	// POP3 server host name, port and protocol
	private String mPop3Host;
	private String mPop3Port = "110";
	private String mProtocal = "pop3";
	
	// user, password to login POP3 server
	private String mUsername;
	private String mPassword;
	
	// validation (authentication needed)
	private boolean mValidate = true;
	
	// where to save the mails
	private String mEmailDir;
	
	// where to save attachment?
	private String mAttachmentDir;
	
	// the mail suffix 'eml'
	private String mEmailFileSuffix = ".eml";
	

	// mail session properties
	public Properties getProperties(){
		
		Properties p = new Properties();
		p.put("mail.pop3.host", mPop3Host);
		p.put("mail.pop3.port", mPop3Port);
		p.put("mail.pop3.auth", mValidate ? "true" : "false");
		
		return p;
	}

	// pop3 server host
	public String getPop3Host() {
		return mPop3Host;
	}

	public void setPop3Host(String host) {
		mPop3Host = host;
	}

	// pop3 port
	public String getPop3Port() {
		return mPop3Port;
	}
	
	public void setpop3Port(String port) {
		mPop3Port = port;
	}
	
	// protocol
	public String getProtocal() {
		return mProtocal;
	}

	public void setProtocal(String protocal) {
		mProtocal = protocal;
	}

	// mail user
	public String getUserName() {
		return mUsername;
	}

	public void setUserName(String name) {
		mUsername = name;
	}

	// password
	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}
	
	// authentication
	public boolean isValidate() {
		return mValidate;
	}

	public void setValidate(boolean validate) {
		mValidate = validate;
	}
	
	// email directory
	public String getEmailDir() {
		return mEmailDir;
	}

	public void setEmailDir(String dir) {
	
		if (!dir.endsWith(File.separator)) {
			mEmailDir = dir + File.separator;
			return;
		}
		
		mEmailDir = dir;
	}
	
	// attachment directory
	public String getAttachmentDir() {
		return mAttachmentDir;
	}

	public void setAttachmentDir(String dir) {

		if (!dir.endsWith(File.separator)) {
			mAttachmentDir = dir + File.separator;
			return;
		}
		
		mAttachmentDir = dir;
	}

	// email suffix
	public String getEmailFileSuffix() {
		return mEmailFileSuffix;
	}

	public void setEmailFileSuffix(String suffix) {
		
		if (!suffix.startsWith(".")) {
			mEmailFileSuffix = "." + suffix;
		}
		
		mEmailFileSuffix = suffix;
	}
	
}
