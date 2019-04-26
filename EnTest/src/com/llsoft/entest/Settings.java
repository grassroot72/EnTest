package com.llsoft.entest;


public class Settings {

	private String mPop3ServerName;
	private String mSmtpServerName;

	private String mEmailBoxName;
	private String mEmailUserName;
	private String mPasscodeContent;
	private String mAttachmentDir;

	private int mLevelNumber;

	private int mGrammarCount;
	private int mReadingCount;

	private int mGrammarTimeLimit;
	private int mReadingTimeLimit;

	private boolean mIsEmailVerified;
	private boolean mIsEmailChanged;

	private String mSubscriptionValidDate;


	public Settings() {

		mPop3ServerName = "";
		mSmtpServerName = "";
		mEmailBoxName = "";
		mEmailUserName = "";
		mPasscodeContent = "";
		mAttachmentDir = "";

		mLevelNumber = 30;

		// we haven't prepared grammar questions, so set grammar count to 0
		mGrammarCount = 0;
		mReadingCount = 10;

		mGrammarTimeLimit = 100;
		mReadingTimeLimit = 100;

		mIsEmailVerified = false;
		mIsEmailChanged = true;

		mSubscriptionValidDate = "";
	}

	// Pop3 server name
	public String getPop3ServerName() {
		return mPop3ServerName;
	}

	public void setPop3ServerName(String servername) {
		mPop3ServerName = servername;
	}

	// Smtp server name
	public String getSmtpServerName() {
		return mSmtpServerName;
	}

	public void setSmtpServerName(String servername) {
		mSmtpServerName = servername;
	}

	// Email box name
	public String getEmailBoxName() {
		return mEmailBoxName;
	}

	public void setEmailBoxName(String boxname) {
		mEmailBoxName = boxname;
	}

	// Email user name
	public String getEmailUserName() {
		return mEmailUserName;
	}

	public void setEmailUserName(String username) {
		mEmailUserName = username;
	}

	// Email passcode
	public String getPasscode() {
		return mPasscodeContent;
	}

	public void setPasscode(String passcode) {
		mPasscodeContent = passcode;
	}

	// Email attachment directory
	public String getAttachmentDir() {
		return mAttachmentDir;
	}

	public void setAttachmentDir(String directory) {
		mAttachmentDir = directory;
	}

	// Subscription - level number
	public int getLevelNumber() {
		return mLevelNumber;
	}

	public void setLevelNumber(int number) {
		mLevelNumber = number;
	}

	// Subscription - grammar count
	public int getGrammarCount() {
		return mGrammarCount;
	}

	public void setGrammarCount(int count) {
		mGrammarCount = count;
	}

	// Subscription - reading count
	public int getReadingCount() {
		return mReadingCount;
	}

	public void setReadingCount(int count) {
		mReadingCount = count;
	}

	// Subscription - grammar count
	public int getGrammarTimeLimit() {
		return mGrammarTimeLimit;
	}

	public void setGrammarTimeLimit(int limit) {
		mGrammarTimeLimit = limit;
	}

	// Subscription - reading count
	public int getReadingTimeLimit() {
		return mReadingTimeLimit;
	}

	public void setReadingTimeLimit(int limit) {
		mReadingTimeLimit = limit;
	}

	// Email verified status
	public boolean isEmailVerified() {
		return mIsEmailVerified;
	}

	public void setEmailVerified(boolean verified) {
		mIsEmailVerified = verified;
	}

	// Email change status
	public boolean isEmailBoxChanged() {
		return mIsEmailChanged;
	}

	public void setEmailBoxChanged(boolean changed) {
		mIsEmailChanged = changed;
	}

	// subscription valid start date
	public String getSubscriptionValidDate() {
		return mSubscriptionValidDate;
	}

	public void setSubscriptionValidDate(String date) {
		mSubscriptionValidDate = date;
	}
}
