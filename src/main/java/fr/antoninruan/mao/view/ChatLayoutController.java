package fr.antoninruan.mao.view;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.utils.chat.Message;
import fr.antoninruan.mao.utils.chat.MessageHistory;
import fr.antoninruan.mao.utils.rabbitmq.RabbitMQManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ChatLayoutController {

    @FXML
    private AnchorPane pane;

    @FXML
    private VBox messages;

    @FXML
    private TextField text;

    @FXML
    private Button send;

    private MessageHistory history = new MessageHistory();

    @FXML
    private void initialize() {
        pane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                sendMessage();
        });
    }

    public void sendMessage() {
        String msg = text.getText().trim();
        if (msg.isEmpty())
            return;

        RabbitMQManager.sendChatMessage(MainApp.getUsername(), msg);
        text.clear();
    }

    public void addMessage(String sender, String content) {
        try {
            if (!history.isEmpty() && history.getLastMessage().getSender().equals(sender)) {
                Label text = new Label(content);
                text.setWrapText(true);
                history.getLastMessage().getContainer().getChildren().add(text);
            } else {
                VBox vBox = new VBox();
                Label username = new Label(sender + ":");
                username.setFont(Font.font("System", FontWeight.BOLD, 15));
                Label text = new Label(content);
                text.setWrapText(true);
                vBox.getChildren().addAll(username, text);
                Message message = new Message(sender, content, vBox);

                if (!history.isEmpty()) {
                    Separator separator = new Separator();
                    separator.setPadding(new Insets(5));
                    separator.setOpacity(0);
                    messages.getChildren().add(messages.getChildren().size(), separator);
                }

                messages.getChildren().add(messages.getChildren().size(), vBox);
                history.addMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
