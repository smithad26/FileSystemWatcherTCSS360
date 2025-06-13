/*
 * TCSS 360 Course Project
 */

package Controller;

import Model.Monitor;
import Model.DataBase;
import View.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.sql.SQLException;

/**
 * Entry point for the File Watcher application.
 *
 * @author Marcus Nguyen
 */
public class App extends Application {

    /** File system monitor (singleton). */
    private static final Monitor MONITOR = Monitor.getMonitor();

    /** Database handler for file event persistence. */
    private static final DataBase DATABASE = DataBase.getDatabase();

    /**
     * Main method represents the main entry point into the program..
     *
     * @param theArgs command line arguments.
     */
    public static void main(final String[] theArgs) {


        launch(MainView.class);
    }

    /**
     * Initializes the application UI and database connection.
     *
     * @param thePrimaryStage the main window
     */
    @Override
    public void start(final Stage thePrimaryStage) {
        try {

            // Create a basic layout since we cannot rely on MainView.getRoot()
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 600);
            thePrimaryStage.setScene(scene);
            thePrimaryStage.setTitle("File Watcher");
            thePrimaryStage.setOnCloseRequest(this::handleCloseRequest);
            thePrimaryStage.show();
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            Platform.exit();
        }

    }

    /**
     * Handles the user closing the application window.
     *
     * @param theEvent the window close event
     */
    private void handleCloseRequest(final WindowEvent theEvent) {
        System.out.println("[App] Closing application: stopping monitor and closing database...");
        try {
            MONITOR.stopMonitoring();
            if (DATABASE != null) {
                DATABASE.close();
                System.out.println("[App] Database closed.");
            }
        } catch (SQLException e) {
            System.err.println("[App] Error during shutdown: " + e.getMessage());
        }
        Platform.exit();
    }

    /**
     * Ensures database connection is closed on app exit.
     */
    @Override
    public void stop() {
        System.out.println("[App] stop() called - performing final cleanup...");
        try {
            if (DATABASE != null) {
                DATABASE.close();
                System.out.println("[App] Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[App] Error closing database in stop(): " + e.getMessage());
        }
    }
}
