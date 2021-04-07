package fr.antoninruan.mao.model;

import fr.antoninruan.mao.MainApp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;


public class Hand {

    private int id;

    private final double baseX;
    private final double baseY;
    private final double baseRotate;
    private final double angleDelta;
    private final double length ;

    private final boolean visible;

    private final int cardHeight;

    private Pane container;

    private ObservableMap<Card, ImageView> cards = FXCollections.observableHashMap();
    private ArrayList<Card> keys = new ArrayList<>();

    public Hand(int id, double baseX, double baseY, double baseRotate, double angleDelta, double length, boolean visible, int cardHeight) {
        this.id = id;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseRotate = baseRotate;
        this.angleDelta = angleDelta;
        this.length = length;
        this.visible = visible;
        this.cardHeight = cardHeight;
    }

    public void setContainer(Pane container) {
        this.container = container;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageView add(Card card) {
        ImageView view = visible ? new ImageView(card.getImage()) : new ImageView(Deck.BLUEBACK);
        view.setPreserveRatio(true);
        view.setFitHeight(cardHeight);

        if(visible)
            view.setOnDragDetected(event -> {
                if(event.getButton() == MouseButton.PRIMARY) {
                    Dragboard dragboard = view.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("hand" + "." + id + "." + keys.indexOf(card));
                    dragboard.setContent(content);
                }
            });


        keys.add(card);
        cards.put(card, view);

        Platform.runLater(() -> {
            updateHand();

            container.getChildren().add(view);
        });

        return view;
    }

    public ImageView remove(Card card) {
        ImageView view = cards.get(card);
        Platform.runLater(() -> {
            container.getChildren().remove(view);
            keys.remove(card);
            cards.remove(card);
            updateHand();
        });
        return view;
    }

    public Card getCard(int id) {
        return keys.get(id);
    }

    public int getCardId(Card card) {
        return keys.indexOf(card);
    }

    public ArrayList<Card> getCards() {
        return keys;
    }

    private void updateHand() {
        double angleDelta = this.angleDelta;

        if ((cards.size() - 1) < angleDelta / 3) {
            angleDelta = 3 * (cards.size() - 1);
        }

        double rotateIncrement = (cards.size() - 1) == 0 ? 0 : angleDelta / ((double) cards.size() - 1);


        for(int i = 0; i < keys.size(); i ++) {
            ImageView cView = cards.get(keys.get(i));
            double rotate = baseRotate - (angleDelta / 2.) + i * rotateIncrement;
            cView.setRotate(rotate);
            cView.setLayoutX(baseX + length * Math.sin((rotate * 2 * Math.PI) / 360));
            cView.setLayoutY(baseY - length * Math.cos((rotate * 2 * Math.PI) / 360));
            cView.setVisible(true);
        }


    }

}
