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

import com.google.api.services.gmail.model.Message;
import com.google.auth.Credentials;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

/**
 * Handles emailing functionality through Gmail
 *
 * @author Adin Smith
 * @version 4/28/2025
 */
public class Email {

    /**
     * The program's sending email address.
     */
    private static final String SENDING = "custom77429@gmail.com";

    /**
     * The subject for all emails to be sent.
     */
    private static final String SUBJECT = "FILE WATCHER UPDATE";

    /**
     * Represents the user's email address
     */
    private String myEmail;

    /**
     * String property for updating view when email is updated.
     */
    private javafx.beans.property.StringProperty myProperty;

    /**
     * Constructor for creating an Email object.
     *
     * @param theEmail the user's email address.
     */
    public Email(final String theEmail) {
        myProperty = new javafx.beans.property.SimpleStringProperty();
        changeEmail(theEmail);
    }

    /**
     * Sends an email with the given message and file
     *
     * @param theMessage the message to be sent.
     * @param theFile the file to be sent.
     */
    public void sendEmail(final String theMessage, final File theFile) {
        try {
            // Export the CSV file from the database if no file is provided
            File csvFile = theFile != null ? theFile : new File("query_results_" + System.currentTimeMillis() + ".csv");
            if (theFile == null) {
                DataBase.getDatabase().export(csvFile.getPath()); // Generate CSV if not provided
            }

            // Create email with attachment
            MimeMessage email = createEmailWithAttachment(myEmail, theMessage, csvFile);

            // Encode and send the email
            Message message = createMessageWithEmail(email);
            sendMessage(message);

        } catch (MessagingException | IOException e) {
            System.out.println("Error caught while sending email: " + e);
        }
    }

    /**
     * Create a MimeMessage using the parameters provided, including an attachment.
     *
     * @param theReceivingAddress email address of the receiver
     * @param theMessage the body text of the email
     * @param theFile the file to attach
     * @throws MessagingException if a wrongly formatted address is encountered.
     * @return the MimeMessage to be used to send email
     */
    private static MimeMessage createEmailWithAttachment(final String theReceivingAddress, final String theMessage, final File theFile)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(SENDING));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(theReceivingAddress));
        email.setSubject(SUBJECT);

        // Create multipart message
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(theMessage != null ? theMessage : "Attached are your query results from the File Watcher app.");
        multipart.addBodyPart(textPart);

        if (theFile != null && theFile.exists()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(theFile);
            attachmentPart.setFileName(theFile.getName());
            multipart.addBodyPart(attachmentPart);
        }

        email.setContent(multipart);
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
     * Sends the encoded message using the Gmail API.
     *
     * @param theMessage the encoded message to send
     * @throws IOException if API communication fails
     */
    private static <GoogleCredentials> void sendMessage(Message theMessage) throws IOException {
        // Load pre-authorized user credentials
        GoogleCredentials credentials;
        credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(GmailScopes.GMAIL_SEND);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter((Credentials) credentials);

        // Create the Gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("File Watcher")
                .build();

        try {
            // Send the message
            Message message = service.users().messages().send("me", theMessage).execute();
            System.out.println("Message id: " + message.getId());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }
}
