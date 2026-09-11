package kelore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Displays the JavaFX user interface for Kelore. */
public class Main extends Application {
    private final Kelore kelore = new Kelore();
    private final VBox dialogContainer = new VBox(10);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    @Override
    public void start(Stage stage) {
        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        dialogContainer.setPadding(new Insets(10));
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));

        userInput.setPromptText("Enter a command...");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        HBox inputArea = new HBox(8, userInput, sendButton);
        inputArea.setPadding(new Insets(10));

        BorderPane root = new BorderPane(scrollPane);
        root.setBottom(inputArea);
        Scene scene = new Scene(root, 500, 600);

        userInput.setOnAction(event -> handleUserInput());
        sendButton.setOnAction(event -> handleUserInput());

        addDialog(kelore.getWelcomeMessage(), false);
        stage.setTitle("Kelore");
        stage.setScene(scene);
        stage.show();
        userInput.requestFocus();
    }

    /** Adds the user's command and Kelore's response to the conversation. */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        addDialog(input, true);
        addDialog(kelore.getResponse(input), false);
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /**
     * Adds one message to the appropriate side of the conversation.
     *
     * @param message Message to display.
     * @param isUser True for a user message; false for a Kelore message.
     */
    private void addDialog(String message, boolean isUser) {
        Label label = new Label((isUser ? "You:\n" : "Kelore:\n") + message.strip());
        label.setMaxWidth(350);
        label.setWrapText(true);
        label.setPadding(new Insets(8));
        label.setStyle(isUser
                ? "-fx-background-color: #dbeafe; -fx-background-radius: 8;"
                : "-fx-background-color: #e5e7eb; -fx-background-radius: 8;");

        HBox row = new HBox(label);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        dialogContainer.getChildren().add(row);
    }
}
