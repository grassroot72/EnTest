package com.llsoft.entest;

import java.util.ArrayList;


public class Question {

	private String mType;
	private String mKey;
	private String mUserKey;
	private String mSequence;
	private String mContent;
    private ArrayList<Choice> mList;

    
    public Question() {
    	
    	mType = Constants.QUESTION_TYPE_4_CHOICES;
    	mKey = "";
    	mUserKey = "";
    	mSequence = "";
    	mContent = "";
    	mList = new ArrayList<Choice>();
    }
    
    public Question(String type, String key, String id, String content, ArrayList<Choice> list) {
    	
    	mType = type;
    	mKey = key;
    	mUserKey = "";
    	mSequence = id;
    	mContent = content;
    	mList = list;
    }
    
    public String getType() {
    	return mType;
    }
    
    public void setType(String type) {
    	mType = type;
    }
    
    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
    	mKey = key;
    }
    
    public String getUserKey() {
        return mUserKey;
    }

    public void setUserKey(String userKey) {
    	mUserKey = userKey;
    }
    
    public String getSequence() {
        return mSequence;
    }

    public void setSequence(String sequence) {
    	mSequence = sequence;
    }
    
    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
    	mContent = content;
    }
    
    public ArrayList<Choice> getChoiceList() {
        return mList;
    }

    public void setChoiceList(ArrayList<Choice> choiceList) {
        mList = choiceList;
    }
        
}
