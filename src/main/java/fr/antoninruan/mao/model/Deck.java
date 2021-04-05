package fr.antoninruan.mao.model;

import fr.antoninruan.mao.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {

    public static final Image BLUEBACK = new Image(MainApp.class.getClassLoader().getResource("card/Blueback.png").toString());

    private static ObservableList<Card> deck = FXCollections.observableArrayList();

    public static void init() {
        for (Card.Suit s : Card.Suit.values()) {
            for (Card.Value v : Card.Value.values()) {
                deck.add(new Card(s, v));
            }
        }
        deck.addListener((ListChangeListener<? super Card>) change -> {
            while (change.next()) {
                if(change.wasRemoved())
                    MainApp.getRootController().removeDeckCard();
                else if (change.wasAdded())
                    MainApp.getRootController().addDeckCard();
            }
        });
    }

    public static void shuffle() {
        List<Card> d = Arrays.asList(deck.toArray(new Card[]{}));
        Collections.shuffle(d);
        deck.clear();
        deck.addAll(d);
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

}
