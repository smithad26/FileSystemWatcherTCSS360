package View;

import Model.Event;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import Model.DataBase;

import java.io.File;

public class QueryView {

    /**
     * DataBase constant to access the DataBase
     */
    private static final DataBase DATABASE = DataBase.getDatabase();

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
    private Button clearButton;

    // Table that displays query results
    private TableView<Event> resultTable;

    /**
     * String fields representing partial SQL statements to be queried later
     */
    private String myExtensionSQL, myDirectorySQL, myStartSQL, myEndSQL, myEventSQL;

    /**
     * This method shows the popup window for querying file event data.
     * It's triggered when the Query button in the main view is clicked.
     */
    public void display() {
        // Initialize partial SQL statements as empty
        myExtensionSQL = "";
        myDirectorySQL = "";
        myStartSQL = "";
        myEndSQL = "";
        myEventSQL = "";

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
        clearButton = new Button("Clear");

        // Set consistent width for all buttons so they line up
        double buttonWidth = 250;
        searchButton.setPrefWidth(buttonWidth);
        exportButton.setPrefWidth(buttonWidth);
        emailButton.setPrefWidth(buttonWidth);
        clearButton.setPrefWidth(buttonWidth);

        // Stack buttons vertically with some space and padding from the top
        VBox buttonBox = new VBox(20, searchButton, exportButton, emailButton, clearButton);
        buttonBox.setPadding(new Insets(20, 0, 0, 10));

        //Query Results Table
        Label resultsLabel = new Label("Query Results:");
        resultTable = TableBuilder.createResultTable(); // defined below

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

        // Add listeners
        addListeners();

        // Export button needs access to queryStage
        exportButton.setOnAction(e -> {
            // Allow user to choose where to save csv file
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fc.getExtensionFilters().add(filter);
            File file = fc.showSaveDialog(queryStage);

            // Get path from user and export
            try {
                DATABASE.export(file.getPath());
            } catch (NullPointerException _) {}     // If user cancels the dialog box
        });

        // Browse button needs access to queryStage
        browseButton.setOnAction(e -> {
            DirectoryChooser dir = new DirectoryChooser();
            File chosen = dir.showDialog(queryStage);

            try {
                String path = chosen.getPath();
                myDirectorySQL = "Directory LIKE '" + path + "'";
                directoryField.setText(path);
            } catch (Exception _) {}    // If user cancels the dialog box
        });

        // Clears all input fields and resets the result table
        clearButton.setOnAction(e -> {
            // Clear all form fields
            extensionField.clear();
            eventTypeDropdown.getSelectionModel().clearSelection();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            directoryField.clear();

            // Clear the results from the TableView
            DATABASE.getQuery().clear();
        });

    }

    /**
     * Adds listeners to buttons/elements
     */
    private void addListeners() {
        searchButton.setOnAction(e -> {
            // Extension filter
            String ext = extensionField.getText().trim();
            myExtensionSQL = ext.isEmpty() ? "" : "Extension = '" + ext + "'";

            // Event type filter
            String type = eventTypeDropdown.getValue();
            myEventSQL = (type == null || type.isEmpty()) ? "" : "Event = '" + type + "'";

            // Start date filter
            if (startDatePicker.getValue() != null) {
                myStartSQL = "Timestamp >= '" + startDatePicker.getValue() + "T00:00:00'";
            } else {
                myStartSQL = "";
            }

            // End date filter
            if (endDatePicker.getValue() != null) {
                myEndSQL = "Timestamp <= '" + endDatePicker.getValue() + "T23:59:59'";
            } else {
                myEndSQL = "";
            }

            // Directory filter (from user input)
            String path = directoryField.getText().trim();
            myDirectorySQL = path.isEmpty() ? "" : "Directory LIKE '%" + path + "%'";

            // Run filtered search
            DATABASE.search(myDirectorySQL, myStartSQL, myEndSQL, myEventSQL, myExtensionSQL);
        });

        // bind textfield
        resultTable.setItems(DATABASE.getQuery());

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

}

