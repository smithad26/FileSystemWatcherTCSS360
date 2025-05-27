package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles SQLite database operations for file events.
 */
public class DataBase {
    /**
     * Only instance of the DataBase class (singleton)
     */
    private static final DataBase DATABASE = new DataBase();

    /** SQLite connection string (creates file if not exists). */
    private static final String DB_URL = "jdbc:sqlite:file_watcher.db";

    /** Connection to the SQLite database. */
    private Connection conn;

    /**
     * StringProperty to notify the View when the DataBase has been updated.
     */
    private final StringProperty myChanges;

    /**
     * List of events to be added to the DataBase.
     */
    private final List<String> myEvents;

    /**
     * Constructs the database and sets up the connection.
     */
    private DataBase() {
        myChanges = new SimpleStringProperty();
        myEvents = new LinkedList<>();

        try {
            conn = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e);
        }
    }

    /**
     * Creates the events table if it doesn't exist.
     *
     * @throws SQLException if a SQL error occurs
     */
    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS events (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Filename TEXT," +
                "Event TEXT," +
                "Timestamp TEXT," +
                "Extension TEXT, " +
                "Directory TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Writes the Events in myEvents to the database.
     */
    public void writeEvents() {
        String insertion = "INSERT INTO events(Filename, Event, Timestamp, Extension, Directory) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(insertion)) {
            createTableIfNotExists();
            while (!myEvents.isEmpty()) {

                // Convert String of events into array to get individual elements
                String[] event = myEvents.removeFirst().split(", ");

                // Set individual elements to the insertion
                for (int i = 0; i < event.length; i++) {
                    statement.setString(i + 1, event[i]);
                }

                // Insert final result
                statement.execute();
            }
        } catch (SQLException e) {
            System.out.println("Error caught in DataBase: " + e);
        }
    }

    /**
     * Checks if the events have already been written to the DataBase.
     *
     * @return if the events list is empty (events have been written).
     */
    public boolean isWritten() {
        return myEvents.isEmpty();
    }

    /**
     * Queries all stored file events from the database.
     *
     * @return result set of all file events
     * @throws SQLException if query fails
     */
    public ResultSet queryAllEvents() throws SQLException {
        String sql = "SELECT * FROM file_events ORDER BY timestamp DESC";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException if closing fails
     */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /**
     * Getter to get the DataBase instance (singleton).
     *
     * @return the only instance of the DataBase class.
     */
    public static DataBase getDatabase() {
        return DATABASE;
    }

    /**
     * Adds the given event to the myEvents list.
     *
     * @param theEvent to be added.
     * @throws IllegalArgumentException if the given event is null or empty
     */
    public void addEvent(final String theEvent) {
        if (theEvent.isEmpty()) {
            throw new IllegalArgumentException("Invalid event to be added");
        }
        myEvents.add(theEvent);
    }

    /**
     * Adds the given listener to the changes field.
     *
     * @param theListener the listener to be added.
     */
    public void addListener(final ChangeListener<String> theListener) {
        myChanges.addListener(theListener);
    }
}
