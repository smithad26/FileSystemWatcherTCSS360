/*
 * TCSS 360 Course Project
 */

package Model;

import java.util.Objects;

/**
 * Represents the information regarding an event that happens.
 *
 * @author Adin Smtih
 * @version 6/13/2025
 */
public class Event implements Comparable<Event> {

    /**
     * Represents the name of the file.
     */
    private final String myFilename;

    /**
     * Represents the event type.
     */
    private final String myEventType;

    /**
     * Represents the time the event took place.
     */
    private final String myTimestamp;

    /**
     * Represents the extension of the file.
     */
    private final String myExtension;

    /**
     * Represents the directory of the file.
     */
    private final String myDirectory;

    /**
     * Creates a new event object.
     *
     * @param theFilename the name of the file affect.
     * @param theEventType the type of event.
     * @param theTimestamp the date the event occurred.
     * @param theExtension the extension of the file.
     * @param theDirectory the directory of the file.
     *
     * @throws NullPointerException if any given argument is null.
     */
    public Event(final String theFilename, final String theEventType,
                 final String theTimestamp, final String theExtension, final String theDirectory) {
        Objects.requireNonNull(theFilename);
        Objects.requireNonNull(theEventType);
        Objects.requireNonNull(theTimestamp);
        Objects.requireNonNull(theExtension);
        Objects.requireNonNull(theDirectory);

        myFilename = theFilename;
        myEventType = theEventType;
        myTimestamp = theTimestamp;
        myExtension = theExtension;
        myDirectory = theDirectory;
    }

    /**
     * Returns the name of the file.
     *
     * @return the name of the file.
     */
    public String getFilename() { return myFilename; }

    /**
     * Returns the event type of the file.
     *
     * @return the event type of the file.
     */
    public String getEventType() { return myEventType; }

    /**
     * Returns the timestamp of the file.
     *
     * @return the timestamp of the file.
     */
    public String getTimestamp() { return myTimestamp; }

    /**
     * Returns the extension of the file.
     *
     * @return the extension of the file.
     */
    public String getExtension() { return myExtension; }

    /**
     * Returns the directory of the file.
     *
     * @return the directory of the file.
     */
    public String getDirectory() { return myDirectory; }

    /**
     * Overrides Java's toString method to produce meaningful output and
     * to make displaying results easier.
     *
     * @return a String formatted nicely representing the current event.
     */
    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s",
                myFilename,
                myEventType,
                myTimestamp,
                myExtension,
                myDirectory);
    }

    /**
     * Overrides Java's compareTo method to make events comparable for unit testing.
     *
     * @param theOther the object to be compared.
     * @return whether the two events are the same or not.
     */
    @Override
    public int compareTo(Event theOther) {
        if (this.toString().equals(theOther.toString())) {
            return 0;
        } else {
            return this.toString().length() - theOther.toString().length();
        }
    }
}

