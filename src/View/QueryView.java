/*
 * TCSS 360 Course Project
 */

package View;

import Model.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import Model.DataBase;

import java.io.File;

/**
 * QueryView class used to create the query window in the program.
 *
 * @author Adin Smith
 * @author Mohamed Mohamed
 * @version 6/12/2025
 */
public class QueryView {

    /**
     * DataBase constant to access the DataBase
     */
    private static final DataBase DATABASE = DataBase.getDatabase();

    // Input fields on the left
    // These are all the form fields users will fill out before running a query

    /**
     * TextField to allow user to include extension to query.
     */
    private TextField myExtensionField;

    /**
     * ComboBox allows user to choose an event type to query.
     */
    private ComboBox<String> myEventTypeDropdown;

    /**
     * DatePicker allows user to choose a starting date to query.
     */
    private DatePicker myStartDatePicker;

    /**
     * DatePicker allows user to choose an ending date to query.
     */
    private DatePicker myEndDatePicker;

    /**
     * TextField allows user to choose a directory to query.
     */
    private TextField myDirectoryField;

    /**
     * Button allows user to browse a directory to query.
     */
    private Button myBrowseButton;

    //Buttons on the right (Search, Export, Email)

    /**
     * Button allows user to query the results.
     */
    private Button mySearchButton;

    /**
     * Button allows user to export the query to a .csv file.
     */
    private Button myExportButton;

    /**
     * Button allows user to email the query results.
     */
    private Button myEmailButton;

    /**
     * Button allows user to clear the query.
     */
    private Button myClearButton;

    /**
     * TableView that displays the query results.
     */
    private TableView<Event> myResultTable;

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
        myExtensionField = new TextField();
        myEventTypeDropdown = new ComboBox<>();
        myEventTypeDropdown.getItems().addAll("ENTRY_CREATE", "ENTRY_MODIFY", "ENTRY_DELETE", "None");
        myEventTypeDropdown.getSelectionModel().select("None"); // Set default

        

        // Wrap both inputs with their labels into a row
        HBox extensionRow = new HBox(20,
                createLabeledField("Extension:", myExtensionField),
                createLabeledField("Event Type:", myEventTypeDropdown)
        );

        //Row 2: Start and End Date Pickers
        myStartDatePicker = new DatePicker();
        myEndDatePicker = new DatePicker();

        HBox dateRow = new HBox(20,
                createLabeledField("Start Date", myStartDatePicker),
                createLabeledField("End Date", myEndDatePicker)
        );

        //Row 3: Directory Field + Browse Button
        myDirectoryField = new TextField();
        myBrowseButton = new Button("Browse..."); // will later open a directory picker
        HBox directoryRow = new HBox(10, myDirectoryField, myBrowseButton);
        VBox directorySection = new VBox(5, new Label("Directory:"), directoryRow);
        myDirectoryField.setPrefWidth(500); // makes the field wide enough to see full paths

        //Buttons on the right side (Search / Export / Email)
        mySearchButton = new Button("Search");
        myExportButton = new Button("Export");
        myEmailButton = new Button("Email");
        myClearButton = new Button("Clear");

        // Set consistent width for all buttons so they line up
        double buttonWidth = 250;
        mySearchButton.setPrefWidth(buttonWidth);
        myExportButton.setPrefWidth(buttonWidth);
        myEmailButton.setPrefWidth(buttonWidth);
        myClearButton.setPrefWidth(buttonWidth);

        // Stack buttons vertically with some space and padding from the top
        VBox buttonBox = new VBox(20, mySearchButton, myExportButton, myEmailButton, myClearButton);
        buttonBox.setPadding(new Insets(20, 0, 0, 10));

        //Query Results Table
        Label resultsLabel = new Label("Query Results:");
        myResultTable = TableBuilder.createResultTable(); // defined below

        //Organize everything on the left: input fields + results table
        VBox leftForm = new VBox(20, extensionRow, dateRow, directorySection, resultsLabel, myResultTable);
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
        myExportButton.setOnAction(e -> {
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
        myBrowseButton.setOnAction(e -> {
            DirectoryChooser dir = new DirectoryChooser();
            File chosen = dir.showDialog(queryStage);

            try {
                String path = chosen.getPath();
                myDirectorySQL = "Directory LIKE '" + path + "'";
                myDirectoryField.setText(path);
            } catch (Exception _) {}    // If user cancels the dialog box
        });

        // Clears all input fields and resets the result table
        myClearButton.setOnAction(e -> {
            // Clear all form fields
            myExtensionField.clear();
            myEventTypeDropdown.getSelectionModel().clearSelection();
            myStartDatePicker.setValue(null);
            myEndDatePicker.setValue(null);
            myDirectoryField.clear();

            // Clear the results from the TableView
            DATABASE.getQuery().clear();
        });

    }

    /**
     * Adds listeners to buttons/elements
     */
    private void addListeners() {
        mySearchButton.setOnAction(e -> {
            // Extension filter
            String ext = myExtensionField.getText().trim();
            myExtensionSQL = ext.isEmpty() ? "" : "Extension = '" + ext + "'";

            // Event type filter
            String type = myEventTypeDropdown.getValue();
            myEventSQL = (type == null || type.equals("None")) ? "" : "Event = '" + type + "'";

            // Start date filter
            if (myStartDatePicker.getValue() != null) {
                myStartSQL = "Timestamp >= '" + myStartDatePicker.getValue() + "T00:00:00'";
            } else {
                myStartSQL = "";
            }

            // End date filter
            if (myEndDatePicker.getValue() != null) {
                myEndSQL = "Timestamp <= '" + myEndDatePicker.getValue() + "T23:59:59'";
            } else {
                myEndSQL = "";
            }

            // Directory filter (from user input)
            String path = myDirectoryField.getText().trim();
            myDirectorySQL = path.isEmpty() ? "" : "Directory LIKE '%" + path + "%'";

            // Run filtered search
            DATABASE.search(myDirectorySQL, myStartSQL, myEndSQL, myEventSQL, myExtensionSQL);
        });

        myEmailButton.setOnAction(e -> {
            new EmailView().display();
        });

        // bind textfield
        myResultTable.setItems(DATABASE.getQuery());

    }

    /**
     * This helper builds a labeled vertical layout: label on top, field underneath.
     * Makes it easy to reuse across form rows.
     *
     * @param theLabelText the label text to be set to.
     * @param theField the control field to be set to.
     */
    private VBox createLabeledField(final String theLabelText, final Control theField) {
        Label label = new Label(theLabelText);
        VBox box = new VBox(5, label, theField); // 5px spacing between label and field
        return box;
    }

}

