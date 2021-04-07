package fr.antoninruan.mao.model;

import fr.antoninruan.mao.MainApp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.List;

public class PlayedStack {

    private static HashMap<Card, ImageView> cards = new HashMap<>();
    private static ObservableList<Card> keys = FXCollections.observableArrayList();

    static {

        keys.addListener((ListChangeListener<? super Card>) change -> {
            while (change.next()) {
                if(change.wasAdded()) {
                    List<? extends Card> added = change.getAddedSubList();
                    for (Card card : added) {
                        Platform.runLater(() -> {
                            ImageView view = MainApp.getRootController().addPlayedCard(card);
                            cards.put(card, view);
                        });
                    }
                } else if(change.wasRemoved()) {
                    List<? extends Card> removed = change.getRemoved();
                    for(Card card : removed) {
                        Platform.runLater(() -> {
                            ImageView view = cards.get(card);
                            MainApp.getRootController().removePlayerCard(view);
                            cards.remove(card);
                            keys.remove(card);
                        });
                    }
                }
            }
        });

    }

    public static ObservableList<Card> getCards() {
        return keys;
    }

    public static void addCard(Card card) {
        keys.add(card);
    }

    public static Card pickLastCard() {
        Card card = keys.get(keys.size() - 1);
        keys.remove(card);
        return card;
    }

}
