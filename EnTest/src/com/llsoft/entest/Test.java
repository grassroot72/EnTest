package com.llsoft.entest;


public class Test {

  private int mMark;
  private int mType;
  private int mLevel;
  private int mSequence;
  private String mDateString;
  private int mStatus;


  public Test() {

    mMark = Constants.STATUS_TEST_NOT_DOWNLOADED;
    mType = Constants.TYPE_TEST_GRAMMAR;
    mLevel = 3;
    mSequence = 0;
    mDateString = "";
    mStatus = Constants.STATUS_TEST_NOT_DOWNLOADED;
  }

  public Test(int mark, int type, int level, int sequence, String dateString, int status) {

    mMark = mark;
    mType = type;
    mLevel = level;
    mSequence = sequence;
    mDateString = dateString;
    mStatus = status;
  }

  public int getMark() {
    return mMark;
  }

  public void setMark(int mark) {
    mMark = mark;
  }

  public int getType() {
    return mType;
  }

  public void setType(int type) {
    mType = type;
  }

  public int getLevel() {
    return mLevel;
  }

  public void setLevel(int level) {
    mLevel = level;
  }

  public int getSequence() {
    return mSequence;
  }
  
  public void setSequence(int sequence) {
    mSequence = sequence;
  }

  public String getDateString() {
    return mDateString;
  }
  
  public void setDateString(String dateString) {
    mDateString = dateString;
  }

  public int getStatus() {
    return mStatus;
  }

  public void setStatus(int status) {
    mStatus = status;
  }
}
