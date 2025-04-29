/*
 * TCSS 360 Course Project
 */

package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.nio.file.*;
import java.util.Hashtable;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Monitor class uses WatchService library for monitoring files and directories.
 * There should only be one instance of this class for the whole program.
 *
 * @author Adin Smith
 * @version 4/28/2025
 */
public class Monitor {

    /**
     * Global access point to class (singleton)
     */
    public static final Monitor MONITOR = new Monitor();

    /**
     * Map containing keys of each path to file to be monitored.
     */
    private final Map<WatchKey, Path> myKeys; // might be used later for something

    /**
     * StringProperty for updating the view with events
     */
    private final StringProperty myEvents;

    /**
     * Watch service field
     */
    private WatchService myWatcher;

    /**
     * Determines if monitoring or not
     */
    private boolean myIsMonitoring;

    /**
     * Constructor for Monitor object
     */
    private Monitor() {
        myKeys = new Hashtable<>();
        myEvents = new SimpleStringProperty();
        myWatcher = null;
        try {
            myWatcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            System.out.println("Error caught in Monitor constructor: " + e);
        }
        myIsMonitoring = false;
    }

    /**
     * Adds the given path to file to the map.
     *
     * @param thePath the given path to be added.
     */
    public void addFile(final String thePath) {
        try {
            Path path = Paths.get(thePath);
            WatchKey watchkey = path.register(myWatcher,
                    ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
            myKeys.put(watchkey, path);
        } catch (IOException e) {
            System.out.println("Error caught in Monitor addFile: " + e);
        }
    }

    /**
     * Starts monitoring
     */
    public void startMonitoring() {
        myIsMonitoring = true;
        monitoring();
    }

    /**
     * Stops monitoring
     */
    public void stopMonitoring() {
        myIsMonitoring = false;
    }

    /**
     * Monitors for events and fires a property change when an event occurs.
     */
    private void monitoring() {
       while (myIsMonitoring) {
           WatchKey key;
           try {
               while ((key = myWatcher.take()) != null) {
                   for (WatchEvent<?> event : key.pollEvents()) {
                       // change property to notify GUI listeners to display event
                       myEvents.set("Event kind: " + event.kind() +
                               ". File affected: " + event.context());
                   }
                   key.reset();
               }
           } catch (InterruptedException e) {
               System.out.println("Error caught in Monitor monitoring: " + e);
           }
       }
    }
}
