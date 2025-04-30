package View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        menuBar.getMenus().addAll(
                fileMenu,
                new Menu("|"),
                monitorMenu,
                new Menu("|"),
                databaseMenu,
                new Menu("|"),
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

