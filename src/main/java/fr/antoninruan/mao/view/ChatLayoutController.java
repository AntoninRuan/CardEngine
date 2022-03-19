package fr.antoninruan.mao.view;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.chat.Emote;
import fr.antoninruan.mao.model.chat.Message;
import fr.antoninruan.mao.model.chat.MessageHistory;
import fr.antoninruan.mao.utils.rabbitmq.RabbitMQManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ChatLayoutController {

    @FXML
    private AnchorPane pane;

    @FXML
    private VBox content;

    @FXML
    private ScrollPane messagesPane;

    @FXML
    private VBox messages;

    @FXML
    private TextField text;

    @FXML
    private Button send;

    private MessageHistory history = new MessageHistory();
    private ScrollPane emotesSelect;
    private boolean emotesSelectOpen;

    @FXML
    private void initialize() {
        pane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                sendMessage();
        });

        messagesPane.vvalueProperty().bind(messages.heightProperty());

        emotesSelect = new ScrollPane();
        emotesSelect.setPrefWidth(Region.USE_COMPUTED_SIZE);
        emotesSelect.setPrefHeight(Region.USE_COMPUTED_SIZE);
        emotesSelect.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        emotesSelect.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        emotesSelect.setPannable(true);

        HBox box = new HBox();
        box.setPrefWidth(Region.USE_COMPUTED_SIZE);
        box.setPrefHeight(Region.USE_COMPUTED_SIZE);
        box.setFillHeight(true);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(2);
        box.setPadding(new Insets(2, 2, 7, 2));

        for (Emote emote : Emote.getEmotes().values().stream().sorted(Comparator.comparing(Emote::getName)).collect(Collectors.toList())) {
            Button button = new Button();
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setStyle("-fx-background-color: rgba(0,0,0,0)");
            button.setPadding(new Insets(-4));
            ImageView view = new ImageView(emote.getImage());
            view.setPreserveRatio(true);
            view.setFitHeight(150);
            button.setGraphic(view);
            button.setOnAction(actionEvent -> {
                sendEmote(emote);
            });
            box.getChildren().add(button);
        }

        emotesSelect.setContent(box);
        emotesSelectOpen = false;
    }

    public void sendMessage() {
        String msg = text.getText().trim();
        if (msg.isEmpty())
            return;

        RabbitMQManager.sendChatMessage(MainApp.getUsername(), msg, "");
        text.clear();
    }

    public void sendEmote(Emote emote) {
        RabbitMQManager.sendChatMessage(MainApp.getUsername(), "", emote.getName());
    }

    public void addMessage(String sender, String content, String emote) {
        try {
            if (!history.isEmpty() && history.getLastMessage().getSender().equals(sender)) {
                if (emote != null && !emote.isEmpty()) {
                    Emote e = Emote.getEmote(emote);
                    ImageView view = new ImageView(e.getImage());
                    view.setPreserveRatio(true);
                    view.setFitWidth(150);
                    history.getLastMessage().getContainer().getChildren().add(view);
                } else {
                    Label msg = new Label(content);
                    msg.setWrapText(true);
                    history.getLastMessage().getContainer().getChildren().add(msg);
                }
            } else {
                VBox vBox = new VBox();
                Label username = new Label(sender + ":");
                username.setFont(Font.font("System", FontWeight.BOLD, 15));
                vBox.getChildren().addAll(username);
                if (emote != null && !emote.isEmpty()) {
                    Emote e = Emote.getEmote(emote);
                    ImageView view = new ImageView(e.getImage());
                    view.setPreserveRatio(true);
                    view.setFitWidth(150);
                    vBox.getChildren().add(view);
                } else {
                    Label msg = new Label(content);
                    msg.setWrapText(true);
                    vBox.getChildren().add(msg);
                }
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

    public void toggleEmojiSelect() {
        if (emotesSelectOpen) {
            content.getChildren().remove(emotesSelect);
            emotesSelectOpen = false;
        } else {
            content.getChildren().add(1, emotesSelect);
            emotesSelectOpen = true;
        }
    }

}
