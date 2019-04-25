package com.llsoft.entest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;


public class MailReceiveService extends IntentService {

  public MailReceiveService() {
    super("MailReceiveService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {

    final ResultReceiver receiver = intent.getParcelableExtra("MailReceiveService");

    // the mail receiver works from here
    MailReceiverInfo mailReceiverInfo = new MailReceiverInfo();
    mailReceiverInfo.setPop3Host(intent.getStringExtra("Pop3Host"));
    mailReceiverInfo.setUserName(intent.getStringExtra("Username"));
    mailReceiverInfo.setPassword(intent.getStringExtra("Password"));
    mailReceiverInfo.setAttachmentDir(intent.getStringExtra("attachmentDir"));

    // receive the mail we are searching
    MailReceiver mailReceiver = new MailReceiver(mailReceiverInfo);

    Bundle bundle = new Bundle();

    /* Update UI: Service is Running */
    receiver.send(Constants.STATUS_SERVICE_RUNNING, Bundle.EMPTY);

    try {

      final String subject = intent.getStringExtra("Subject");
      final int itemId = intent.getIntExtra("RowId", 1);

      boolean isFound = mailReceiver.searchMail(subject);
      /* Sending result back to activity */
      bundle.putBoolean("IsFound", isFound);
      bundle.putInt("RowId", itemId);
      receiver.send(Constants.STATUS_SERVICE_FINISHED, bundle);
    }
    catch (Exception e) {
      /* Sending error message back to activity */
      bundle.putString("ErrorResult", e.toString());
      receiver.send(Constants.STATUS_SERVICE_ERROR, bundle);  
    }
  }

}
