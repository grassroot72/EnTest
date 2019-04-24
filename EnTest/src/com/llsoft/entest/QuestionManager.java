package com.llsoft.entest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;


public class QuestionManager {
	
	public static ArrayList<Question> parseXMLFile(String filename) {
		
    	ArrayList<Question> qlist = null;
		ArrayList<Choice> clist = null;
		Question question = null;
		Choice choice = null;
		
		try {
			
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(bis, "utf-8");
			int event = parser.getEventType();
			
			while (event != XmlPullParser.END_DOCUMENT) {
				
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					qlist = new ArrayList<Question>();
					break;
					
				case XmlPullParser.START_TAG:
					if ("question".equals(parser.getName())) {
						
						question = new Question();
						question.setType(parser.getAttributeValue(0));
						question.setSequence(parser.getAttributeValue(1));
						
						clist = new ArrayList<Choice>();
						
					} else if ("content".equals(parser.getName())) {
						
						// get the attribute first
						question.setKey(parser.getAttributeValue(0));
						question.setContent(parser.nextText());
						
					} else if ("choice".equals(parser.getName())) {
						
						choice = new Choice();
						choice.setSequence(parser.getAttributeValue(0));
						choice.setContent(parser.nextText());
						clist.add(choice);
					}
					break;
					
				case XmlPullParser.END_TAG:
					if ("question".equals(parser.getName())) {
						question.setChoiceList(clist);
						qlist.add(question);
					}
				}
				event = parser.next();
			}
			
			//---close everything---
			bis.close();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return qlist;
	}
	
	public static void setUserKey(Question question, int childPosition) {
    	
		if (question.getType().equals(Constants.QUESTION_TYPE_4_CHOICES)) {
			
			switch (childPosition) {
			case 0:
				question.setUserKey(Constants.KEY_A);
				break;
			case 1:
				question.setUserKey(Constants.KEY_B);
				break;
			case 2:
				question.setUserKey(Constants.KEY_C);
				break;
			case 3:
				question.setUserKey(Constants.KEY_D);
			}
			
		} else if (question.getType().equals(Constants.QUESTION_TYPE_T_OR_F)) {
			
			switch (childPosition) {
			case 0:
				question.setUserKey(Constants.KEY_T);
				break;
			case 1:
				question.setUserKey(Constants.KEY_F);
			}
		}
    }

    public static void loadUserKeyFile(QuestionListAdapter questionListAdapter, String filename) {
    	
    	try {

			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			InputStreamReader isr = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(isr);
			
			ArrayList<Question> questionList = questionListAdapter.getQuestionList();
			
			String strline;
			// read the first line
			strline = br.readLine();
			// check if the user key had submitted or not
			questionListAdapter.setUserKeySubmitted(Boolean.valueOf(strline));
			
			while ((strline = br.readLine()) != null) {
				
				String[] data = strline.split(" ");
				int groupPosition = Integer.parseInt(data[0]);
				int childPosition = Integer.parseInt(data[1]);
				Question question = questionList.get(groupPosition);
				Choice choice = question.getChoiceList().get(childPosition);
				choice.setSelected(true);
				
				setUserKey(question, childPosition);	
			}
			
			//---close everything---
			br.close();
			isr.close();
			bis.close();
			fis.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
    	
    }
	
	public static void saveUserKeyFile(QuestionListAdapter questionListAdapter, String filename) {

    	try {

    		Question question = null;
    		ArrayList<Choice> clist = null;
    		ArrayList<Question> questionList = questionListAdapter.getQuestionList();
    		
    		String keysString = String.valueOf(questionListAdapter.isUserKeySubmitted()) + "\n";
    		
    		for (int i = 0; i < questionList.size(); i++) {
    			
    			question = questionList.get(i);
    			clist = question.getChoiceList();
    			
    			for (int j = 0; j < clist.size(); j++) {
    				
    				Choice choice = clist.get(j);
    				if (choice.isSelected()) {
    					keysString += String.valueOf(i) + " " + String.valueOf(j) + "\n";
    				}
    			}

    		}
    		
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			OutputStreamWriter osw = new OutputStreamWriter(bos);
			
			//---write the string to the file---
			osw.write(keysString);
			osw.flush();
			
			//---close everything---
			osw.close();
			bos.close();
			fos.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
    }
}
