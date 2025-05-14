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
 * Handles emailing functionality through Gmail
 *
 * @author Adin Smtih
 * @version 4/28/2025
 */
public class Email {

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
    private static final String BODY = "peepee poopoo hahhaha";

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

    /**
     * Sends an email with the given message and file
     *
     * @param theMessage the message to be sent.
     * @param theFile the file to be sent.
     */
    public void sendEmail(final String theMessage, final File theFile) {
        try {

            // Create email
            MimeMessage email = createEmail(myEmail);

            // Encode email
            Message message = createMessageWithEmail(email);

            //

        } catch (MessagingException | IOException e) {
            System.out.println("Error caught while sending email: " + e);
        }
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param theReceivingAddress email address of the receiver
     * @throws MessagingException if a wrongly formatted address is encountered.
     * @return the MimeMessage to be used to send email
     */
    private static MimeMessage createEmail(final String theReceivingAddress)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(SENDING));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO,
                new InternetAddress(theReceivingAddress));
        email.setSubject(SUBJECT);
        email.setText(BODY);

        return email;
    }

    /**
     * Encodes the MimeMessage and returns a message to be sent.
     *
     * @throws MessagingException if a wrongly formatted address is encountered.
     * @throws IOException if service account credentials file is not found.
     * @return the new base64url encoded message.
     */
    private static Message createMessageWithEmail(final MimeMessage theMessage)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        theMessage.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Creates a draft message with the attached file.
     *
     * @param theFile the file to be attached.
     * @param theMessage the encoded message.
     * @throws MessagingException if a wrongly formatted address is encountered.
     * @throws IOException if service account credentials file is not found.
     * @return a draft message.
     */
    private static Draft createDraftMessage(final File theFile, final Message theMessage)
            throws MessagingException, IOException {

        return new Draft();
    }


}
