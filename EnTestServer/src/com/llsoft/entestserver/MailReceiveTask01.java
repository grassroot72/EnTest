package com.llsoft.entestserver;

import java.util.ArrayList;


public class MailReceiveTask01 implements Runnable {

	private ArrayList<String> mTaskList;
	private MailReceiver mMailReceiver;
	
	public MailReceiveTask01(ArrayList<String> taskList, MailReceiverInfo mailReceiverInfo) {
		
		mTaskList = taskList;
		mMailReceiver = new MailReceiver(mailReceiverInfo);
	}

	@Override
	public void run() {
		
		try {
			
			System.out.println("Task01 searching ...");
			mMailReceiver.searchMail("2016.10.16", mTaskList);
			
			
			// task to run goes here
			/*
			if (isFound) {
				System.out.println("entest_001@sina.com 找到了!!");
			} else {
				System.out.println("entest_001@sina.com 没找到!!");
			}
			*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
