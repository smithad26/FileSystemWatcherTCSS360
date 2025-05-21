/*
 * TCSS 360 Course Project
 */

package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
    private static final Monitor MONITOR = new Monitor();

    /**
     * Map containing keys of each path to file to be monitored.
     */
    private final Map<WatchKey, Path> myKeys;

    /**
     * StringProperty for updating the view with events
     */
    private final StringProperty myEvents;

    /**
     * Watch service field
     */
    private WatchService myWatcher;

    /**
     * A thread representing the separate monitoring task to be run.
     */
    private Thread myMonitorThread;

    /**
     * A volatile boolean used to stop running the monitoring.
     */
    private volatile boolean myRunning;

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
        myRunning = false;
    }

    /**
     * Adds the given path to file to the map.
     *
     * @param thePath the given path to be added.
     */
    public void addFile(final String thePath) {
        try {
            Path path = Paths.get(thePath);
            walkThroughDir(path);
        } catch (IOException e) {
            System.out.println("Error caught in Monitor addFile: " + e);
        }
    }

    /**
     * registers the given path to the myKeys map.
     *
     * @param theDir the given path to register.
     * @throws IOException
     */
    private void registerDirectory(final Path theDir) throws IOException {
        WatchKey watchkey = theDir.register(myWatcher,
                ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
        myKeys.put(watchkey, theDir);
    }

    /**
     * Recursively adds and registers all files and subdirectories to the myKeys map.
     *
     * @param theStart the starting path to walk through.
     * @throws IOException if
     */
    private void walkThroughDir(final Path theStart) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(theStart, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Starts monitoring
     */
    public void startMonitoring() {
        myRunning = true;
        myMonitorThread = new Thread(this::monitoring);
        myMonitorThread.setDaemon(true);
        myMonitorThread.start();
    }

    /**
     * Stops monitoring
     */
    public void stopMonitoring() {
        myRunning = false;
        if (myMonitorThread != null) {
            myMonitorThread.interrupt();
        }
    }

    /**
     * Getter for getting the only instance of this class.
     *
     * @return the only instance of the monitor class.
     */
    public static Monitor getMonitor() {
        return MONITOR;
    }

    /**
     * Getter for myEvents for binding the MianView in the controller
     *
     * @return the events StringProperty
     */
    public StringProperty getEvents() {
        return myEvents;
    }

    /**
     * Monitors for events and fires a property change when an event occurs.
     */
    private void monitoring() {
           WatchKey key;
           try {
               while (myRunning && (key = myWatcher.take()) != null) {
                   Path dir = myKeys.get(key);
                   if (dir == null) {
                       System.err.println("WatchKey not recognized!");
                       continue;
                   }
                   for (WatchEvent<?> event : key.pollEvents()) {
                       WatchEvent.Kind<?> kind = event.kind();

                       // Context for directory entry event is the file name of
                       @SuppressWarnings("unchecked")
                       Path name = ((WatchEvent<Path>) event).context();
                       Path child = dir.resolve(name);

                       // fire property change
                       String out = String.format("%s: %s\n", event.kind().name(), child);
                       myEvents.set(out);
                       System.out.format(out);

                       // if directory is created, and watching recursively, then register it and its sub-directories
                       if (kind == ENTRY_CREATE) {
                           try {
                               if (Files.isDirectory(child)) {
                                   walkThroughDir(child);
                               }
                           } catch (IOException x) {
                               // do something useful
                               System.out.println("Error processing events: " + x);
                           }
                       }
                   }
                   // reset key and remove from set if directory no longer accessible
                   boolean valid = key.reset();
                   if (!valid) {
                       myKeys.remove(key);

                       // all directories are inaccessible
                       if (myKeys.isEmpty()) {
                           break;
                       }
                   }
               }
           } catch (InterruptedException e) {
               System.out.println("Error caught in Monitor monitoring: " + e);
           }
    }

    /**
     * Adds the given listener to the events field.
     *
     * @param theListener the given listener to be added.
     */
    public void addEventHandler(final ChangeListener<String> theListener) {
        myEvents.addListener(theListener);
    }

    /**
     * Removes the given listener from the vents field.
     *
     * @param theListener the listener to be removed.
     */
    public void removeEventHandler(ChangeListener<String> theListener) {
        myEvents.removeListener(theListener);
    }
}
