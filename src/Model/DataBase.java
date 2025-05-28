package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles saving and retrieving file events in an SQLite database.
 * This class uses the Singleton design pattern.
 */
public class DataBase {
    
    /** The one and only instance of this class. */
    private static final DataBase DATABASE = new DataBase();

    /** URL for the database file. It will be created if it doesn't exist. */
    private static final String DB_URL = "jdbc:sqlite:file_watcher.db";

    /** Connection to the SQLite database. */
    private Connection myConnection;

    /** Used to let the View know when changes happen in the database. */
    private final StringProperty myChanges;

    /** List of file events waiting to be saved in the database. */
    private final List<String> myEvents;

    /**
     * Private constructor to prevent outside classes from creating instances.
     * It connects to the database and creates the table if needed.
     */
    private DataBase() {
        myChanges = new SimpleStringProperty();
        myEvents = new LinkedList<>();

        try {
            myConnection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
        } catch (final SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    /**
     * Creates the table in the database if it doesn't already exist.
     *
     * @throws SQLException if there's a problem creating the table
     */
    private void createTableIfNotExists() throws SQLException {
        final String sql = """
            CREATE TABLE IF NOT EXISTS events (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                Filename TEXT,
                Event TEXT,
                Timestamp TEXT,
                Extension TEXT,
                Directory TEXT
            )""";

        try (Statement stmt = myConnection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Saves all file events from the list into the database.
     */
    public void writeEvents() {
        final String sql = "INSERT INTO events(Filename, Event, Timestamp, Extension, Directory) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement statement = myConnection.prepareStatement(sql)) {
            createTableIfNotExists();

            while (!myEvents.isEmpty()) {
                final String[] eventParts = myEvents.removeFirst().split(", ");

                for (int i = 0; i < eventParts.length; i++) {
                    statement.setString(i + 1, eventParts[i]);
                }

                statement.execute();
            }
        } catch (final SQLException e) {
            System.out.println("Failed to write events: " + e.getMessage());
        }
    }

    /**
     * Checks if all events have already been saved.
     *
     * @return true if there are no events left to save; false otherwise
     */
    public boolean isWritten() {
        return myEvents.isEmpty();
    }

    /**
     * Retrieves all events stored in the database.
     *
     * @return a ResultSet containing all saved file events
     * @throws SQLException if the query fails
     */
    public ResultSet queryAllEvents() throws SQLException {
        final String sql = "SELECT * FROM events ORDER BY Timestamp DESC";
        final Statement stmt = myConnection.createStatement();
        return stmt.executeQuery(sql);
    }

    /**
     * Closes the connection to the database.
     *
     * @throws SQLException if closing the connection fails
     */
    public void close() throws SQLException {
        if (myConnection != null && !myConnection.isClosed()) {
            myConnection.close();
        }
    }

    /**
     * Gets the single instance of the DataBase class.
     *
     * @return the DataBase instance
     */
    public static DataBase getDatabase() {
        return DATABASE;
    }

    /**
     * Adds a file event to the list for saving later.
     *
     * @param theEvent the event string (must not be empty)
     * @throws IllegalArgumentException if the event is empty
     */
    public void addEvent(final String theEvent) {
        if (theEvent.isEmpty()) {
            throw new IllegalArgumentException("Event cannot be empty");
        }
        myEvents.add(theEvent);
    }

    /**
     * Lets another class listen for changes to the database.
     *
     * @param theListener the listener that gets notified on changes
     */
    public void addListener(final ChangeListener<String> theListener) {
        myChanges.addListener(theListener);
    }
}
