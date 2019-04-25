package com.llsoft.entest;

import android.content.Context;
import android.content.Intent;


public class MailService {
	
  public static Intent getSendIntent(Context context, Class<?> cls, Settings settings, ServiceResultReceiver serviceReceiver) {
    // create a MailSendService intent
    Intent serviceIntent = new Intent(context, cls);
    // assign the "MailSendService" serviceReceiver
    serviceIntent.putExtra("MailSendService", serviceReceiver);

    // fill the settings
    serviceIntent.putExtra("SmtpHost", settings.getSmtpServerName());
    serviceIntent.putExtra("Sender", settings.getEmailBoxName());
    serviceIntent.putExtra("Username", settings.getEmailUserName());
    serviceIntent.putExtra("Password", settings.getPasscode());

    return serviceIntent;
  }

  public static Intent getReceiveIntent(Context context, Class<?> cls, Settings settings, ServiceResultReceiver serviceReceiver) {
    // create the MailReceiveService intent
    Intent serviceIntent = new Intent(context, cls);
    // assign the "MailReceiveService" serviceReceiver
    serviceIntent.putExtra("MailReceiveService", serviceReceiver);

    // fill the settings
    serviceIntent.putExtra("Pop3Host", settings.getPop3ServerName());
    serviceIntent.putExtra("Username", settings.getEmailUserName());
    serviceIntent.putExtra("Password", settings.getPasscode());
    serviceIntent.putExtra("attachmentDir", settings.getAttachmentDir());

    return serviceIntent;
  }

}
