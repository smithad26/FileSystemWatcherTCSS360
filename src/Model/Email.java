/*
 * TCSS 360 Course Project
 */

package Model;


import java.time.Instant;
import java.util.Properties;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import java.io.File;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMultipart;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Handles emailing functionality through Gmail
 *
 * @author Adin Smtih
 * @version 4/28/2025
 */
public class Email {


    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    /**
     * The program's sending email address.
     */
    private static final String SENDING = "custom77429@gmail.com";

    /**
     * Generated app password for the program's sending email address
     * (gmail requires a password).
     */
    private static final String PASSWORD = "yrrg jopn xiiq zzhs";

    /**
     * The subject for all emails to be sent.
     */
    private static final String SUBJECT = "FILE WATCHER UPDATE";

    /**
     * The body text for all emails to be sent.
     */
    private static final String BODY = "Here is the report prepared on: " + Instant.now();

    /**
     * Represents the user's email address
     */
    private String myEmail;

    /**
     * String property for updating view when email is updated.
     */
    private StringProperty myProperty;

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
     */
    public void changeEmail(final String theNewEmail) {
        if (theNewEmail.isEmpty()) {
            throw new IllegalArgumentException("Email is empty!");
        }
        myEmail = theNewEmail;
        myProperty.set(myEmail);
    }


    public void sendEmailWithAttachment(final String theFilePath) {
        // Set up email properties
        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        // Authenticate using username and password
        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDING, PASSWORD);
            }
        });

        try {
            // Check if the file path is null or file doesn't exist
            if (theFilePath == null || theFilePath.isEmpty()) {
                throw new IllegalArgumentException("File path is null or empty.");
            }

            final File file = new File(theFilePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist: " + theFilePath);
            }

            // Create email message
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDING));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(myEmail));
            message.setSubject(SUBJECT);

            // Create a multipart message
            final Multipart multipart = new MimeMultipart();

            // Add email text
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(BODY);
            multipart.addBodyPart(textPart);

            // Add file attachment
            final MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(file)));
            attachmentPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentPart);

            // Set the complete message parts
            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully with attachment: " + theFilePath);

        } catch (final MessagingException e) { // Catch messaging exceptions
            e.printStackTrace(); // Log the stack trace for further analysis
        } catch (final IllegalArgumentException e) { // Catch illegal argument exceptions
            // Handle invalid file path exception
        } catch (final Exception e) { // Catch any other unexpected exceptions
            // Catch any other unexpected exceptions
            e.printStackTrace(); // Log the stack trace for debugging
        }
    }

}
