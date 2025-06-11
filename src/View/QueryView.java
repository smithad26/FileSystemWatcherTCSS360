/*
 * TCSS 360 Course Project
 */
package View;

import Model.Email;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;

import jakarta.mail.MessagingException;

public class QueryView {
    private File lastSavedCsv;
    private Stage stage;

    /**
     * Displays the QueryView window.
     */
    public void display() {
        stage = new Stage();
        VBox layout = new VBox(10);
        TextField emailField = new TextField();
        emailField.setPromptText("Enter recipient email");
        Button saveButton = new Button("Save CSV");
        Button emailButton = new Button("Email CSV");

        saveButton.setOnAction(e -> saveToCSV());
        emailButton.setOnAction(e -> {
            try {
                emailCSV(emailField.getText());
            } catch (IOException | GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        });

        layout.getChildren().addAll(emailField, saveButton, emailButton);
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Query View");
        stage.show();
    }

    /**
     * Emails the CSV file to the specified recipient.
     *
     * @param recipient the email address to send to.
     */
    private void emailCSV(String recipient) throws IOException, GeneralSecurityException {
        if (recipient == null || recipient.trim().isEmpty()) {
            showAlert("Error", "Please enter a recipient email address.");
            return;
        }
        if (lastSavedCsv == null || !lastSavedCsv.exists()) {
            showAlert("Warning", "No CSV file has been saved. Please save query results first.");
            saveToCSV();
            if (lastSavedCsv == null || !lastSavedCsv.exists()) {
                return;
            }
        }
        Email email = new Email(recipient);
        email.sendEmail("File Watcher query results", lastSavedCsv);
        showAlert("Success", "Email sent successfully to " + recipient);
    }

    /**
     * Saves the query results to a CSV file.
     */
    private void saveToCSV() {
        try {
            String fileName = "query_results_" + System.currentTimeMillis() + ".csv";
            lastSavedCsv = new File(fileName);

            String[] headers = {"Name", "Event", "Date"};
            String[][] data = {
                    {"Test1", "FileCreated", "2025-06-11"},
                    {"Test2", "FileModified", "2025-06-11"}
            };

            try (FileWriter writer = new FileWriter(lastSavedCsv)) {
                writer.append(String.join(",", headers));
                writer.append("\n");
                for (String[] row : data) {
                    writer.append(String.join(",", row));
                    writer.append("\n");
                }
                writer.flush();
            }

            showAlert("Success", "CSV file saved as " + lastSavedCsv.getAbsolutePath());
        } catch (IOException e) {
            showAlert("Error", "Failed to save CSV file: " + e.getMessage());
            lastSavedCsv = null;
        }
    }

    /**
     * Displays an alert with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
