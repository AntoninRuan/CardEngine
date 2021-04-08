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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {

    public static final Image BLUEBACK = new Image(MainApp.class.getClassLoader().getResource("card/Blueback.png").toString());

    private static ObservableList<Card> deck = FXCollections.observableArrayList();

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

/*    public static void init() {
        for (Card.Suit s : Card.Suit.values()) {
            for (Card.Value v : Card.Value.values()) {
//                deck.add(new Card(s, v));
            }
        }
    }

    public static void shuffle() {
        List<Card> d = Arrays.asList(deck.toArray(new Card[]{}));
        Collections.shuffle(d);
        deck.clear();
        deck.addAll(d);
    }*/

    public static ObservableList<Card> getDeck() {
        return deck;
    }

    public static int getSize() {
        return deck.size();
    }

    public static Card draw() {
        if (deck.isEmpty())
            return null;

        Card card = deck.get(deck.size() - 1);
        deck.remove(deck.size() - 1);
        return card;
    }

    public static void put(Card card) {
        deck.add(card);
    }

    public static void setFromJson(JsonArray jsonArray) {
        System.out.println("New deck: " + jsonArray.toString());
//        ArrayList<Card> deck = new ArrayList<>();
        Deck.getDeck().clear();
        for (JsonElement element : jsonArray) {
            JsonObject object = element.getAsJsonObject();
            String suit = object.get("suit").getAsString();
            String value = object.get("value").getAsString();
            Deck.put(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
        }
//        Deck.getDeck().setAll(deck);
    }

}
