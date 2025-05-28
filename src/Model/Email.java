/*
 * TCSS 360 Course Project
 */

package Model;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Draft;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This class handles sending emails through Gmail using a preset sender account.
 * It uses JavaMail API to construct and encode email messages.
 * 
 * @author 
 * @version 4/28/2025
 */
public class Email {

    /** The Gmail address used to send all outgoing emails. */
    private static final String SENDER_EMAIL = "custom77429@gmail.com";

    /** App-specific password for the Gmail sender account. */
    private static final String APP_PASSWORD = "yrrg jopn xiiq zzhs";

    /** Default subject for all emails sent by this program. */
    private static final String DEFAULT_SUBJECT = "FILE WATCHER UPDATE";

    /** Default body text for emails (can be customized later). */
    private static final String DEFAULT_BODY = "peepee poopoo hahhaha";

    /** Stores the recipient's email address. */
    private String myEmail;

    /** Used to notify listeners when the email address changes (e.g., in the GUI). */
    private final StringProperty myProperty;

    /**
     * Constructs an Email object and sets the initial email address.
     *
     * @param theEmail the initial recipient email address
     * @throws IllegalArgumentException if the provided email is empty
     */
    public Email(final String theEmail) {
        myProperty = new SimpleStringProperty();
        changeEmail(theEmail);
    }

    /**
     * Changes the recipient email address and updates the view property.
     *
     * @param theNewEmail the new email address to use
     * @throws IllegalArgumentException if the email is empty
     */
    public void changeEmail(final String theNewEmail) {
        if (theNewEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }
        myEmail = theNewEmail;
        myProperty.set(myEmail);
    }

    /**
     * Sends an email to the current recipient. Includes an optional file attachment.
     *
     * @param theMessage the message content (currently unused)
     * @param theFile the file to attach (currently unused)
     */
    public void sendEmail(final String theMessage, final File theFile) {
        try {
            // Create the email message
            final MimeMessage email = createEmail(myEmail);

            // Convert the email into a base64url-encoded Gmail message
            final Message message = createMessageWithEmail(email);

            // TODO: Send the message using Gmail API (not yet implemented)

        } catch (final MessagingException | IOException e) {
            System.out.println("Error while sending email: " + e.getMessage());
        }
    }

    /**
     * Creates a basic email message with a subject and body text.
     *
     * @param theReceivingAddress the recipient email address
     * @return the constructed MimeMessage
     * @throws MessagingException if the email address is invalid or creation fails
     */
    private static MimeMessage createEmail(final String theReceivingAddress)
            throws MessagingException {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        final MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(SENDER_EMAIL));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(theReceivingAddress));
        email.setSubject(DEFAULT_SUBJECT);
        email.setText(DEFAULT_BODY);

        return email;
    }

    /**
     * Converts a MimeMessage into a Gmail-compatible Message by encoding it.
     *
     * @param theMessage the MimeMessage to encode
     * @return the encoded Gmail message
     * @throws MessagingException if message formatting fails
     * @throws IOException if output stream fails
     */
    private static Message createMessageWithEmail(final MimeMessage theMessage)
            throws MessagingException, IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        theMessage.writeTo(buffer);
        final byte[] bytes = buffer.toByteArray();
        final String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        final Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Creates a Gmail draft message with an attached file.
     *
     * @param theFile the file to attach (currently unused)
     * @param theMessage the encoded Gmail message
     * @return a new draft (currently a placeholder)
     * @throws MessagingException if message creation fails
     * @throws IOException if file processing fails
     */
    private static Draft createDraftMessage(final File theFile, final Message theMessage)
            throws MessagingException, IOException {
        // TODO: Add attachment and return a proper draft
        return new Draft();
    }
}
