/*
 * TCSS 360 Course Project
 */

package Model;

import com.opencsv.CSVWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.Objects;

/**
 * Handles SQLite database operations for file events.
 *
 * @author Adin Smith
 * @author Marcus Nguyen
 * @version 6/12/2025
 */
public class DataBase {
    /**
     * Only instance of the DataBase class (singleton)
     */
    private static final DataBase DATABASE = new DataBase();

    /** SQLite connection string (creates file if not exists). */
    private static final String DB_URL = "jdbc:sqlite:file_watcher.db";

    /** Connection to the SQLite database. */
    private Connection myConn;

    /**
     * List of results from query to be displayed to the QueryView and to be
     * saved into a csv file.
     */
    private final ObservableList<Event> myQuery;

    /**
     * Constructs the database and sets up the connection.
     */
    private DataBase() {

        myQuery = FXCollections.observableArrayList();

        try {
            myConn = DriverManager.getConnection(DB_URL);
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
        try (Statement stmt = myConn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Writes the Events in myEvents to the database.
     */
    public void writeEvents() {
        String insertion = "INSERT INTO events(Filename, Event, " +
                "Timestamp, Extension, Directory) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement statement = myConn.prepareStatement(insertion)) {
            createTableIfNotExists();

            List<Event> events = Monitor.getMonitor().getEvents();
            while (!events.isEmpty()) {

                // Convert String of events into array to get individual elements
                String[] event = events.removeFirst().toString().split(", ");

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
     * Queries the database through specifics defined by the QueryView.
     *
     * @param theSpecifics user defined specifics to query from.
     */
    public void search(final String... theSpecifics) {
        // Reset the list
        myQuery.clear();
        try {
            // build statement based on theSpecifics
            // default statement is to select everything
            StringBuilder statement = new StringBuilder("SELECT * FROM events WHERE ");

            for (String s : theSpecifics) {
                if (!s.isEmpty()) {
                    statement.append(s);
                    statement.append(" AND ");
                }
            }

            if (statement.length() == 27) {
                // Remove WHERE clause
                statement.delete(statement.length() - 6, statement.length());
            } else {
                // Remove extra AND
                statement.delete(statement.length() - 5, statement.length());
            }

            //System.out.println(statement);

            // execute statement
            Statement execution = myConn.createStatement();
            ResultSet rs = execution.executeQuery(statement.toString());

            // add results to myQuery list
            while (rs.next()) {
                // Id, Filename, Event, Timestamp, Extension, Directory
                Event out = new Event(
                        rs.getString("Filename"),
                        rs.getString("Event"),
                        rs.getString("Timestamp"),
                        rs.getString("Extension"),
                        rs.getString("Directory"));

                myQuery.add(out);
            }

        } catch (SQLException e) {
            System.out.println("Error querying database: " + e);
        }
    }

    /**
     * Exports the queried results into a .csv file.
     *
     * @param thePath the path to the .csv file defined by the user.
     * @throws NullPointerException if the given path is null
     */
    public void export(final String thePath) {
        Objects.requireNonNull(thePath);

        File file = new File(thePath);
        try (FileWriter outputfile = new FileWriter(file);
             CSVWriter writer = new CSVWriter(outputfile)) {

            for (Event e : myQuery) {
                // Convert event into String[] and write to file
                writer.writeNext(e.toString().split(", "));
            }
        }
        catch (IOException e) {
            System.out.println("Error writing to file: " + e);
        }
    }

    /**
     * Returns the observable list to be bound in the QueryView
     *
     * @return the observable list myQuery
     */
    public ObservableList<Event> getQuery() {
        return myQuery;
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException if closing fails.
     */
    public void close() throws SQLException {
        if (myConn != null && !myConn.isClosed()) {
            myConn.close();
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


}
