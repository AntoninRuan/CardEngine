package fr.antoninruan.mao.model.chat;

import javafx.scene.layout.VBox;

import javax.swing.text.html.ImageView;

public class Message {

    private final String sender;
    private final String content;
    private ImageView view;
    private boolean image;
    private final VBox container;

    public Message(String sender, String content, VBox container) {
        this.sender = sender;
        this.content = content;
        this.container = container;
    }

    public Message(String sender, ImageView view, boolean image, VBox container) {
        this.sender = sender;
        this.content = null;
        this.view = view;
        this.image = image;
        this.container = container;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public ImageView getView() {
        return view;
    }

    public boolean isImage() {
        return image;
    }

    public VBox getContainer() {
        return container;
    }

}
