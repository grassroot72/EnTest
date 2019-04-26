package com.llsoft.entestserver;

import java.util.ArrayList;


public class MailReceiveTask04 implements Runnable {

  private ArrayList<String> mTaskList;
  private MailReceiver mMailReceiver;

  public MailReceiveTask04(ArrayList<String> taskList, MailReceiverInfo mailReceiverInfo) {

    mTaskList = taskList;
    mMailReceiver = new MailReceiver(mailReceiverInfo);
  }

  @Override
  public void run() {

    try {

      System.out.println("Task04 searching ...");
            //boolean isFound = mMailReceiver.searchMail("2016.10.16", mTaskList);
      mMailReceiver.searchMail("2016.10.16", mTaskList);

      // task to run goes here
      /*
      if (isFound) {
        System.out.println("entest_004@sina.com found!!");
      } else {
        System.out.println("entest_004@sina.com not found!!");
      }
      */

    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
