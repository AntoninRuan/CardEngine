package fr.antoninruan.mao.model.cardcontainer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.Card;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class Deck extends CardContainer implements Stack {

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
        return keys.size();
    }

    public Card getLastCard() {
        if(keys.isEmpty())
            return null;
        return keys.get(keys.size() - 1);
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
        if(keys.isEmpty())
            return;

        keys.remove(keys.size() - 1);
    }
    @Override
    public void moveCardTo(Card card, CardContainer dest) {
        ImageView view = (ImageView) this.container.getChildren().get(this.container.getChildren().size() - 1);
        this.keys.remove(card);
        MainApp.getRootController().animateMove(.3, container, view, dest.container, () -> {
            dest.add(card);
            container.getChildren().remove(view);
        });
    }

    public void setFromJson(JsonArray jsonArray) {
        Platform.runLater(() -> {
            MainApp.getRootController().removeDeckCard(this.keys.size());
            this.keys.clear();
            for (JsonElement element : jsonArray) {
                JsonObject object = element.getAsJsonObject();
                String suit = object.get("suit").getAsString();
                String value = object.get("value").getAsString();
                this.add(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
            }
        });
    }
}
