package com.llsoft.entestserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;


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
	private StringBuffer bodytext;


	public MailReceiver(MailReceiverInfo info) {
		mReceiverInfo = info;
		bodytext = new StringBuffer();
	}



	// search a mail with its subject and return the index of the mail
	public boolean searchMail(String subject, ArrayList<String> taskList) throws Exception {
		
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
						String sender = getFrom();

						if (currSubject.contains(subject)) {
							
							
							parseMessage(mCurrentMessage);
							
							//System.out.println(sender);
							//System.out.println("---------------------------------------------------");
							//System.out.println(bodytext.toString());
							//System.out.println("---------------------------------------------------");
							synchronized (this) {
								taskList.add(sender);
								taskList.add(mReceiverInfo.getUserName());
								taskList.add(currSubject);
								taskList.add(bodytext.toString());
							}
							
							
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
	public void getMailContent(Part part) throws Exception {
		
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;

		if (nameindex != -1) {
			conname = true;
		}
		

		if (part.isMimeType("text/plain") && !conname) {
			
			InputStreamReader reader = new InputStreamReader((InputStream) part.getContent());
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[1024];

			while (reader.read(buffer) != -1) {
				sb.append(buffer);
			}

			bodytext.append(sb.toString());

		} else if (part.isMimeType("text/html") && !conname) {
			
			InputStreamReader reader = new InputStreamReader((InputStream) part.getContent());
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
	/*
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
	*/


	private String getFrom() throws Exception {
		return getFrom(mCurrentMessage);
	}

	private String getFrom(Message mimeMessage) throws Exception {
		
		InternetAddress[] address = (InternetAddress[]) mimeMessage.getFrom();

		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}

		/*
		String personal = address[0].getPersonal();
		if (personal == null) {
			personal = "";
		}
		

		String fromaddr = personal + "<" + from + ">";
		*/
		
		return from;      
	}

	private void parseMessage(Message message) throws Exception{

		Object object = message.getContent();
		
		if(object instanceof Multipart) {
			
			Multipart multipart = (Multipart) object ;
			reMultipart(multipart);
			
		} else if (object instanceof Part){
			
			Part part = (Part) object; 
			rePart(part);
			
		} else {
			getBodyText((Part)message);
		}
	}

	private void rePart(Part part) throws Exception {
		
		if (part.getDisposition() != null) {
			getAttachment(part);
		} else {
			getBodyText(part);
		}
	}

	private void reMultipart(Multipart multipart) throws Exception {

		for (int j = 0, n = multipart.getCount(); j < n; j++) {
	    
			Part part = multipart.getBodyPart(j);
	        // 解包, 取出 MultiPart的各个部分, 每部分可能是邮件内容,
	        // 也可能是另一个小包裹(MultipPart)
	        // 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative
	        if (part.getContent() instanceof Multipart) {
	        	// 转成小包裹
	            Multipart p = (Multipart) part.getContent();
	            //递归迭代
	            reMultipart(p);
	        } else {
	        	rePart(part);
	        }  
	    }
	}
	
	private void getAttachment(Part part) throws Exception {
		// MimeUtility.decodeText解决附件名乱码问题
		String strFileNmae = MimeUtility.decodeText(part.getFileName());
	    InputStream in = part.getInputStream();
	    
	    saveFile(strFileNmae, in);
	}
	
	private void getBodyText(Part part) throws Exception {
		
		// 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
		boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
		
		if (part.getContentType().startsWith("text/plain") && !isContainTextAttach) {
			
			System.out.println("----- bodytext -----");
			bodytext.append(part.getContent().toString());
			
		} else {
			//System.out.println("HTML内容：" + part.getContent());
		}
	}
	
	// Save the file to storage
	private void saveFile(String filename, InputStream in) throws Exception {

		String dir = mReceiverInfo.getAttachmentDir();
		String separator = "";
 
		File storefile = new File(MimeUtility.decodeText(dir) + 
				                  MimeUtility.decodeText(separator) + 
				                  MimeUtility.decodeText(filename));
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
