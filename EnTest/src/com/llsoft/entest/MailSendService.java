package com.llsoft.entest;

import javax.mail.MessagingException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;


public class MailSendService extends IntentService {
	
	public MailSendService() {
        super("MailSendService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		final ResultReceiver receiver = intent.getParcelableExtra("MailSendService");
		
		// fill the mailSenderInfo
		MailSenderInfo mailSenderInfo = new MailSenderInfo();
		mailSenderInfo.setSmtpHost(intent.getStringExtra("SmtpHost"));
		mailSenderInfo.setFromAddress(intent.getStringExtra("Sender"));
		mailSenderInfo.setUserName(intent.getStringExtra("Username"));
		mailSenderInfo.setPassword(intent.getStringExtra("Password"));
		mailSenderInfo.setToAddress(intent.getStringExtra("ToAddress"));
		mailSenderInfo.setSubject(intent.getStringExtra("Subject"));
		mailSenderInfo.setContent(intent.getStringExtra("Content"));
		

		Bundle bundle = new Bundle();
		
		/* Update UI: Service is Running */
        receiver.send(Constants.STATUS_SERVICE_RUNNING, Bundle.EMPTY);
        
		// send the mail
		try {
			
			MailSender.sendTextMail(mailSenderInfo);
			/* Sending result back to activity */
			bundle.putString("status", "sent");
	        receiver.send(Constants.STATUS_SERVICE_FINISHED, bundle);
			
		} catch (MessagingException me) {
			
			/* Sending error message back to activity */
            //bundle.putString("result", me.toString());
			bundle.putString("status", me.toString());
            receiver.send(Constants.STATUS_SERVICE_ERROR, bundle);
		}
		
	}
}
