/*
 * TCSS 360 Course Project
 */

package Tests;

import Model.Event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test claws performs unit tests on the Event class
 *
 * @author Adin Smith
 * @version 6/13/2025
 */

class EventTests {

    /**
     * Tests the constructor in Event.
     */
    @Test
    void testConstructor() {
        assertThrows(NullPointerException.class, () -> new Event(null,null,null,null,null),
                "Should throw an exception");
    }

    /**
     * Tests the toString() method in Event.
     */
    @Test
    void testToString() {
        Event test = new Event("test.txt", "Created", "2025-13-6", ".txt", "C:");
        String test2 = "test.txt, Created, 2025-13-6, .txt, C:";
        assertEquals(test2, test.toString(), "Should be : test.txt, Created, 2025-13-6, .txt, C:");
    }

    /**
     * Tests the getExtension() method in Event.
     */
    @Test
    void testGetExtension() {
        Event test = new Event("test.txt", "Created", "2025-13-6", ".txt", "C:");
        assertEquals(".txt", test.getExtension(), "Should be : .txt");
    }

    /**
     * Tests the getDirectory() method in Event.
     */
    @Test
    void testGetDirectory() {
        Event test = new Event("test.txt", "Created", "2025-13-6", ".txt", "C:");
        assertEquals("C:", test.getDirectory(), "Should be : C:");
    }

    /**
     * Tests the getName() method in Event.
     */
    @Test
    void testGetName() {
        Event test = new Event("test.txt", "Created", "2025-13-6", ".txt", "C:");
        assertEquals("test.txt", test.getFilename(), "Should be : test.txt");
    }

    /**
     * Tests the getEvent() method in Event.
     */
    @Test
    void testGetEvent() {
        Event test = new Event("test.txt", "Created", "2025-13-6", ".txt", "C:");
        assertEquals("Created", test.getEventType(), "Should be : Created");
    }

    /**
     * Tests the getTimestamp() method in Event.
     */
    @Test
    void testGetTimestamp() {
        Event test = new Event("test.txt", "Created", "2025-13-6", ".txt", "C:");
        assertEquals("2025-13-6", test.getTimestamp(), "Should be : 2025-13-6");
    }
}
