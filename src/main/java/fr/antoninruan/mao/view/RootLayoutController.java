package fr.antoninruan.mao.view;

import com.google.gson.JsonObject;
import fr.antoninruan.mao.model.*;
import fr.antoninruan.mao.utils.rabbitmq.RabbitMQManager;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;
import java.util.stream.Collectors;

public class RootLayoutController {

    private final HashMap<Integer, List<Point2D>> positions = new HashMap<>();

    private int ownId;

    @FXML
    private Pane area;

    @FXML
    private StackPane deck;

    @FXML
    private StackPane playedStack;

    @FXML
    private AnchorPane hand;

    private final Hand ownHand = new Hand(0, 261, 1050, 0, 25, 1015, true, 150);

    private final HashMap<Hand, Pane> hands = new HashMap<>();
    private final HashMap<Integer, Hand> others = new HashMap<>();
    private final ArrayList<Integer> usedIds = new ArrayList<>();

    @FXML
    private void initialize() {

        initPosition();

        for(int i = 0; i < Deck.getSize(); i ++) {
            addDeckCard();
        }

        ownHand.setContainer(hand);

        deck.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.isAltDown() && mouseEvent.getButton() == MouseButton.MIDDLE) {
//                System.out.println("shuffle");
                JsonObject object = new JsonObject();
                object.addProperty("type", "shuffle");
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        deck.setOnDragDetected(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                Dragboard dragboard = deck.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("deck");
                dragboard.setContent(content);
            }
        });

        deck.setOnDragOver(event -> {
            if(event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        deck.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasString()) {
                String[] s = dragboard.getString().split("\\.");
                JsonObject object = new JsonObject();
                object.addProperty("type", "card_move");
                if(s[0].equals("hand")) {
                    object.addProperty("source", Integer.parseInt(s[1]));
                    object.addProperty("card_id", Integer.parseInt(s[2]));
                    /*int handId = Integer.parseInt(s[1]);
                    int cardId = Integer.parseInt(s[2]);
                    Card card;
                    if(handId == ownId) {
                        card = ownHand.getCards().get(cardId);
                        ownHand.remove(card);
                    } else {
                        card = others.get(handId).getCards().get(cardId);
                        others.get(handId).remove(card);
                    }
                    Deck.put(card);*/
                } else {
                    object.addProperty("source", "playedStack");
                }
                object.addProperty("destination", "deck");
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        hand.setOnDragOver(event -> {
            if(event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        hand.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String s = dragboard.getString();
                JsonObject object = new JsonObject();
                object.addProperty("type", "card_move");
                if(s.equals("deck")) {
                    object.addProperty("source", "deck");
                   /* Card card = Deck.draw();
                    if (card != null)
                        ownHand.add(card);*/
                } else if (s.equals("playedStack")) {
                    object.addProperty("source", "playedStack");
//                    ownHand.add(PlayedStack.pickLastCard());
                }
                object.addProperty("destination", ownId);
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        playedStack.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.isAltDown() && mouseEvent.getButton() == MouseButton.MIDDLE) {
                System.out.println("Rollback");
                JsonObject object = new JsonObject();
                object.addProperty("type", "rollback");
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        playedStack.setOnDragDetected(event -> {
            if(!PlayedStack.getCards().isEmpty()) {
                Dragboard dragboard = playedStack.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("playedStack");
                dragboard.setContent(content);
            }
        });

        playedStack.setOnDragOver(event -> {
            if(event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        playedStack.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasString()) {
                String[] s = dragboard.getString().split("\\.");
                JsonObject object = new JsonObject();
                object.addProperty("type", "card_move");
                object.addProperty("destination", "playedStack");
                if(s[0].equals("deck"))
                    object.addProperty("source", "deck");
//                    addPlayedCard(Deck.draw());
                else if (s[0].equals("hand")) {
                    object.addProperty("source", Integer.parseInt(s[1]));
                    object.addProperty("card_id", Integer.parseInt(s[2]));
                    /*int handId = Integer.parseInt(s[1]);
                    int cardId = Integer.parseInt(s[2]);
                    Card card;
                    if(handId == ownId) {
                        card = ownHand.getCards().get(cardId);
                        ownHand.remove(card);
                    } else {
                        card = others.get(handId).getCards().get(cardId);
                        others.get(handId).remove(card);
                    }
                    PlayedStack.addCard(card);*/
                }
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

    }

    private void initPosition() {
        ArrayList<Point2D> pos = new ArrayList<>();
        pos.add(new Point2D(495, -10));
        positions.put(0, new ArrayList<>(pos));

        pos.clear();
        pos.add(new Point2D(34, 93));
        pos.add(new Point2D(908,93));
        positions.put(1, new ArrayList<>(pos));

        pos.clear();
        pos.add(new Point2D(-150, 345));
        pos.add(new Point2D(471, -10));
        pos.add(new Point2D(1092, 345));
        positions.put(2, new ArrayList<>(pos));

        pos.clear();
        pos.add(new Point2D(-63, 525));
        pos.add(new Point2D(100, 55));
        pos.add(new Point2D(842, 55));
        pos.add(new Point2D(1016, 525));
        positions.put(3, new ArrayList<>(pos));

        pos.clear();
        pos.add(new Point2D(0, 577));
        pos.add(new Point2D(0, 113));
        pos.add(new Point2D(471, -10));
        pos.add(new Point2D(942, 113));
        pos.add(new Point2D(942, 577));
        positions.put(4, new ArrayList<>(pos));

        pos.clear();
        pos.add(new Point2D(39, 614));
        pos.add(new Point2D(-106, 233));
        pos.add(new Point2D(236, 40));
        pos.add(new Point2D(706, 40));
        pos.add(new Point2D(1048, 233));
        pos.add(new Point2D(903, 614));
        positions.put(5, new ArrayList<>(pos));
    }

    public Hand getOwnHand() {
        return ownHand;
    }

    public void removeDeckCard() {
        deck.getChildren().remove(deck.getChildren().size() - 1);
    }

    public void addDeckCard() {
        ImageView view = new ImageView(Deck.BLUEBACK);
        view.setPreserveRatio(true);
        view.setFitHeight(150);
        Random random = new Random();

        int rotate = random.nextInt(11) - 5;
        int translateX = random.nextInt(11) - 5;
        int translateY = random.nextInt(11) - 5;

        view.setRotate(rotate);
        view.setTranslateX(translateX);
        view.setTranslateY(translateY);

        deck.getChildren().add(view);
    }

    public ImageView addPlayedCard(Card card) {
        ImageView view = new ImageView(card.getImage());
        view.setPreserveRatio(true);
        view.setFitHeight(150);

        Random random = new Random();

        int rotate = random.nextInt(21) - 10;
        int translateX = random.nextInt(21) - 10;
        int translateY = random.nextInt(21) - 10;

        view.setRotate(rotate);
        view.setTranslateX(translateX);
        view.setTranslateY(translateY);

        playedStack.getChildren().add(view);
        return view;
    }

    public void removePlayerCard(ImageView view) {
        playedStack.getChildren().remove(view);
    }

    public void addPlayer(String name, int id) {
        if(others.size() >= positions.keySet().size())
            return;
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-border-color: #000");
        pane.setPrefWidth(441);
        pane.setScaleX(.7);
        pane.setScaleY(.7);
        pane.setPrefHeight(161);
        area.getChildren().add(pane);

//        name = "Joueur " + id;
        Label label = new Label(name);
        label.setLayoutX(0);
        label.setLayoutY(0);
        label.setFont(Font.font(Font.getDefault().getFamily(), 20));
        label.setTextFill(Color.WHITE);
        pane.getChildren().add(label);

        Hand hand = new Hand(id, 183, 739, 0, 20,710, false, 105);
        hand.setContainer(pane);

        pane.setOnDragOver(event -> {
            if(event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        pane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String s = dragboard.getString();
                JsonObject object = new JsonObject();
                object.addProperty("type", "card_move");
                object.addProperty("destination", hand.getId());
                if(s.equals("deck")) {
                    object.addProperty("source", "deck");
                    /*Card card = Deck.draw();
                    if (card != null) {
                        hand.add(card);
                    }*/
                } else if (s.equals("playedStack")) {
                    object.addProperty("source", "playedStack");
//                    hand.add(PlayedStack.pickLastCard());
                }
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        usedIds.add(id);
        others.put(id, hand);
        hands.put(hand, pane);

        updateHands();
    }

    private void updateHands() {
        List<Point2D> positions = this.positions.get(others.size() - 1);
        double angle = 360. / (double) (others.size() + 1);
        int j = 0;
        for(int i : usedIds.stream().sorted(Comparator.comparingInt(o -> rotate(o, usedIds.size()))).collect(Collectors.toList())) {
            Hand hand = others.get(i);
            Pane pane = hands.get(hand);
            Point2D point = positions.get(j);
            pane.setLayoutX(point.getX());
            pane.setLayoutY(point.getY());
            pane.setRotate(angle * (j+1));
            Label label = (Label) pane.getChildren().stream().filter(node -> node instanceof Label).findFirst().get();
            label.setRotate(-angle * (j+1));
            j++;
        }
    }

    private int rotate(int i, int size) {
        return ((i - ownId) % size);
    }

    public void removePlayer(int id) {
        Hand hand = others.get(id);
        if(!hand.getCards().isEmpty()) {
            for(Card card : new ArrayList<>(hand.getCards())) {
                hand.remove(card);
                Deck.put(card);
            }
        }
        usedIds.remove((Integer) hand.getId());
        others.remove(hand.getId());
        area.getChildren().remove(hands.get(hand));
        hands.remove(hand);
        updateHands();
    }

    public int getOwnId() {
        return ownId;
    }

    public void setOwnId(int ownId) {
        this.ownId = ownId;
        this.ownHand.setId(ownId);
    }

    public Hand getHand(int id) {
        if(id == ownId)
            return ownHand;
        else
            return others.get(id);
    }

}
