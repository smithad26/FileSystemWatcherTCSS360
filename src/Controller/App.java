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
 */
public class App extends Application {

    /** File system monitor (singleton). */
    private static final Monitor MONITOR = Monitor.getMonitor();

    /** Database handler for file event persistence. */
    private static final DataBase DATABASE = DataBase.getDatabase();

    public static void main(String[] args) {


        launch(MainView.class);
    }

    /**
     * Initializes the application UI and database connection.
     *
     * @param primaryStage the main window
     */
    @Override
    public void start(Stage primaryStage) {
        try {

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
