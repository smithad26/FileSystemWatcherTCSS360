/*
 * TCSS 360 Course Project
 */

package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    private static final String SENDING = "something"; // placeholder for now

    /**
     * Password for the program's sending email address
     * (gmail requires a password).
     */
    private static final String PASSWORD = "123456789"; // placeholder for now

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
        setEmail(theEmail);
    }

    /**
     * Changes the current email address to a new one.
     *
     * @param theEmail the new email to be set to.
     */
    public void setEmail(final String theEmail) {
        if (theEmail.isEmpty()) {
            throw new IllegalArgumentException("Cannot set email: " + theEmail);
        }
        myEmail = theEmail;
        myProperty.set(theEmail);
    }

    /**
     * Sends an email with the given message
     */
    private void sendEmail(final String theMessage) {
        // TBA
    }
}
