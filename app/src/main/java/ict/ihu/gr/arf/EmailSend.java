package ict.ihu.gr.arf;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSend {

    public static void sendEmail(final String recipientEmail, final String subject, final String body) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Mail server properties
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com"); // Change to your SMTP server host
                    props.put("mail.smtp.port", "587"); // Change to your SMTP server port

                    // Sender's email address and password
                    final String username = "zedkairengar@gmail.com"; // Change to your email address
                    final String password = "hrko ctth vqrl upst"; // Change to your email password

                    // Create a new session with an authenticator
                    Session session = Session.getInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(username, password);
                                }
                            });

                    // Create a new message
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                    message.setSubject(subject);
                    message.setText(body);

                    // Send the message
                    Transport.send(message);
                } catch (MessagingException e) {
                    Log.e("EmailSender", "Error sending email", e);
                }
                return null;
            }
        }.execute();
    }
}
