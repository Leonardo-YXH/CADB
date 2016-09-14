package cn.npt.util.mail;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import cn.npt.util.data.PropertyFileParse;

import com.sun.mail.util.MailSSLSocketFactory;

public class MailUtil {

	private static PropertyFileParse pfp=PropertyFileParse.getInstance("cadb.properties");
	
	private static final String HOST = "smtp.163.com";
	private static final int PORT = 465;
	private static final String PROTOCOL = "smtp";
	/**
	 * 系统发件人邮箱
	 */
	private static final String Account = pfp.getValue("mail", "account");
	/**
	 * 邮箱登录密码
	 */
	private static final String Passwd =pfp.getValue("mail", "password");
	/**
	 * 获取Session
	 * @return
	 */
	private static Session getQqSession() {

		Properties props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.transport.protocol", PROTOCOL);
		MailSSLSocketFactory sf;
		try {
			sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.socketFactory", sf);
			Session session = Session.getDefaultInstance(props);
			session.setDebug(true);
			return session;
		} catch (GeneralSecurityException e) {
			return null;
		}
	}

	/**
	 * 邮件发送
	 * @param toEmail 目标邮箱
	 * @param title
	 * @param content
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static void sendMail(String toEmail, String title, String content) throws AddressException, MessagingException {
		Session session = getQqSession();
		Message msg = new MimeMessage(session);
		msg.setText(content);
		msg.setFrom(new InternetAddress(Account));
		msg.setSubject(title);
		Transport transport = session.getTransport();
		transport.connect(HOST, PORT, Account, Passwd);
		String[] toEmailArr = toEmail.split(",");
		Address[] addrs = new InternetAddress[toEmailArr.length];
		for(int i=0;i<toEmailArr.length;i++){
			String email = toEmailArr[i];
			addrs[i] = new InternetAddress(email);
		}
		transport.sendMessage(msg,
				addrs);
		transport.close();
	}
	/** 
	 * 发送富文本编辑器
	 * @param toEmail
	 * @param title
	 * @param content
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static void sendRichTextMail(String toEmail, String title, String content) throws AddressException, MessagingException {
		Session session = getQqSession();
		Message msg = new MimeMessage(session);
		//msg.setText(content);
		Multipart mainPart = new MimeMultipart(); 
		BodyPart html = new MimeBodyPart();  
		html.setContent(content, "text/html; charset=utf-8");  
        mainPart.addBodyPart(html);
        msg.setContent(mainPart);  
        msg.saveChanges();  
		msg.setFrom(new InternetAddress(Account));
		msg.setSubject(title);
		Transport transport = session.getTransport();
		transport.connect(HOST, PORT, Account, Passwd);
		String[] toEmailArr = toEmail.split(",");
		Address[] addrs = new InternetAddress[toEmailArr.length];
		for(int i=0;i<toEmailArr.length;i++){
			String email = toEmailArr[i];
			addrs[i] = new InternetAddress(email);
		}
		transport.sendMessage(msg,
				addrs);
		transport.close();
	}
}