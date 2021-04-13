package fr.antoninruan.mao.model.cardcontainer;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.Card;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class PlayedStack {

    private static final HashMap<Card, Pair<ImageView, ImageView>> cards = new HashMap<>();
    private static final ObservableList<Card> keys = FXCollections.observableArrayList();

    static {

        keys.addListener((ListChangeListener<? super Card>) change -> {
            while (change.next()) {
                if(change.wasAdded()) {
                    List<? extends Card> added = change.getAddedSubList();
                    for (Card card : added) {
                        Platform.runLater(() -> {
                            Pair<ImageView, ImageView> views = MainApp.getRootController().addPlayedCard(card);
                            cards.put(card, views);
                        });
                    }
                } else if(change.wasRemoved()) {
                    List<? extends Card> removed = change.getRemoved();
                    for(Card card : removed) {
                        Platform.runLater(() -> {
                            Pair<ImageView, ImageView> views = cards.get(card);
                            MainApp.getRootController().removePlayedCard(views.getKey());
                            MainApp.getRootController().removeCardHistory(views.getValue());
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

    public static ImageView getView(Card card) {
        return cards.get(card).getKey();
    }

    public static Card getLastCard() {
        return keys.get(keys.size() - 1);
    }

    public static void removeLastCard() {
        keys.remove(keys.size() - 1);
    }

}
