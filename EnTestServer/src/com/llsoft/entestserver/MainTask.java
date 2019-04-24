package com.llsoft.entestserver;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainTask {
	
	public static void main(String[] args) {
		
		ArrayList<String> taskList = new ArrayList<>();
		
		MailReceiverInfo mailReceiverInfo01 = new MailReceiverInfo();
		mailReceiverInfo01.setPop3Host("pop.sina.com");
		mailReceiverInfo01.setUserName("entest_001@sina.com");
		mailReceiverInfo01.setPassword("china0927");
		mailReceiverInfo01.setAttachmentDir("/home/edward/EnTest");
		
		MailReceiverInfo mailReceiverInfo02 = new MailReceiverInfo();
		mailReceiverInfo02.setPop3Host("pop.sina.com");
		mailReceiverInfo02.setUserName("entest_002@sina.com");
		mailReceiverInfo02.setPassword("china0927");
		mailReceiverInfo02.setAttachmentDir("/home/edward/EnTest");
		
		MailReceiverInfo mailReceiverInfo03 = new MailReceiverInfo();
		mailReceiverInfo03.setPop3Host("pop.sina.com");
		mailReceiverInfo03.setUserName("entest_003@sina.com");
		mailReceiverInfo03.setPassword("china0927");
		mailReceiverInfo03.setAttachmentDir("/home/edward/EnTest");
		
		MailReceiverInfo mailReceiverInfo04 = new MailReceiverInfo();
		mailReceiverInfo04.setPop3Host("pop.sina.com");
		mailReceiverInfo04.setUserName("entest_004@sina.com");
		mailReceiverInfo04.setPassword("china0927");
		mailReceiverInfo04.setAttachmentDir("/home/edward/EnTest");
		
		MailReceiverInfo mailReceiverInfo05 = new MailReceiverInfo();
		mailReceiverInfo05.setPop3Host("pop.sina.com");
		mailReceiverInfo05.setUserName("charming_lei72@sina.com");
		mailReceiverInfo05.setPassword("lzyllf0312");
		mailReceiverInfo05.setAttachmentDir("/home/edward/EnTest");
		
		MailReceiveTask01 mailReceiveTask01 = new MailReceiveTask01(taskList, mailReceiverInfo01);
		MailReceiveTask02 mailReceiveTask02 = new MailReceiveTask02(taskList, mailReceiverInfo02);
		MailReceiveTask03 mailReceiveTask03 = new MailReceiveTask03(taskList, mailReceiverInfo03);
		MailReceiveTask04 mailReceiveTask04 = new MailReceiveTask04(taskList, mailReceiverInfo04);
		MailReceiveTask05 mailReceiveTask05 = new MailReceiveTask05(taskList, mailReceiverInfo05);
		
		long mailReceiveDelay01 = TimeUtils.calDelay(9, 48, 00);
		System.out.println("Mail Receive Dealy " + String.valueOf(mailReceiveDelay01/1000) + " seconds");
		
		long mailReceiveDelay02 = TimeUtils.calDelay(9, 48, 00);
		System.out.println("Mail Receive Dealy " + String.valueOf(mailReceiveDelay02/1000) + " seconds");
		
		long mailReceiveDelay03 = TimeUtils.calDelay(9, 48, 00);
		System.out.println("Mail Receive Dealy " + String.valueOf(mailReceiveDelay03/1000) + " seconds");
		
		long mailReceiveDelay04 = TimeUtils.calDelay(9, 48, 00);
		System.out.println("Mail Receive Dealy " + String.valueOf(mailReceiveDelay04/1000) + " seconds");

		long mailReceiveDelay05 = TimeUtils.calDelay(9, 48, 00);
		System.out.println("Mail Receive Dealy " + String.valueOf(mailReceiveDelay05/1000) + " seconds");

		
		
		long mailReceivePeriod = TimeUtils.calPeriod(60*60);
		
		
		ScheduledExecutorService service01 = Executors.newSingleThreadScheduledExecutor();
		service01.scheduleAtFixedRate(mailReceiveTask01, mailReceiveDelay01, mailReceivePeriod, TimeUnit.MILLISECONDS);
		
		ScheduledExecutorService service02 = Executors.newSingleThreadScheduledExecutor();
		service02.scheduleAtFixedRate(mailReceiveTask02, mailReceiveDelay02, mailReceivePeriod, TimeUnit.MILLISECONDS);
		
		ScheduledExecutorService service03 = Executors.newSingleThreadScheduledExecutor();
		service03.scheduleAtFixedRate(mailReceiveTask03, mailReceiveDelay03, mailReceivePeriod, TimeUnit.MILLISECONDS);
		
		ScheduledExecutorService service04 = Executors.newSingleThreadScheduledExecutor();
		service04.scheduleAtFixedRate(mailReceiveTask04, mailReceiveDelay04, mailReceivePeriod, TimeUnit.MILLISECONDS);
		
		ScheduledExecutorService service05 = Executors.newSingleThreadScheduledExecutor();
		service05.scheduleAtFixedRate(mailReceiveTask05, mailReceiveDelay05, mailReceivePeriod, TimeUnit.MILLISECONDS);
		
		
		
		CollectSubscriptionTask collectSubscriptionTask = new CollectSubscriptionTask(taskList);
		
		long collectSubscriptionDelay = TimeUtils.calDelay(9, 49, 00);
		System.out.println("Collect Subscription Dealy " + String.valueOf(collectSubscriptionDelay/1000) + " seconds");
		
		long collectSubscriptionPeriod = TimeUtils.calPeriod(24*60*60);
		
		ScheduledExecutorService service06 = Executors.newSingleThreadScheduledExecutor();
		service06.scheduleAtFixedRate(collectSubscriptionTask, collectSubscriptionDelay, collectSubscriptionPeriod, TimeUnit.MILLISECONDS);
		
	}
}