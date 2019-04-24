package com.llsoft.entest;


public class Choice {

    private String mSequence;
    private String mContent;
    private boolean mSelected;

    
    public Choice() {
    	
    	mSequence = "";
    	mContent = "";
    	mSelected = false;
    }
    
    public Choice(String sequence, String content) {
    	
    	mSequence = sequence;
    	mContent = content;
    	mSelected = false;
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
    
    public boolean isSelected() {
    	return mSelected;
    }
    
    public void setSelected(boolean selected) {
    	mSelected = selected;
    }
    
}
