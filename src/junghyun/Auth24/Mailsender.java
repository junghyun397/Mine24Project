package junghyun.Auth24;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailsender extends Thread {
	
	private String address = null;
	
	private String player = null;
	
	private String token = null;
	
	private String SUB = null;
	
	private String PMS = null;
	
	public Mailsender(String address, String player, String token, int type) {
		this.address = address;
		this.player = player;
		this.token = token;
	
		if (type == 1) {
			this.SUB = this.getSUB_auth();
			this.PMS = this.getPMS_auth();
		} else if (type == 2) {
			this.SUB = this.getSUB_reset_pwd();
			this.PMS = this.getPMS_reset_pwd();
		}
	}
	
	@Override
	public void run() {
		this.check_mail(this.address);
	}
	
	private boolean check_mail(String adderss) {
    	Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.worksmobile.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("mine24@mine24.net","junghyun0911!");
			}
		});

		try {
			
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("mine24@mine24.net"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(adderss));
			message.setSubject(this.SUB);
			message.setContent(this.PMS, "text/html; charset=utf-8");
			
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
    }
	
	private String getSUB_auth() {
		return this.player+"님, 인증을 완료 해주세요.";
	}
	
	private String getPMS_auth() {
		String text = "<div style=\"width: 500px; text-align: center;\"><img src=\"http://i.imgur.com/ynrC0rz.png\" style=\"margin: 0 auto; width: 500px; margin-bottom: 0px;\"><a href=\"http://Mine24.net/mail.php?token="+this.token+"\"><img src=\"http://i.imgur.com/kSYMiJk.png\" style=\"margin: 0 auto; width: 500px; margin-top: 0px; padding-top: 0px;\"></a></div>";
		return text;
	}
	
	private String getSUB_reset_pwd() {
		return this.player+"님, 비밀번호를 초기화를 완료 해주세요.";
	}
	
	private String getPMS_reset_pwd() {
		String text = "<div style=\"width: 500px; text-align: center;\"><img src=\"http://i.imgur.com/ynrC0rz.png\" style=\"margin: 0 auto; width: 500px; margin-bottom: 0px;\"><a href=\"http://Mine24.net/mail.php?token="+this.token+"&type=pwrst\"><img src=\"http://i.imgur.com/kSYMiJk.png\" style=\"margin: 0 auto; width: 500px; margin-top: 0px; padding-top: 0px;\"></a></div>";
		return text;
	}
}
