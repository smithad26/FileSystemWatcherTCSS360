/*
 * TCSS 360 Course Project
 */
package Model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class Email {

    /**
     * The application's name for Google API identification.
     */
    private static final String APPLICATION_NAME = "File Watcher";

    /**
     * JSON factory using Gson instead of Jackson.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Directory to store OAuth 2.0 tokens.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Path to the client secrets JSON file (obtained from Google Cloud Console).
     */
    private static final String CREDENTIALS_FILE_PATH = "credentials.json"; // Update with your credentials file path

    /**
     * Represents the recipient's email address.
     */
    private String myEmail;

    /**
     * Constructor for creating an Email object.
     *
     * @param theEmail the recipient's email address.
     */
    public Email(final String theEmail) {
        if (theEmail == null || theEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is empty or null!");
        }
        myEmail = theEmail.trim();
    }

    /**
     * Gets the current recipient email address.
     *
     * @return the recipient email address.
     */
    public String getEmail() {
        return myEmail;
    }

    /**
     * Sets a new recipient email address.
     *
     * @param theNewEmail the new email address.
     */
    public void setEmail(final String theNewEmail) {
        if (theNewEmail == null || theNewEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is empty or null!");
        }
        myEmail = theNewEmail.trim();
    }

    /**
     * Retrieves credentials for the Gmail API using OAuth 2.0.
     *
     * @param HTTP_TRANSPORT the network HTTP transport.
     * @return the OAuth 2.0 credential.
     * @throws IOException if the credentials file cannot be loaded or authorization fails.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        try (InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH)) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList("https://www.googleapis.com/auth/gmail.send"))
                    .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
        }
    }

    /**
     * Sends an email with the specified message and CSV file as an attachment using the Gmail API.
     *
     * @param theMessage the body text of the email, or null for default.
     * @param theFile the CSV file to attach, or null for no attachment.
     * @throws IOException if file access or API communication fails.
     * @throws GeneralSecurityException if security issues occur during authorization.
     */
    public void sendEmail(final String theMessage, final File theFile) throws IOException, GeneralSecurityException {
        if (theFile != null) {
            if (!theFile.exists()) {
                throw new IllegalArgumentException("CSV file does not exist: " + theFile.getAbsolutePath());
            }
            if (!theFile.getName().toLowerCase().endsWith(".csv")) {
                throw new IllegalArgumentException("File must be a .csv file: " + theFile.getName());
            }
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Construct the raw email message
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("To: ").append(myEmail).append("\r\n");
        emailContent.append("Subject: File Watcher Query Results\r\n");
        emailContent.append("MIME-Version: 1.0\r\n");
        emailContent.append("Content-Type: multipart/mixed; boundary=\"boundary123\"\r\n");
        emailContent.append("\r\n");
        emailContent.append("--boundary123\r\n");
        emailContent.append("Content-Type: text/plain; charset=\"UTF-8\"\r\n");
        emailContent.append("Content-Transfer-Encoding: 7bit\r\n");
        emailContent.append("\r\n");
        emailContent.append(theMessage != null ? theMessage : "Attached is your CSV file from the File Watcher app.\r\n");
        emailContent.append("\r\n");

        if (theFile != null) {
            emailContent.append("--boundary123\r\n");
            emailContent.append("Content-Type: application/octet-stream; name=\"").append(theFile.getName()).append("\"\r\n");
            emailContent.append("Content-Transfer-Encoding: base64\r\n");
            emailContent.append("Content-Disposition: attachment; filename=\"").append(theFile.getName()).append("\"\r\n");
            emailContent.append("\r\n");
            // Read file and encode to base64
            try (FileInputStream fis = new FileInputStream(theFile)) {
                byte[] fileBytes = new byte[(int) theFile.length()];
                fis.read(fileBytes);
                emailContent.append(java.util.Base64.getEncoder().encodeToString(fileBytes)).append("\r\n");
            }
            emailContent.append("--boundary123--");
        }

        // Convert to Base64 for Gmail API
        byte[] rawMessageBytes = emailContent.toString().getBytes("UTF-8");
        String encodedEmail = java.util.Base64.getUrlEncoder().encodeToString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        // Send the email
        service.users().messages().send("me", message).execute();
    }
}
