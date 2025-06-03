/*
 * TCSS 360 Course Project
 */

package Model;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.mail.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;

/**
 * Handles emailing functionality through Gmail, sending a CSV file as an attachment.
 *
 * @author Marcus Nguyen
 * @version 6/3/2025
 */
public class Email {

    /**
     * The program's sending email address.
     */
    private static final String SENDING = "custom77429@gmail.com";

    /**
     * Generated app password for the program's sending email address
     * (Gmail requires a password).
     */
    private static final String PASSWORD = "yrrg jopn xiiq zzhs";

    /**
     * The subject for all emails to be sent.
     */
    private static final String SUBJECT = "File Watcher Query Results";

    /**
     * The default body text for all emails to be sent.
     */
    private static final String BODY = "Attached is the query results CSV file from the File Watcher application.";

    /**
     * Represents the user's email address
     */
    private String myEmail;

    /**
     * String property for updating view when email is updated.
     */
    private final StringProperty myProperty;

    /**
     * Constructor for creating an Email object.
     *
     * @param theEmail the user's email address.
     */
    public Email(final String theEmail) {
        myProperty = new SimpleStringProperty();
        changeEmail(theEmail);
    }

    /**
     * Changes the current email address to the new one.
     *
     * @param theNewEmail the new email to be changed to.
     * @throws IllegalArgumentException if the email is empty
     */
    public void changeEmail(final String theNewEmail) {
        if (theNewEmail == null || theNewEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is empty!");
        }
        myEmail = theNewEmail.trim();
        myProperty.set(myEmail);
    }

    /**
     * Gets the current email address.
     *
     * @return the recipient's email address
     */
    public String getEmail() {
        return myEmail;
    }

    /**
     * Gets the email property for binding to the view.
     *
     * @return the email string property
     */
    public StringProperty emailProperty() {
        return myProperty;
    }

    /**
     * Sends an email with the specified message and file as an attachment.
     *
     * @param theMessage the message to be sent, or null to use default BODY
     * @param theFile the file to be sent, or null for no attachment
     * @throws MessagingException if email sending fails
     */
    public void sendEmail(final String theMessage, final File theFile) throws MessagingException {
        if (theFile != null && !theFile.exists()) {
            throw new IllegalArgumentException("CSV file does not exist");
        }

        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDING, PASSWORD);
            }
        });

        // Create the email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDING));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(myEmail));
        message.setSubject(SUBJECT);

        // Create the message part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(theMessage != null ? theMessage : BODY);

        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        // Add attachment if provided
        if (theFile != null) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(theFile);
            multipart.addBodyPart(attachmentPart);
        }

        message.setContent(multipart);

        // Send the email
        Transport.send(message);
    }
    session.setDebug(true);

}
