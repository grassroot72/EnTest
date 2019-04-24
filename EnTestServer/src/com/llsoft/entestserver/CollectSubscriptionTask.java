package com.llsoft.entestserver;

import java.util.ArrayList;


public class CollectSubscriptionTask implements Runnable {

	private ArrayList<String> mTaskList;
	
	
	public CollectSubscriptionTask(ArrayList<String> taskList) {
		mTaskList = taskList;
	}

	@Override
	public void run() {
		
		try {
			
			System.out.println("----- the collection -----");
			
			for(int i = 0; i < mTaskList.size(); i++) {
				System.out.println(mTaskList.get(i));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}