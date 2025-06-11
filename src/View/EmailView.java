package View;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmailView {
    public void display() {
        Stage emailStage = new Stage();
        emailStage.setTitle("Email");
        emailStage.setResizable(false);

        Label label = new Label("Enter your email address:");
        TextField emailField = new TextField();
        Button sendButton = new Button("Send Email");
        Label statusLabel = new Label();

        // Handle button click
        sendButton.setOnAction(e -> {
            String email = emailField.getText();
            if (email.isEmpty() || !email.contains("@")) {
                statusLabel.setText("Invalid email address.");
            } else {
                // Simulate sending an email (replace this with actual sending logic)
                statusLabel.setText("Email sent to: " + email);
                // You could also call a real method here, e.g., sendEmail(email);
            }
        });

        // Layout
        VBox root = new VBox(10, label, emailField, sendButton, statusLabel);
        root.setPadding(new Insets(20));

        // Set up the stage
        Scene scene = new Scene(root, 300, 200);
        emailStage.setScene(scene);
        emailStage.show();
    }
}
