package fr.antoninruan.mao.model.chat;

import java.util.ArrayList;
import java.util.List;

public class MessageHistory {

    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        messages.add(message);
    }

    public Message getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

}
