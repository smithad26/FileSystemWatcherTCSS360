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
import java.util.List;

/**
 * Handles SQLite database operations for file events.
 */
public class DataBase {

    /** SQLite connection string (creates file if not exists). */
    private static final String DB_URL = "jdbc:sqlite:file_watcher.db";

    /** Connection to the SQLite database. */
    private Connection conn;

    /**
     * StringProperty to notify the View when the DataBase has been updated.
     */
    private final StringProperty myChanges;

    /**
     * Constructs the database and sets up the connection.
     */
    public DataBase() {
        myChanges = new SimpleStringProperty();
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
        String sql = "CREATE TABLE IF NOT EXISTS file_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "file_path TEXT NOT NULL," +
                "event_type TEXT NOT NULL," +
                "timestamp TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Writes a list of file events to the database.
     *
     * @param events list of events to store
     * @throws SQLException if writing fails
     */
//    public void writeEvents(List<FileEvent> events) throws SQLException {
//        String sql = "INSERT INTO file_events (file_path, event_type, timestamp) VALUES (?, ?, ?)";
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            for (FileEvent event : events) {
//                pstmt.setString(1, event.getFilePath());
//                pstmt.setString(2, event.getEventType());
//                pstmt.setString(3, event.getTimestamp());
//                pstmt.addBatch();
//            }
//            pstmt.executeBatch();
//        }
//    }

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
     * Adds the given listener to the changes field.
     *
     * @param theListener the listener to be added.
     */
    public void addListener(ChangeListener<String> theListener) {
        myChanges.addListener(theListener);
    }
}
