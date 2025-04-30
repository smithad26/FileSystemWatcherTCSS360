/*
 * TCSS 360 Course Project
 */
package controller;

import Model.Monitor;
import view.MainView;
import view.QueryView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.sql.SQLException;

/**
 * App class serves as the entry point for the File Watcher application.
 * It initializes the model and view, ties them together, and handles application lifecycle events.
 *
 * @author Marcus Nguyen
 * @version 4/29/2025
 */
public class App extends Application {

    // Model for monitoring file system events
    private Monitor monitor;
    // Main GUI view
    private MainView mainView;
    // Database manager for SQLite operations
    private DatabaseManager dbManager;

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Start the JavaFX application
        launch(args);
    }

    /**
     * Initializes and sets up the main window, model, and view.
     *
     * @param primaryStage the main application window
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Get the singleton Monitor instance
            monitor = Monitor.MONITOR;
            // Initialize the SQLite database manager
            dbManager = new DatabaseManager();
            // Create the main view, passing Monitor and DatabaseManager, and a callback to open QueryView
            mainView = new MainView(monitor, dbManager, this::openQueryView);

            // Set up the main scene with the view's root node (800x600 window)
            Scene scene = new Scene(mainView.getRoot(), 800, 600);
            // Attach the scene to the stage
            primaryStage.setScene(scene);
            // Set the window title
            primaryStage.setTitle("File Watcher");

            // Handle window close event to prompt saving and cleanup
            primaryStage.setOnCloseRequest(this::handleCloseRequest);
            // Show the main window
            primaryStage.show();
        } catch (SQLException e) {
            // Log database initialization errors and exit
            System.err.println("Error initializing database: " + e.getMessage());
            Platform.exit();
        } catch (Exception e) {
            // Log general startup errors and exit
            System.err.println("Error starting application: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Opens the query view window for database queries.
     */
    private void openQueryView() {
        // Create and show the query view with the database manager
        QueryView queryView = new QueryView(dbManager);
        queryView.show();
    }

    /**
     * Handles the window close event, prompting to save events and cleaning up resources.
     *
     * @param event the window close event
     */
    private void handleCloseRequest(WindowEvent event) {
        try {
            // Check if there are unsaved file events
            if (mainView.hasUnsavedEvents()) {
                // Prompt user to save events to the database
                boolean save = mainView.promptToSaveToDatabase();
                if (save) {
                    // Save current events to the database
                    dbManager.writeEvents(mainView.getCurrentEvents());
                }
            }
            // Stop file system monitoring
            monitor.stopMonitoring();
            // Close the database connection
            dbManager.close();
        } catch (SQLException e) {
            // Log errors during database save or close
            System.err.println("Error saving to database on exit: " + e.getMessage());
        }
        // Exit the JavaFX application
        Platform.exit();
    }

    /**
     * Cleans up resources when the application stops.
     */
    @Override
    public void stop() {
        try {
            // Ensure the database connection is closed
            if (dbManager != null) {
                dbManager.close();
            }
        } catch (SQLException e) {
            // Log errors during database cleanup
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
