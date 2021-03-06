package com.llsoft.entestserver;

import java.util.ArrayList;


public class MailReceiveTask05 implements Runnable {

  private ArrayList<String> mTaskList;
  private MailReceiver mMailReceiver;

  public MailReceiveTask05(ArrayList<String> taskList, MailReceiverInfo mailReceiverInfo) {

    mTaskList = taskList;
    mMailReceiver = new MailReceiver(mailReceiverInfo);
  }

  @Override
  public void run() {

    try {

      System.out.println("Task05 searching ...");
            //boolean isFound = mMailReceiver.searchMail("来自John的职位分享201605", mTaskList);
      mMailReceiver.searchMail("来自John的职位分享201605", mTaskList);

      // task to run goes here
      /*
      if (isFound) {
        System.out.println("charming_lei72@sina.com found!!");
      } else {
        System.out.println("charming_lei72@sina.com not found!!");
      }
      */

    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
