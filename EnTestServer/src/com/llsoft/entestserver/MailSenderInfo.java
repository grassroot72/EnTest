package com.llsoft.entestserver;

import java.util.Properties;


// Mail sender's information
public class MailSenderInfo {

  // SMTP host IP address and port
  private String mSmtpHost;
  private String mSmtpPort = "25";

  // mail sender's address
  private String mFromAddress;
  // mail receiver's address
  private String mToAddress;

  // mail user and passcode
  private String mUsername;
  private String mPassword;

  // validation (authentication needed)
  private boolean mValidate = true;

  // subject
  private String mSubject;

  // mail content
  private String mContent;

  // attachment names
  private String[] mAttachFileNames;


  // mail session properties
  public Properties getProperties() {

    Properties p = new Properties();
    p.put("mail.smtp.host", mSmtpHost);
    p.put("mail.smtp.port", mSmtpPort);
    p.put("mail.smtp.auth", mValidate ? "true" : "false");

    return p;
  }

  // smtp server host
  public String getSmtpHost() {
    return mSmtpHost;
  }

  public void setSmtpHost(String host) {
    mSmtpHost = host;
  }

  // smtp port
  public String getSmtpPort() {
    return mSmtpPort;
  }

  public void setSmtpPort(String port) {
    mSmtpPort = port;
  }

  // sender's address
  public String getFromAddress() {
    return mFromAddress;
  }

  public void setFromAddress(String address) {
    mFromAddress = address;
  }

  // receiver's address
  public String getToAddress() {
    return mToAddress;
  }

  public void setToAddress(String address) {
    mToAddress = address;
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

  // subject
  public String getSubject() {
    return mSubject;
  }

  public void setSubject(String subject) {
    mSubject = subject;
  }

  // content
  public String getContent() {
    return mContent;
  }

  public void setContent(String content) {
    mContent = content;
  }

  // attachment names
  public String[] getAttachFileNames() {
    return mAttachFileNames;
  }

  public void setAttachFileNames(String[] filenames) {
    mAttachFileNames = filenames;
  }

}
