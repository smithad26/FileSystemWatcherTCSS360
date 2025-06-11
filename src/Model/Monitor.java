/*
 * TCSS 360 Course Project
 */

package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

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
     * ObservableList for updating the view with events
     */
    private final ObservableList<Event> myEvents;

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
     * Represents the current file extension to monitor
     */
    private String myExtension;

    /**
     * Constructor for Monitor object
     */
    private Monitor() {
        myKeys = new Hashtable<>();
        myEvents = FXCollections.observableArrayList();
        myWatcher = null;
        try {
            myWatcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            System.out.println("Error caught in Monitor constructor: " + e);
        }
        myRunning = false;
        myExtension = "none";
    }

    /**
     * Adds the given path to file to the map.
     *
     * @param thePath the given path to be added.
     * @throws IOException if the given path is invalid.
     */
    public void addFile(final String thePath) throws IOException {

        Path path = Paths.get(thePath);
        walkThroughDir(path);
    }

    /**
     * registers the given path to the myKeys map.
     *
     * @param theDir the given path to register.
     * @throws IOException if exception occurs
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
     * @throws IOException if exception occurs
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

                    // Check if the file matches the monitoring extension
                    // and skip if necessary
                    String extension = getFileExtension(child.toString());
                    if (!extension.equalsIgnoreCase(myExtension) &&
                            !myExtension.equalsIgnoreCase("none")) {
                        continue;
                    }

                    // fire property change and add to database
                    // Filename, Event, Timestamp, Extension, Directory
                    Event out = new Event(
                            event.context().toString(),
                            event.kind().name(),
                            Instant.now().toString(),
                            extension,
                            child.toString());

                    myEvents.add(out);

                    // if directory is created, and watching recursively,
                    // then register it and its sub-directories
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
     * Helper method to get the file extension.
     *
     * @param theFileName the name of the file to get the extension from.
     * @return the provided file's extension.
     */
    private String getFileExtension(final String theFileName) {
        int dotIndex = theFileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : theFileName.substring(dotIndex);
    }

    /**
     * Changes the file extension to monitor.
     *
     * @param theExtension the new file extension to monitor.
     * @throws NullPointerException if the given extension is null.
     */
    public void changeExtension(final String theExtension) {
        Objects.requireNonNull(theExtension);

        myExtension = theExtension;
    }

    /**
     * Returns the ObservableList to be bound to the MainView
     *
     * @return the myEvents ObservableList
     */
    public ObservableList<Event> getEvents() {
        return myEvents;
    }

}
