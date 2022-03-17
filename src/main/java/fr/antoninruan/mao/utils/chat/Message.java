package fr.antoninruan.mao.utils.chat;

import javafx.scene.layout.VBox;

public class Message {

    private final String sender;
    private final String content;
    private final VBox container;

    public Message(String sender, String content, VBox container) {
        this.sender = sender;
        this.content = content;
        this.container = container;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public VBox getContainer() {
        return container;
    }

}
