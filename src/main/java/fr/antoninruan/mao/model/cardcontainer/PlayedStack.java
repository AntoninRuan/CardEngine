package fr.antoninruan.mao.model.cardcontainer;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.Card;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayedStack extends CardContainer {

    private final HashMap<Card, Pair<ImageView, ImageView>> cards = new HashMap<>();

    public PlayedStack() {
        super(null);
    }

    @Override
    public void add(Card card) {
        super.add(card);
        Platform.runLater(() -> {
            Pair<ImageView, ImageView> views = MainApp.getRootController().addPlayedCard(card);
            cards.put(card, views);
        });
    }

    @Override
    public void remove(Card card) {
        super.remove(card);
        Pair<ImageView, ImageView> views = cards.get(card);
        cards.remove(card);
        Platform.runLater(() -> {
            MainApp.getRootController().removePlayedCard(views.getKey());
            MainApp.getRootController().removeCardHistory(views.getValue());
        });
    }

    public void clear() {
        for(Card c : new ArrayList<>(keys)) {
            remove(c);
        }
    }

    public Card getLastCard() {
        return keys.get(keys.size() - 1);
    }

    @Override
    public void moveCardTo(Card card, CardContainer dest) {
        Pair<ImageView, ImageView> views = cards.get(card);
        this.keys.remove(card);
        this.cards.remove(card);
        Platform.runLater(() -> MainApp.getRootController().removeCardHistory(views.getValue()));
        MainApp.getRootController().animateMove(.3, container, views.getKey(), dest.container, () -> {
            dest.add(card);
            container.getChildren().remove(views.getKey());
        });
    }
}
