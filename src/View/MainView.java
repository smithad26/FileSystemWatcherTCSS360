/*
 * TCSS 360 Course Project
 */

package View;

import Model.DataBase;
import Model.Event;
import Model.Monitor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * MainView is the JavaFX-based graphical user interface for the File System Watcher application.
 * It allows users to:
 *
 *   Select a directory to monitor
 *   Choose file extensions for filtering events
 *   Start and stop the file monitoring process
 *   Write monitoring events to a database
 *   Query recorded events
 *   View real-time file system events in a text area
 *   Access an About dialog with usage information
 *
 * <p>This class binds to the {@link Monitor} and {@link DataBase} models
 * and serves as the View component in an MVC architecture.</p>
 *
 * @author Mohamed Mohamed
 * @version 6/12/2025
 */

public class MainView extends Application {

    /** The main menu bar displayed at the top of the window. */
    private MenuBar myMenuBar;
    /** Menus for grouping related actions. */
    private Menu myFileMenu, myMonitorMenu, myDataBaseMenu, myHelpMenu;

    /** MenuItem under Help for showing application information. */
    private MenuItem myAbout;
    // Menu items (moved to class level so addListeners() can access them)

    /**
     * MenuItem represents new monitor option.
     */
    private MenuItem myNewMonitorItem;

    /**
     * MenuItem represents exit option.
     */
    private MenuItem myExitItem;

    /**
     * MenuItem represents start monitoring option.
     */
    private MenuItem myStartMonitoringItem;

    /**
     * MenuItem represents stop monitoring option.
     */
    private MenuItem myStopMonitoringItem;

    /**
     * MenuItem represents query events option.
     */
    private MenuItem myQueryEventsItem;

    /**
     * MenuItem represents export logs option.
     */
    private MenuItem myExportLogsItem;


    /** Dropdown for selecting which file extension to monitor. */
    private ComboBox<String> myExtensionDropdown;

    /** TextField for directory path input and display. */
    private TextField myDirectoryField;
    /** Button for opening a directory chooser. */
    private Button myBrowseButton;

    /** Control buttons for starting, stopping, querying, and writing events. */
    private Button myStartButton, myStopButton, myQueryButton, myWriteButton;

    /** TableView to display file system events as they occur. */
    private TableView<Event> myFileEventArea;

    /** Singleton instance of the Monitor model. */
    private static final Monitor MONITOR = Monitor.getMonitor();
    /** Singleton instance of the DataBase model. */
    private static final DataBase DATABASE = DataBase.getDatabase();

    /** Alert dialog shown when the user inputs an invalid directory. */
    private Alert myDirectoryAlert;

    /**
     * Initializes and displays the main application window.
     * Sets up menus, controls, layout, and binds model events to the view.
     *
     * @param thePrimaryStage the primary Stage for this JavaFX application
     */
    @Override
    public void start(final Stage thePrimaryStage) {
        thePrimaryStage.setTitle("File System Watcher");

        //Menu Bar
        myMenuBar = new MenuBar();
        myFileMenu = new Menu("File");
        myMonitorMenu = new Menu("Monitor");
        myDataBaseMenu = new Menu("Database");
        myHelpMenu = new Menu("Help");

        // MenuItems
        myAbout = new MenuItem("About");

        // Alerts
        myDirectoryAlert = new Alert(Alert.AlertType.ERROR);
        myDirectoryAlert.setTitle("Error");
        myDirectoryAlert.setHeaderText(null);
        myDirectoryAlert.setContentText("Invalid directory");

        // Disabled the separator lines
        Menu separator1 = new Menu("|");
        separator1.setDisable(true);
        Menu separator2 = new Menu("|");
        separator2.setDisable(true);
        Menu separator3 = new Menu("|");
        separator3.setDisable(true);

        //Add dropdown options to the menu bar items
        //File
        myNewMonitorItem = new MenuItem("New Monitor ");
        myNewMonitorItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));

        myExitItem = new MenuItem("Exit");
        myExitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));

        myFileMenu.getItems().addAll(myNewMonitorItem, new SeparatorMenuItem(), myExitItem);

        //Monitor
        myStartMonitoringItem = new MenuItem("Start Monitoring");
        myStartMonitoringItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        myStopMonitoringItem = new MenuItem("Stop Monitoring");
        myStopMonitoringItem.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));

        myMonitorMenu.getItems().addAll(myStartMonitoringItem, myStopMonitoringItem);

        //Database
        myQueryEventsItem = new MenuItem("Query");
        myQueryEventsItem.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));

        myExportLogsItem = new MenuItem("Export");
        myExportLogsItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));

        myDataBaseMenu.getItems().addAll(myQueryEventsItem, myExportLogsItem);

        //Help
        myAbout.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));

        myHelpMenu.getItems().add(myAbout);



        myMenuBar.getMenus().addAll(
                myFileMenu,
                separator1,
                myMonitorMenu,
                separator2,
                myDataBaseMenu,
                separator3,
                myHelpMenu
        );


        //Top Controls (Extension + Directory Input)

        // File extension dropdown
        Label extensionLabel = new Label("Monitor by extension");
        myExtensionDropdown = new ComboBox<>();
        myExtensionDropdown.setPromptText("Select extension");
        myExtensionDropdown.setPrefWidth(200);
        myExtensionDropdown.getItems().addAll("None",".txt", ".java", ".log", ".md");
        myExtensionDropdown.getSelectionModel().select("None"); // Set default

        VBox extensionBox = new VBox(5, extensionLabel, myExtensionDropdown);

        // Directory text field and browse button
        Label directoryLabel = new Label("Directory:");
        myDirectoryField = new TextField();
        myDirectoryField.setPrefWidth(300);
        myBrowseButton = new Button("Browse...");

        HBox directoryBox = new HBox(10, myDirectoryField, myBrowseButton);
        VBox directorySection = new VBox(5, directoryLabel, directoryBox);

        // Combine top-left controls
        VBox leftControls = new VBox(30, extensionBox, directorySection);

        //Right-Side Buttons
        myStartButton = new Button("Start");
        myStartButton.setPadding(new Insets(5, 30, 5, 30));
        myStopButton = new Button("Stop");
        myStopButton.setPadding(new Insets(5, 30, 5, 30));
        myQueryButton = new Button("Query");
        myQueryButton.setPadding(new Insets(5, 30, 5, 30));

        myWriteButton = new Button("Write Contents");
        myWriteButton.setPadding(new Insets(5, 30, 5, 30));
        myWriteButton.setPrefWidth(150);


        myStartButton.setPrefWidth(150);
        myStopButton.setPrefWidth(150);
        myStopButton.setDisable(true); // App starts in a non-monitoring state
        myQueryButton.setPrefWidth(150);


        VBox buttonBox = new VBox(30, myStartButton, myStopButton, myQueryButton, myWriteButton);
        buttonBox.setPadding(new Insets(40, 10, 0, 30));

        // add listeners to components (functionality)
        addListeners();

        myBrowseButton.setOnAction(e -> {
            DirectoryChooser dir = new DirectoryChooser();
            File chosen = dir.showDialog(thePrimaryStage);

            String path = chosen.getPath();
            try {
                MONITOR.addFile(path);
                myDirectoryField.setText(path);
            } catch (Exception i) {
                myDirectoryAlert.showAndWait();
            }

        });

        //File Event Area
        Label fileEventLabel = new Label("File Event:");
        fileEventLabel.setPadding(new Insets(20, 0, 0, 0));

        myFileEventArea = TableBuilder.createResultTable();
        VBox.setVgrow(myFileEventArea, Priority.ALWAYS);


        VBox centerContent = new VBox(20, leftControls, fileEventLabel, myFileEventArea);
        centerContent.setPadding(new Insets(40, 0, 0, 0));


        //Layout Configuration
        BorderPane layout = new BorderPane();
        layout.setTop(myMenuBar);
        layout.setCenter(centerContent);
        layout.setRight(buttonBox);
        layout.setPadding(new Insets(15));

        // Event Handling
        eventHandling();

        //Scene Setup
        Scene scene = new Scene(layout, 700, 800);
        thePrimaryStage.setScene(scene);
        thePrimaryStage.show();
    }

    /**
     * Registers event handlers for the Start, Stop, Query, Write, Browse, and About actions.
     * Handles user input validation and delegates to Monitor and DataBase.
     */
    private void addListeners() {

        myWriteButton.setOnAction(e -> {
            DATABASE.writeEvents();
        });

        // needs to be fixed
        myStartButton.setOnAction(event -> {
            String path = myDirectoryField.getText();
            String extension = myExtensionDropdown.getValue();
            if (!extension.equalsIgnoreCase("None")) {
                MONITOR.changeExtension(extension);
            }

            if (path == null || path.isEmpty()) {
                myDirectoryAlert.showAndWait();
                return;
            }

            try {
                MONITOR.addFile(path);
                MONITOR.startMonitoring();

                // Disable Start button and enable Stop
                myStartButton.setDisable(true);
                myStopButton.setDisable(false);
            } catch (Exception e) {
                myDirectoryAlert.showAndWait();
            }
        });

        // needs to be fixed
        myStopButton.setOnAction(event -> {
            MONITOR.stopMonitoring();

            // Enable Start button and disable Stop
            myStartButton.setDisable(false);
            myStopButton.setDisable(true);

            // Add this alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Stopped");
            alert.setHeaderText(null);
            alert.setContentText("Monitoring has been stopped.");
            alert.showAndWait();

//            fileEventArea.clear();

        });

        //Added: Query pop up window opens when Query button is clicked
        myQueryButton.setOnAction(event -> {
            new QueryView().display();
        });

        // needs to be fixed
        myDirectoryField.setOnAction(event -> {
            String path = myDirectoryField.getText();
            try {
                MONITOR.addFile(path);
            } catch (Exception e) {
                myDirectoryAlert.showAndWait();
            }
        });

        myBrowseButton.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Directory");
            File selectedDir = chooser.showDialog(null);
            if (selectedDir != null) {
                myDirectoryField.setText(selectedDir.getAbsolutePath());
            }
        });

        myAbout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText(null);
            alert.setContentText("""
                    SystemFileWatcher Information and Usage:
                    Developers: Adin Smith, Marcus Nguyen, Mohamed Mohamed
                    Date: 6/13/2025
                    Version: 1.0.0
                   \s
                    HOW TO USE
                    Provide a directory from your computer to start monitoring all file activity from. The
                    user can choose what specific file extensions they want to be monitored from the directory.
                    Once the program starts monitoring, the program will display any events that occur in the\s
                    box below. The user has the option to write specific file events to a database, and also
                    has the option to be notified of events via email.\s
                   \s""");
            alert.showAndWait();
        });
        

        // File > New Monitor: Reset form and stop monitoring
        myNewMonitorItem.setOnAction(e -> {
            // Stop monitoring if active
            MONITOR.stopMonitoring();

            // Reset UI fields
            myExtensionDropdown.getSelectionModel().clearSelection();
            myDirectoryField.clear();

            // Reset buttons
            myStartButton.setDisable(false);
            myStopButton.setDisable(true);

            // Show confirmation alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Monitor Reset");
            alert.setHeaderText(null);
            alert.setContentText("Monitor has been reset. Please select a new directory.");
            alert.showAndWait();
        });

        // File > Exit: Close the application
        myExitItem.setOnAction(e -> {
            Platform.exit();
        });

        // Monitor > Start Monitoring: Simulate Start button click
        myStartMonitoringItem.setOnAction(e -> {
            myStartButton.fire();
        });

        // Monitor > Stop Monitoring: Simulate Stop button click
        myStopMonitoringItem.setOnAction(e -> {
            myStopButton.fire();
        });

        // Database > Query: Simulate Query button click
        myQueryEventsItem.setOnAction(e -> {
            myQueryButton.fire();
        });

        // Database > Export: Simulate Write button click
        myExportLogsItem.setOnAction(e -> {
            myWriteButton.fire();
        });

    }

    /**
     * Binds the Monitor's event StringProperty to the fileEventArea so UI updates reflect model events.
     */
    private void eventHandling() {

        // monitor fires property changes to fileEventArea

        myFileEventArea.setItems(MONITOR.getEvents());

    }
}

