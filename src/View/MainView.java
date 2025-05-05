package View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainView extends Application {

    // Menu bar and its menus
    private MenuBar menuBar;
    private Menu fileMenu, monitorMenu, databaseMenu, helpMenu;

    // Dropdown for selecting file extension to monitor
    private ComboBox<String> extensionDropdown;

    // Directory path field and browse button
    private TextField directoryField;
    private Button browseButton;

    // Control buttons (start, stop, query)
    private Button startButton, stopButton, queryButton;

    // Display area for file events
    private TextArea fileEventArea;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File System Watcher");

        //Menu Bar
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        monitorMenu = new Menu("Monitor");
        databaseMenu = new Menu("Database");
        helpMenu = new Menu("Help");

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
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));

        helpMenu.getItems().add(aboutItem);



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

        //Added: Query pop up window opens when Query button is clicked
        queryButton.setOnAction(e -> {
            new QueryView().display();
        });

        startButton.setPrefWidth(150);
        stopButton.setPrefWidth(150);
        queryButton.setPrefWidth(150);


        VBox buttonBox = new VBox(30, startButton, stopButton, queryButton);
        buttonBox.setPadding(new Insets(40, 10, 0, 30));



        //File Event Area
        Label fileEventLabel = new Label("File Event:");
        fileEventLabel.setPadding(new Insets(20, 0, 0, 0));
        fileEventArea = new TextArea();
        fileEventArea.setEditable(false);
        fileEventArea.setPrefHeight(300);
        fileEventArea.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(fileEventArea, Priority.ALWAYS);


        VBox centerContent = new VBox(20, leftControls, fileEventLabel, fileEventArea);
        centerContent.setPadding(new Insets(40, 0, 0, 0));


        //Layout Configuration
        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(centerContent);
        layout.setRight(buttonBox);
        layout.setPadding(new Insets(15));

        //Scene Setup
        Scene scene = new Scene(layout, 700, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}

