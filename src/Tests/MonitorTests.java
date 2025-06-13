/*
 * TCSS 360 Course Project
 */

package Tests;

import Model.Event;
import Model.Monitor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Monitor class.
 *
 * @author Adin Smith
 * @version 6/13/2025
 */
class MonitorTests {

    /**
     * Monitor instance.
     */
    private static final Monitor MONITOR = Monitor.getMonitor();

    /**
     * Path to the testing directory to monitor.
     */
    private static final String TEST_PATH = "MonitorTesting";

    /**
     * List of events recorded from the monitor.
     */
    private static final List<Event> TEST_LIST = MONITOR.getEvents();

    /**
     * A wait time to give the thread time to catch events.
     */
    private static final int WAIT_TIME = 500;

    /**
     * Sets up the testing directory.
     */
    @BeforeAll
    static void setUp() {
        // Add the test directory to be monitored
        try {
            File dir = new File(TEST_PATH);
            // Make directory if it doesn't exist
            if (!dir.exists()) {
                dir.mkdirs();
            }
            MONITOR.addFile(dir.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the testing directory.
     */
    @AfterAll
    static void tearDown() {
        File dir = new File(TEST_PATH);
        dir.delete();
    }

    /**
     * Tests adding an invalid file.
     */
    @Test
    void testAddFileInvalid() {

        assertThrows(IOException.class, () -> MONITOR.addFile("invalid"),
                "Should throw an exception");

    }

    /**
     * Tests adding a null object to the monitor.
     */
    @Test
    void testAddFileNull() {

        assertThrows(NullPointerException.class, () -> MONITOR.addFile(null),
                "Should throw an exception");

    }

    /**
     * Tests monitoring without a specific extension.
     */
    @Test
    void testMonitoringNoExtension() {
        TEST_LIST.clear();

        MONITOR.startMonitoring();
        try {
            File test = new File(TEST_PATH + "\\\\testing1.txt");
            // Create file
            test.createNewFile();
            Thread.sleep(WAIT_TIME);

            // Modify file
            Files.write(test.toPath(), "initial".getBytes());
            Thread.sleep(WAIT_TIME);

            // Delete file
            test.delete();
            Thread.sleep(WAIT_TIME);

        } catch (Exception _) {}
        MONITOR.stopMonitoring();

        assertEquals(5, TEST_LIST.size(), "Size should be five.");


    }

    /**
     * Tests monitoring given a specific extension.
     */
    @Test
    void testMonitoringWithExtension() {
        TEST_LIST.clear();
        MONITOR.changeExtension(".txt");

        MONITOR.startMonitoring();
        try {
            File test = new File(TEST_PATH + "\\\\testing2.txt");
            File test2 = new File(TEST_PATH + "\\\\testing3.png");

            // Create files
            test.createNewFile();
            Thread.sleep(WAIT_TIME);
            test2.createNewFile();
            Thread.sleep(WAIT_TIME);

            // Modify files
            Files.write(test.toPath(), "initial".getBytes());
            Thread.sleep(WAIT_TIME);
            Files.write(test2.toPath(), "initial".getBytes());
            Thread.sleep(WAIT_TIME);

            // Delete files
            test.delete();
            Thread.sleep(WAIT_TIME);
            test2.delete();
            Thread.sleep(WAIT_TIME);

        } catch (Exception _) {}
        MONITOR.stopMonitoring();

        assertEquals(5, TEST_LIST.size(), "Size should be five.");


    }
}
