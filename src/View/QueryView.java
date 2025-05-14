package View;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

public class QueryView {

    // Input fields on the left
    // These are all the form fields users will fill out before running a query
    private TextField extensionField;
    private ComboBox<String> eventTypeDropdown;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField directoryField;
    private Button browseButton;

    //Buttons on the right (Search, Export, Email)
    private Button searchButton;
    private Button exportButton;
    private Button emailButton;

    // Table that displays query results
    private TableView<QueryResult> resultTable;

    /**
     * This method shows the popup window for querying file event data.
     * It's triggered when the Query button in the main view is clicked.
     */
    public void display() {
        Stage queryStage = new Stage();
        queryStage.setTitle("Query Form");
        queryStage.setResizable(false); // user shouldn't resize this window

        //Row 1: Extension and Event Type
        extensionField = new TextField();
        eventTypeDropdown = new ComboBox<>();
        eventTypeDropdown.setPromptText("Select event type");

        // Wrap both inputs with their labels into a row
        HBox extensionRow = new HBox(20,
                createLabeledField("Extension:", extensionField),
                createLabeledField("Event Type:", eventTypeDropdown)
        );

        //Row 2: Start and End Date Pickers
        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();

        HBox dateRow = new HBox(20,
                createLabeledField("Start Date", startDatePicker),
                createLabeledField("End Date", endDatePicker)
        );

        //Row 3: Directory Field + Browse Button
        directoryField = new TextField();
        browseButton = new Button("Browse..."); // will later open a directory picker
        HBox directoryRow = new HBox(10, directoryField, browseButton);
        VBox directorySection = new VBox(5, new Label("Directory:"), directoryRow);
        directoryField.setPrefWidth(500); // makes the field wide enough to see full paths

        //Buttons on the right side (Search / Export / Email)
        searchButton = new Button("Search");
        exportButton = new Button("Export");
        emailButton = new Button("Email");

        // Set consistent width for all buttons so they line up
        double buttonWidth = 250;
        searchButton.setPrefWidth(buttonWidth);
        exportButton.setPrefWidth(buttonWidth);
        emailButton.setPrefWidth(buttonWidth);

        // Stack buttons vertically with some space and padding from the top
        VBox buttonBox = new VBox(20, searchButton, exportButton, emailButton);
        buttonBox.setPadding(new Insets(20, 0, 0, 10));

        //Query Results Table
        Label resultsLabel = new Label("Query Results:");
        resultTable = createResultTable(); // defined below

        //Organize everything on the left: input fields + results table
        VBox leftForm = new VBox(20, extensionRow, dateRow, directorySection, resultsLabel, resultTable);
        leftForm.setPadding(new Insets(20));
        leftForm.setPrefWidth(800);

        //Main layout: left form + right-side buttons
        HBox mainLayout = new HBox(30, leftForm, buttonBox);
        mainLayout.setPadding(new Insets(20));

        //Set up the scene and show it
        Scene scene = new Scene(mainLayout, 850, 600); // size of the popup window
        queryStage.setScene(scene);
        queryStage.show();
    }

    /**
     * This helper builds a labeled vertical layout: label on top, field underneath.
     * Makes it easy to reuse across form rows.
     */
    private VBox createLabeledField(String labelText, Control field) {
        Label label = new Label(labelText);
        VBox box = new VBox(5, label, field); // 5px spacing between label and field
        return box;
    }

    /**
     * Builds and configures the results table.
     * This table shows the output of the user's search query.
     */
    private TableView<QueryResult> createResultTable() {
        TableView<QueryResult> table = new TableView<>();
        table.setEditable(false); // users can't edit any cells
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // fills the available width
        table.setMaxWidth(Double.MAX_VALUE); // lets it stretch fully

        //Define all 5 columns
        TableColumn<QueryResult, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setResizable(false);

        TableColumn<QueryResult, String> nameCol = new TableColumn<>("Filename");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        nameCol.setPrefWidth(150);
        nameCol.setResizable(false);

        TableColumn<QueryResult, String> typeCol = new TableColumn<>("Event Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        typeCol.setPrefWidth(80);
        typeCol.setResizable(false);

        TableColumn<QueryResult, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeCol.setPrefWidth(100);
        timeCol.setResizable(false);

        TableColumn<QueryResult, String> dirCol = new TableColumn<>("Directory");
        dirCol.setCellValueFactory(new PropertyValueFactory<>("directory"));
        dirCol.setPrefWidth(240);
        dirCol.setResizable(false);

        // Add all columns to the table
        table.getColumns().addAll(idCol, nameCol, typeCol, timeCol, dirCol);

        // don't let users drag column headers around
        table.getColumns().forEach(col -> col.setReorderable(false));


        table.setPrefHeight(300);
        return table;
    }

    /**
     * This class represents each row in the query result table
     */
    public static class QueryResult {
        private final String myId;
        private final String myFilename;
        private final String myEventType;
        private final String myTimestamp;
        private final String myDirectory;

        public QueryResult(String theId, String theFilename, String theEventType, String theTimestamp, String theDirectory) {
            myId = theId;
            myFilename = theFilename;
            myEventType = theEventType;
            myTimestamp = theTimestamp;
            myDirectory = theDirectory;
        }

        public String getId() { return myId; }
        public String getFilename() { return myFilename; }
        public String getEventType() { return myEventType; }
        public String getTimestamp() { return myTimestamp; }
        public String getDirectory() { return myDirectory; }
    }
}

