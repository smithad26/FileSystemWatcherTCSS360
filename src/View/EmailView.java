package View;

import Model.Email;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class EmailView {

    private File selectedFile;
    public void display() {
        Stage emailStage = new Stage();
        emailStage.setTitle("Email");
        emailStage.setResizable(false);

        Label label = new Label("Enter your email address:");
        TextField emailField = new TextField();

        Label fileLabel = new Label("No file selected");
        Button browseButton = new Button("Browse for .csv file");

        Button sendButton = new Button("Send Email");
        Label statusLabel = new Label();

        // Browse button action
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select CSV File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

            File file = fileChooser.showOpenDialog(emailStage);
            if (file != null) {
                selectedFile = file;
                fileLabel.setText("Selected file: " + file.getName());
            } else {
                fileLabel.setText("No file selected");
            }
        });

        // Handle button click
        sendButton.setOnAction(e -> {
            String email = emailField.getText();
            if (!email.contains("@")) {
                statusLabel.setText("Invalid email address.");
            } else {
                // Simulate sending an email (replace this with actual sending logic)

                Email sending = new Email(email);
                sending.sendEmailWithAttachment(selectedFile.getAbsolutePath());

                statusLabel.setText("Email sent to: " + email);
                // You could also call a real method here, e.g., sendEmail(email);
            }
        });

        // Layout
        VBox root = new VBox(10, label, emailField, sendButton, statusLabel, fileLabel, browseButton);
        root.setPadding(new Insets(20));

        // Set up the stage
        Scene scene = new Scene(root, 400, 250);
        emailStage.setScene(scene);
        emailStage.show();
    }
}
