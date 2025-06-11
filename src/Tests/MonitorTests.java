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

class MonitorTests {
    private static final Monitor MONITOR = Monitor.getMonitor();
    private static final String TEST_PATH = "MonitorTesting";
    private static final List<Event> TEST_LIST = MONITOR.getEvents();
    private static final int WAIT_TIME = 500;

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

    @AfterAll
    static void tearDown() {
        File dir = new File(TEST_PATH);
        dir.delete();
    }

    @Test
    void testAddFileInvalid() {

        assertThrows(IOException.class, () -> MONITOR.addFile("invalid"), "Should throw an exception");

    }

    @Test
    void testAddFileNull() {

        assertThrows(NullPointerException.class, () -> MONITOR.addFile(null), "Should throw an exception");

    }

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
