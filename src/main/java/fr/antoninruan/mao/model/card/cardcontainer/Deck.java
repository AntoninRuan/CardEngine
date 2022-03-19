package fr.antoninruan.mao.model.card.cardcontainer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.card.Card;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Deck extends CardContainer {

    public static final Image BLUEBACK = new Image(MainApp.class.getClassLoader().getResource("card/Blueback.png").toString());

    public Deck() {
        super(null);
    }


//    static {
//        deck.addListener((ListChangeListener<? super Card>) change -> {
//            while (change.next()) {
//                if(change.wasRemoved())
//                    Platform.runLater(() -> MainApp.getRootController().removeDeckCard(change.getRemovedSize()));
//                else if (change.wasAdded())
//                    Platform.runLater(() -> MainApp.getRootController().addDeckCard(change.getAddedSize()));
//            }
//        });
//    }

    public int getSize() {
        return super.keys.size();
    }

    public Card getLastCard() {
        if (super.keys.isEmpty())
            return null;
        return super.keys.get(super.keys.size() - 1);
    }

    @Override
    public void add(Card card) {
        super.add(card);
        Platform.runLater(() -> MainApp.getRootController().addDeckCard(1));
    }

    @Override
    public void remove(Card card) {
        super.remove(card);
        Platform.runLater(() -> MainApp.getRootController().removeDeckCard(1));
    }

    public void removeLast() {
        if (super.keys.isEmpty())
            return;

        super.keys.remove(super.keys.size() - 1);
    }
    @Override
    public void moveCardTo(Card card, CardContainer dest) {
        ImageView view = (ImageView) super.container.getChildren().get(super.container.getChildren().size() - 1);
        super.keys.remove(card);
        MainApp.getRootController().animateMove(.3, super.container, view, dest.container, () -> {
            dest.add(card);
            super.container.getChildren().remove(view);
        });
    }

    public void setFromJson(JsonArray jsonArray) {
        Platform.runLater(() -> {
            MainApp.getRootController().removeDeckCard(super.keys.size());
            super.keys.clear();
            for (JsonElement element : jsonArray) {
                JsonObject object = element.getAsJsonObject();
                String suit = object.get("suit").getAsString();
                String value = object.get("value").getAsString();
                super.add(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
            }
        });
    }
}
