/*
 * TCSS 360 Course Project
 */

package Tests;

import Model.DataBase;
import Model.Event;
import Model.Monitor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataBase class.
 *
 * @author Adin Smith
 * @version 6/13/2025
 */
class DataBaseTests {

    /**
     * DataBase instance to test.
     */
    private static final DataBase DATABASE = DataBase.getDatabase();

    /**
     * Monitor instance for testing.
     */
    private static final Monitor MONITOR = Monitor.getMonitor();

    /**
     * Adds an event to the DataBase to test.
     */
    @BeforeAll
    static void setUp() {
        Event test = new Event("testing.txt",
                "EVENT_CREATED",
                "2025-6-12-something idk",
                ".txt",
                "directory");

        MONITOR.getEvents().add(test);
        DATABASE.writeEvents();
    }

    /**
     * Removes the added file from the DataBase at the end.
     */
    @AfterAll
    static void tearDown() {
        DATABASE.getQuery().clear();
        try {
            DATABASE.close();
        } catch (SQLException e) {
            System.out.println("Unable to close database: " + e);
        }
    }

    /**
     * Tests writing events.
     */
    @Test
    void testWriteEvents() {
        String test = "testing.txt, EVENT_CREATED, 2025-6-12-something idk, .txt, directory";
        DATABASE.search();
        Event test2 = DATABASE.getQuery().getLast();
        assertEquals(test, test2.toString());
    }

    /**
     * Tests searching events.
     */
    @Test
    void testSearch() {
        DATABASE.search();
        assertFalse(DATABASE.getQuery().isEmpty(), "Query results should be greater than 0.");
    }

    /**
     * Tests exporting events.
     */
    @Test
    void testExport() {
        try {
            File tempFile = File.createTempFile("export_test", ".csv");
            tempFile.deleteOnExit();

            DATABASE.export(tempFile.getAbsolutePath());

            assertTrue(tempFile.exists());
            assertTrue(tempFile.length() > 0);
        } catch (IOException e) {
            System.out.println("Error caught in testExport: " + e);
        }
    }

}
