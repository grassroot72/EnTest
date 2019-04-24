package com.llsoft.entestserver;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


// simple mail sender (no attachments)  
public class MailSender {

	// send text mail
	public static void sendTextMail(MailSenderInfo mailInfo) throws MessagingException {

		// mail authenticator
		MailAuthenticator authenticator = null;
		Properties p = mailInfo.getProperties();

		// if authentication needed, then create one 
		if (mailInfo.isValidate()) {
			authenticator = new MailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}

		// build a send mail session
		Session sendMailSession = Session.getInstance(p, authenticator);
			
		// create a new message according to mail session
		Message mailMessage = new MimeMessage(sendMailSession);

		// create mail sender's address
		Address from = new InternetAddress(mailInfo.getFromAddress());
		mailMessage.setFrom(from);

		// create mail receiver's address
		Address to = new InternetAddress(mailInfo.getToAddress());
		mailMessage.setRecipient(Message.RecipientType.TO, to);
			
		// set subject
		mailMessage.setSubject(mailInfo.getSubject());
			
		// set send date
		mailMessage.setSentDate(new Date());

		// set mail text content
		String mailContent = mailInfo.getContent();
		mailMessage.setText(mailContent);

		// send mail
		Transport.send(mailMessage);
	}
	
}

