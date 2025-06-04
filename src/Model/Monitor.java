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
 * The {@code Monitor} class uses the WatchService API to monitor file system changes
 * in specified directories. It follows the Singleton design pattern to ensure only one
 * instance is used throughout the application.
 * <p>
 * Detected file system events are recorded as {@code Event} objects.
 * </p>
 * 
 * <p>Thread-safe and JavaFX-friendly through observable properties.</p>
 * 
 * @author Marcus Nguyen
 * @version 6/3/2025
 */
public final class Monitor {

    /** The singleton instance of this class. */
    private static final Monitor MONITOR = new Monitor();

    /** Reference to the application’s database. */
    private static final DataBase DATABASE = DataBase.getDatabase();

    /** Map linking WatchKeys to their associated Paths. */
    private final Map<WatchKey, Path> myKeys;

    /** Observable list of events to update the view. */
    private final ObservableList<Event> myEvents;

    /** The WatchService instance used for monitoring. */
    private WatchService myWatcher;

    /** The thread used for background monitoring. */
    private Thread myMonitorThread;

    /** A flag indicating whether the monitoring process is running. */
    private volatile boolean myRunning;

    /** The file extension currently being monitored. */
    private String myExtension;

    /**
     * Private constructor to enforce Singleton usage.
     * Initializes internal data structures and attempts to set up the WatchService.
     */
    private Monitor() {
        myKeys = new Hashtable<>();
        myEvents = FXCollections.observableArrayList();
        myExtension = "none";

        try {
            myWatcher = FileSystems.getDefault().newWatchService();
        } catch (final IOException e) {
            System.err.println("Failed to initialize WatchService: " + e.getMessage());
        }

        myRunning = false;
    }

    /**
     * Returns the single instance of the Monitor class.
     *
     * @return the Monitor instance.
     */
    public static Monitor getMonitor() {
        return MONITOR;
    }

    /**
     * Adds a directory and all its subdirectories to be monitored.
     *
     * @param thePath the path of the directory to add.
     */
    public void addFile(final String thePath) {
        try {
            final Path path = Paths.get(thePath);
            walkThroughDir(path);
        } catch (final IOException e) {
            System.err.println("Error adding file: " + e.getMessage());
        }
    }

    /**
     * Registers a directory to be monitored for changes.
     *
     * @param theDir the directory to register.
     * @throws IOException if registration fails.
     */
    private void registerDirectory(final Path theDir) throws IOException {
        final WatchKey watchKey = theDir.register(myWatcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
        myKeys.put(watchKey, theDir);
    }

    /**
     * Recursively walks through a directory and registers all subdirectories.
     *
     * @param theStart the root path to begin from.
     * @throws IOException if an I/O error occurs.
     */
    private void walkThroughDir(final Path theStart) throws IOException {
        Files.walkFileTree(theStart, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Starts the background monitoring thread.
     */
    public void startMonitoring() {
        myRunning = true;
        myMonitorThread = new Thread(this::monitoring);
        myMonitorThread.setDaemon(true);
        myMonitorThread.start();
    }

    /**
     * Stops the background monitoring thread gracefully.
     */
    public void stopMonitoring() {
        myRunning = false;
        if (myMonitorThread != null) {
            myMonitorThread.interrupt();
        }
    }

    /**
     * Main monitoring logic that listens for file system events and processes them.
     */
    private void monitoring() {
        WatchKey key;
        try {
            while (myRunning && (key = myWatcher.take()) != null) {
                final Path dir = myKeys.get(key);
                if (dir == null) {
                    System.err.println("Unrecognized WatchKey.");
                    continue;
                }

                for (final WatchEvent<?> event : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    final Path name = ((WatchEvent<Path>) event).context();
                    final Path child = dir.resolve(name);

                    final String extension = getFileExtension(child.toString());
                    if (!extension.equalsIgnoreCase(myExtension)
                            && !myExtension.equalsIgnoreCase("none")) {
                        continue;
                    }

                    final Event out = new Event(
                            name.toString(),
                            kind.name(),
                            Instant.now().toString(),
                            extension,
                            child.toString()
                    );

                    myEvents.add(out);
                    DATABASE.addEvent(out);

                    if (kind == ENTRY_CREATE && Files.isDirectory(child)) {
                        try {
                            walkThroughDir(child);
                        } catch (final IOException e) {
                            System.err.println("Error walking new directory: " + e.getMessage());
                        }
                    }
                }

                final boolean valid = key.reset();
                if (!valid) {
                    myKeys.remove(key);
                    if (myKeys.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Monitor interrupted: " + e.getMessage());
        }
    }

    /**
     * Extracts the file extension from a filename.
     *
     * @param theFileName the name of the file.
     * @return the file extension, or an empty string if none found.
     */
    private String getFileExtension(final String theFileName) {
        final int dotIndex = theFileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : theFileName.substring(dotIndex);
    }

    /**
     * Changes the file extension that this monitor listens for.
     *
     * @param theExtension the new extension to monitor (e.g., ".txt", ".java").
     * @throws NullPointerException if the extension is {@code null}.
     */
    public void changeExtension(final String theExtension) {
        Objects.requireNonNull(theExtension, "Extension must not be null.");
        myExtension = theExtension;
    }

    /**
     * Returns the list of recorded events for external viewing or binding.
     *
     * @return the observable list of events.
     */
    public ObservableList<Event> getEvents() {
        return myEvents;
    }
}
