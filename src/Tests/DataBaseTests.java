package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseTest {

    public static void main(String[] args) {
        try {
            // Create a new database
            DataBase db = new DataBase();
            System.out.println("Database created");

            // Add some events to test
            List<FileEvent> events = new ArrayList<>();
            events.add(new FileEvent("/file1.txt", "CREATED", "2025-05-20 20:00"));
            events.add(new FileEvent("/file2.txt", "MODIFIED", "2025-05-20 20:01"));
            db.writeEvents(events);
            System.out.println("Events added");

            // Check the events
            ResultSet results = db.queryAllEvents();
            while (results.next()) {
                System.out.println("Found: " + results.getString("file_path") + 
                        ", " + results.getString("event_type") + 
                        ", " + results.getString("timestamp"));
            }

            // Close the database
            db.close();
            System.out.println("Database closed");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
