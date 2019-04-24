package com.llsoft.entest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.util.Locale;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;


/**
  * Mail receiver - only support pop3
  * It can receive text, HTML and mail with attachments
  */
public class MailReceiver {
	
	// the parameters needed by MailReceiver
	private MailReceiverInfo mReceiverInfo;
	// The store obtained when connecting with pop3 server
	private Store mStore;
	// inbox folder
	private Folder mFolder;
	// messages in the inbox
	private Message[] mMessages;	
	// the current message
	private Message mCurrentMessage;
	// body text
	//private StringBuffer bodytext;


	public MailReceiver(MailReceiverInfo info) {
		mReceiverInfo = info;
		//bodytext = new StringBuffer();
	}



	// search a mail with its subject and return the index of the mail
	public boolean searchMail(String subject) throws Exception {
		
		if (mReceiverInfo == null) {
			throw new Exception("must provide receiver parameters!");
		}

		// connect to pop3 server
		if (connect()) {
			
			// open the inbox folder
			if (openInBoxFolder()) {
				
				// obtain messages from the inbox folder
				mMessages = mFolder.getMessages();
				int mailArrayLength = getMessageCount();

				boolean isFound = false;
				
				for (int index = mailArrayLength - 1; index >= 0; index--) {
					
					try {
						
						// get the current message
						mCurrentMessage = (mMessages[index]);
						String currSubject = getSubject(mCurrentMessage);

						if (currSubject.equals(subject)) {
							
							//getMailContent((Part) currentMessage);
							saveAttachMent((Part) mCurrentMessage);
							isFound = true;
							break;		
						}
					
					} catch (Throwable e) {
						// Todo
					}
					
				}
				
				disconnect();
				
				// searching status
				return isFound;
				
			} else {		
				throw new Exception("Open Inbox folder failed!");		
			}
		
		} else {
			throw new Exception("Connect to pop3 server failed!");
		}
		
	}
	

	// connect to the pop3 server
	public boolean connect() throws MessagingException {
		
		// check if authentication is needed
		MailAuthenticator authenticator = null;
		
		// if authentication needed, create an authenticator
		if (mReceiverInfo.isValidate()) {
			authenticator = new MailAuthenticator(mReceiverInfo.getUserName(), mReceiverInfo.getPassword());
		}

		// create a session
		Session session = Session.getInstance(mReceiverInfo.getProperties(), authenticator);

		// create a store, establish the connection
		try {
			mStore = session.getStore(mReceiverInfo.getProtocal());
		} catch (NoSuchProviderException e) {
			return false;
		}

		try {
			mStore.connect();
		} catch (MessagingException e) {
			// Connect to pop3 server failed!
			throw e;
		}
		
		return true;
	}

	
	// Open Inbox
	private boolean openInBoxFolder() {

		try {
			
			mFolder = mStore.getFolder("INBOX");
			// read only mode
			mFolder.open(Folder.READ_ONLY);
			return true;

		} catch (MessagingException e) {
			// Open Inbox folder failed!
			return false;
		}

	}


	// Disconnect from POP3 server
	private boolean disconnect() {
		
		try {
			
			if (mFolder.isOpen()) {
				mFolder.close(true);
			}
			
			mStore.close();
			// Disconnect from POP3 server succeeded!
			return true;
			
		} catch (Exception e) {
			// Unknown error when disconnecting from POP3 server
			return false;
		}

	}


	// Obtain the total number of messages
	private int getMessageCount() {
		return mMessages.length;
	}


	// Obtain the subject of a mail
	private String getSubject(Message mimeMessage) throws MessagingException {
		
		String subject = "";
		
		try {
			
			// decode the subject
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			
			if (subject == null) {
				subject = "";
			}
			
		} catch (Exception exce) { }
		
		return subject;
	}
    

	//Parse the message and store the content in a StringBuffer object
	/*
	private void getMailContent(Part part) throws Exception {
		
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;

		if (nameindex != -1) {
			conname = true;
		}

		if (part.isMimeType("text/plain") && !conname) {
			
			InputStreamReader reader = new InputStreamReader((InputStream)part.getContent());
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[1024];

			while (reader.read(buffer) != -1) {
				sb.append(buffer);
			}

			bodytext.append(sb.toString());

		} else if (part.isMimeType("text/html") && !conname) {
			
			InputStreamReader reader = new InputStreamReader((InputStream)part.getContent());
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[1024];

			while(reader.read(buffer) != -1) {
				sb.append(buffer);
			}

			bodytext.append(sb.toString());

		} else if (part.isMimeType("multipart/*")) {      

			DataSource source = new ByteArrayDataSource(part.getInputStream(), "multipart/*");
			Multipart multipart = new MimeMultipart(source);
			int counts = multipart.getCount();

			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}       

		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		}

	}
	*/


	// Save the attachment
	private void saveAttachMent(Part part) throws Exception {

		String filename = "";

		if (part.isMimeType("multipart/*")) {

			Locale currLocale = Locale.getDefault();
			DataSource source = new ByteArrayDataSource(part.getInputStream(), "multipart/*");
			Multipart mp = new MimeMultipart(source);

			for (int i = 0; i < mp.getCount(); i++) {       

				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();

				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {       

					filename = mpart.getFileName();
					if ((filename != null) && (filename.toLowerCase(currLocale).indexOf("gbk") != -1)) {
						filename = MimeUtility.decodeText(filename);
					}
					saveFile(filename, mpart.getInputStream());
					
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachMent(mpart);
				} else {       

					filename = mpart.getFileName();

					if ((filename != null) && (filename.toLowerCase(currLocale).indexOf("gb2312") != -1)) {

						filename = MimeUtility.decodeText(filename);
						saveFile(filename, mpart.getInputStream());	
					}
				}
			}
			
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachMent((Part) part.getContent());
		}
	}


	// Save the file to storage
	private void saveFile(String filename, InputStream in) throws Exception {

		String dir = mReceiverInfo.getAttachmentDir();
		String separator = "";
 
		File storefile = new File(MimeUtility.decodeText(dir) + MimeUtility.decodeText(separator) + MimeUtility.decodeText(filename));
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;

		try {
			
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(in);

			int c;

			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new Exception("Failed saving file!");
			
		} finally {
			bos.close();
			bis.close();
		}
		
	}

}
