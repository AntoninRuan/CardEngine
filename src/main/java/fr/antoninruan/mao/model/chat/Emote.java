package fr.antoninruan.mao.model.chat;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class Emote {

    private static Map<String, Emote> emotes = new HashMap<>();

    private String name;
    private Image image;

    public Emote(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public static Map<String, Emote> getEmotes() {
        return emotes;
    }

    public static Emote getEmote(String name) {
        return emotes.get(name);
    }

}
