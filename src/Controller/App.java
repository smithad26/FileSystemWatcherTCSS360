package Controller;

import Model.Monitor;
import Model.DataBase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.sql.SQLException;

/**
 * Entry point for the File Watcher application.
 */
public class App extends Application {

    /** File system monitor (singleton). */
    private Monitor monitor;

    /** Database handler for file event persistence. */
    private DataBase database;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the application UI and database connection.
     *
     * @param primaryStage the main window
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            monitor = Monitor.MONITOR;
            database = new DataBase();  // Using your custom DataBase class

            // Create a basic layout since we cannot rely on MainView.getRoot()
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("File Watcher");
            primaryStage.setOnCloseRequest(this::handleCloseRequest);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Handles the user closing the application window.
     *
     * @param event the window close event
     */
    private void handleCloseRequest(WindowEvent event) {
        try {
            monitor.stopMonitoring();
            if (database != null) {
                database.close();
            }
        } catch (SQLException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
        Platform.exit();
    }

    /**
     * Ensures database connection is closed on app exit.
     */
    @Override
    public void stop() {
        try {
            if (database != null) {
                database.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
