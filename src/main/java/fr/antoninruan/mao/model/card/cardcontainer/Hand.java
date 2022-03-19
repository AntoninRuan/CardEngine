package fr.antoninruan.mao.model.card.cardcontainer;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.card.Card;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;

import java.util.Arrays;
import java.util.Objects;

public class Hand extends CardContainer {

    private int id;

    private final double baseX;
    private final double baseY;
    private final double baseRotate;
    private final double angleDelta;
    private final double length ;

    private final boolean visible;

    private final int cardHeight;

    private final ObservableMap<Card, ImageView> cards = FXCollections.observableHashMap();

    public Hand(int id, double baseX, double baseY, double baseRotate, double angleDelta, double length, boolean visible, int cardHeight) {
        super(null);
        this.id = id;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseRotate = baseRotate;
        this.angleDelta = angleDelta;
        this.length = length;
        this.visible = visible;
        this.cardHeight = cardHeight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void add(Card card) {
        super.add(card);
        ImageView view = visible ? new ImageView(card.getImage()) : new ImageView(Deck.BLUEBACK);
        view.setPreserveRatio(true);
        view.setFitHeight(cardHeight);

        if(visible) {
            view.setStyle("-fx-border-color: #121212");
            view.setOnDragDetected(event -> {
                if(event.getButton() == MouseButton.PRIMARY) {
                    Dragboard dragboard = view.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("hand" + "." + id + "." + super.keys.indexOf(card));
                    dragboard.setContent(content);
                }
            });
        }

        cards.put(card, view);

        Platform.runLater(() -> {
            updateHand();

            super.container.getChildren().add(view);
        });

    }

    public void remove(Card card) {
        super.remove(card);
        ImageView view = cards.get(card);
        Platform.runLater(() -> {
            super.container.getChildren().remove(view);
            cards.remove(card);
            updateHand();
        });
    }

    @Override
    public void moveCardTo(Card card, CardContainer dest) {
        ImageView view = cards.get(card);
        super.keys.remove(card);
        this.cards.remove(card);
        Platform.runLater(this::updateHand);
        MainApp.getRootController().animateMove(.3, super.container, view, dest.container, () -> {
            dest.add(card);
            super.container.getChildren().remove(view);
        });
    }

    public Card getCard(int id) {
        return super.keys.get(id);
    }

    public ImageView getView(Card card) {
        return cards.get(card);
    }

    private void updateHand() {
        double angleDelta = this.angleDelta;

        if ((cards.size() - 1) < angleDelta / 3) {
            angleDelta = 3 * (cards.size() - 1);
        }

        double rotateIncrement = (cards.size() - 1) == 0 ? 0 : angleDelta / ((double) cards.size() - 1);


        for(int i = 0; i < super.keys.size(); i ++) {
            ImageView cView = cards.get(super.keys.get(i));
            double rotate = baseRotate - (angleDelta / 2.) + i * rotateIncrement;
            cView.setRotate(rotate);
            cView.setLayoutX(baseX + length * Math.sin((rotate * 2 * Math.PI) / 360));
            cView.setLayoutY(baseY - length * Math.cos((rotate * 2 * Math.PI) / 360));
            cView.setVisible(true);
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Hand hand = (Hand) o;
        return id == hand.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "Hand{" +
                "keys=" + Arrays.deepToString(super.keys.toArray()) +
                ", id=" + id +
                '}';
    }
}
