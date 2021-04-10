package fr.antoninruan.mao.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.antoninruan.mao.MainApp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class Deck {

    public static final Image BLUEBACK = new Image(MainApp.class.getClassLoader().getResource("card/Blueback.png").toString());

    private static final ObservableList<Card> deck = FXCollections.observableArrayList();

    static {
        deck.addListener((ListChangeListener<? super Card>) change -> {
            while (change.next()) {
                if(change.wasRemoved())
                    Platform.runLater(() -> MainApp.getRootController().removeDeckCard(change.getRemovedSize()));
                else if (change.wasAdded())
                    Platform.runLater(() -> MainApp.getRootController().addDeckCard(change.getAddedSize()));
            }
        });
    }

    public static ObservableList<Card> getDeck() {
        return deck;
    }

    public static int getSize() {
        return deck.size();
    }

    public static Pair<Card, ImageView> getLastCard() {
        if(deck.isEmpty())
            return null;
        ImageView view = (ImageView) MainApp.getRootController().getDeck().getChildren().get(MainApp.getRootController().getDeck().getChildren().size() - 1);
        return new Pair<>(deck.get(deck.size() - 1), view);
    }

    public static void removeLast() {
        if(deck.isEmpty())
            return;

        deck.remove(deck.size() - 1);
    }

    public static void put(Card card) {
        deck.add(card);
    }

    public static void setFromJson(JsonArray jsonArray) {
        Deck.getDeck().clear();
        for (JsonElement element : jsonArray) {
            JsonObject object = element.getAsJsonObject();
            String suit = object.get("suit").getAsString();
            String value = object.get("value").getAsString();
            Deck.put(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
        }
    }

}
