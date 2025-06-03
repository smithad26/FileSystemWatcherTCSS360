package View;

import Model.DataBase;
import Model.Event;
import Model.Monitor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.event.EventHandler;

import java.io.File;
import java.nio.file.Path;

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
 *
 */

public class MainView extends Application {

    /** The main menu bar displayed at the top of the window. */
    private MenuBar menuBar;
    /** Menus for grouping related actions. */
    private Menu fileMenu, monitorMenu, databaseMenu, helpMenu;

    /** MenuItem under Help for showing application information. */
    private MenuItem myAbout;

    /** Dropdown for selecting which file extension to monitor. */
    private ComboBox<String> extensionDropdown;

    /** TextField for directory path input and display. */
    private TextField directoryField;
    /** Button for opening a directory chooser. */
    private Button browseButton;

    /** Control buttons for starting, stopping, querying, and writing events. */
    private Button startButton, stopButton, queryButton, myWriteButton;

    /** TableView to display file system events as they occur. */
    private TableView<Event> fileEventArea;

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
     * @param primaryStage the primary Stage for this JavaFX application
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File System Watcher");

        //Menu Bar
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        monitorMenu = new Menu("Monitor");
        databaseMenu = new Menu("Database");
        helpMenu = new Menu("Help");

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
        MenuItem newMonitorItem = new MenuItem("New Monitor ");
        newMonitorItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));

        fileMenu.getItems().addAll(newMonitorItem, new SeparatorMenuItem(), exitItem);

        //Monitor
        MenuItem startMonitorItem = new MenuItem("Start Monitoring");
        startMonitorItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        MenuItem stopMonitorItem = new MenuItem("Stop Monitoring");
        stopMonitorItem.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));

        monitorMenu.getItems().addAll(startMonitorItem, stopMonitorItem);

        //Database
        MenuItem queryEventsItem = new MenuItem("Query");
        queryEventsItem.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));

        MenuItem exportLogsItem = new MenuItem("Export");
        exportLogsItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));

        databaseMenu.getItems().addAll(queryEventsItem, exportLogsItem);

        //Help
        myAbout.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));

        helpMenu.getItems().add(myAbout);



        menuBar.getMenus().addAll(
                fileMenu,
                separator1,
                monitorMenu,
                separator2,
                databaseMenu,
                separator3,
                helpMenu
        );


        //Top Controls (Extension + Directory Input)

        // File extension dropdown
        Label extensionLabel = new Label("Monitor by extension");
        extensionDropdown = new ComboBox<>();
        extensionDropdown.setPromptText("Select extension");
        extensionDropdown.setPrefWidth(200);
        extensionDropdown.getItems().addAll(".txt", ".java", ".log", ".md");
        extensionDropdown.getSelectionModel().select(".txt"); // Set default

        VBox extensionBox = new VBox(5, extensionLabel, extensionDropdown);

        // Directory text field and browse button
        Label directoryLabel = new Label("Directory:");
        directoryField = new TextField();
        directoryField.setPrefWidth(300);
        browseButton = new Button("Browse...");

        HBox directoryBox = new HBox(10, directoryField, browseButton);
        VBox directorySection = new VBox(5, directoryLabel, directoryBox);

        // Combine top-left controls
        VBox leftControls = new VBox(30, extensionBox, directorySection);

        //Right-Side Buttons
        startButton = new Button("Start");
        startButton.setPadding(new Insets(5, 30, 5, 30));
        stopButton = new Button("Stop");
        stopButton.setPadding(new Insets(5, 30, 5, 30));
        queryButton = new Button("Query");
        queryButton.setPadding(new Insets(5, 30, 5, 30));

        myWriteButton = new Button("Write Contents");
        myWriteButton.setPadding(new Insets(5, 30, 5, 30));
        myWriteButton.setPrefWidth(150);


        startButton.setPrefWidth(150);
        stopButton.setPrefWidth(150);
        stopButton.setDisable(true); // App starts in a non-monitoring state
        queryButton.setPrefWidth(150);


        VBox buttonBox = new VBox(30, startButton, stopButton, queryButton, myWriteButton);
        buttonBox.setPadding(new Insets(40, 10, 0, 30));

        // add listeners to components (functionality)
        addListeners();

        browseButton.setOnAction(e -> {
            DirectoryChooser dir = new DirectoryChooser();
            File chosen = dir.showDialog(primaryStage);

            String path = chosen.getPath();
            try {
                MONITOR.addFile(path);
                directoryField.setText(path);
            } catch (Exception i) {
                myDirectoryAlert.showAndWait();
            }

        });

        //File Event Area
        Label fileEventLabel = new Label("File Event:");
        fileEventLabel.setPadding(new Insets(20, 0, 0, 0));
//        fileEventArea = new TextArea();
//        fileEventArea.setEditable(false);
//        fileEventArea.setPrefHeight(300);
//        fileEventArea.setMaxWidth(Double.MAX_VALUE);


        fileEventArea = TableBuilder.createResultTable();
        VBox.setVgrow(fileEventArea, Priority.ALWAYS);


        VBox centerContent = new VBox(20, leftControls, fileEventLabel, fileEventArea);
        centerContent.setPadding(new Insets(40, 0, 0, 0));


        //Layout Configuration
        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(centerContent);
        layout.setRight(buttonBox);
        layout.setPadding(new Insets(15));

        // Event Handling
        eventHandling();

        //Scene Setup
        Scene scene = new Scene(layout, 700, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
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
        startButton.setOnAction(event -> {
            String path = directoryField.getText();
            String extension = extensionDropdown.getValue();

            if (path == null || path.isEmpty()) {
                myDirectoryAlert.showAndWait();
                return;
            }

            try {
                MONITOR.addFile(path);
                MONITOR.startMonitoring();

                // Disable Start button and enable Stop
                startButton.setDisable(true);
                stopButton.setDisable(false);
            } catch (Exception e) {
                myDirectoryAlert.showAndWait();
            }
        });

        // needs to be fixed
        stopButton.setOnAction(event -> {
            MONITOR.stopMonitoring();

            // Enable Start button and disable Stop
            startButton.setDisable(false);
            stopButton.setDisable(true);

            // Add this alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Stopped");
            alert.setHeaderText(null);
            alert.setContentText("Monitoring has been stopped.");
            alert.showAndWait();

//            fileEventArea.clear();

        });

        //Added: Query pop up window opens when Query button is clicked
        queryButton.setOnAction(event -> {
            new QueryView().display();
        });

        // needs to be fixed
        directoryField.setOnAction(event -> {
            String path = directoryField.getText();
            try {
                MONITOR.addFile(path);
            } catch (Exception e) {
                myDirectoryAlert.showAndWait();
            }
        });

        browseButton.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Directory");
            File selectedDir = chooser.showDialog(null);
            if (selectedDir != null) {
                directoryField.setText(selectedDir.getAbsolutePath());
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
    }

    /**
     * Binds the Monitor's event StringProperty to the fileEventArea so UI updates reflect model events.
     */
    private void eventHandling() {

        // monitor fires property changes to fileEventArea
        //fileEventArea.textProperty().bind(MONITOR.getEvents());

        fileEventArea.setItems(MONITOR.getEvents());

    }
}

