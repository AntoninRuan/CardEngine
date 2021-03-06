package fr.antoninruan.mao.view;

import com.google.gson.JsonObject;
import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.card.Card;
import fr.antoninruan.mao.model.card.cardcontainer.Deck;
import fr.antoninruan.mao.model.card.cardcontainer.Hand;
import fr.antoninruan.mao.utils.rabbitmq.RabbitMQManager;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class RootLayoutController {

    private final HashMap<Integer, List<Point2D>> positions = new HashMap<>();

    private int ownId;

    @FXML
    private StackPane layout;

    @FXML
    private Pane area;

    @FXML
    private HBox cardHistory;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane deck;

    @FXML
    private StackPane playedStack;

    @FXML
    private AnchorPane hand;

    private final Hand ownHand = new Hand(0, 261, 1050, 0, 25, 1015, true, 150);

    private final HashMap<Hand, Pane> hands = new HashMap<>();

    @FXML
    private void initialize() {

        initPosition();

        addDeckCard(MainApp.getDeck().getSize());

        scrollPane.hvalueProperty().bind(cardHistory.widthProperty());

        ownHand.setContainer(hand);

        MainApp.getPlayedStack().setContainer(playedStack);
        MainApp.getDeck().setContainer(deck);


        deck.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.isAltDown() && mouseEvent.getButton() == MouseButton.MIDDLE) {
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
                } else if (s[0].equals("playedStack")){
                    object.addProperty("source", "playedStack");
                } else
                    return;

                object.addProperty("destination", "deck");
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        hand.setOnMouseClicked(mouseEvent -> {
            if((mouseEvent.isControlDown() || mouseEvent.isMetaDown()) && mouseEvent.getButton() == MouseButton.SECONDARY) {
                JsonObject object = new JsonObject();
                object.addProperty("type", "knock");
                RabbitMQManager.sendGameAction(object.toString());
            } else if((mouseEvent.isAltDown() || mouseEvent.isMetaDown()) && mouseEvent.getButton() == MouseButton.PRIMARY) {
                JsonObject object = new JsonObject();
                object.addProperty("type", "rub");
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        hand.setOnDragOver(event -> {
            if(event.getDragboard().hasString()) {
                String s = event.getDragboard().getString();
                if(s.equals("deck") || s.equals("playedStack"))
                    event.acceptTransferModes(TransferMode.MOVE);
            }
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
                JsonObject object = new JsonObject();
                object.addProperty("type", "rollback");
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        playedStack.setOnDragDetected(event -> {
            if(!MainApp.getPlayedStack().getCards().isEmpty()) {
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
                } else {
                    return;
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

    public void removeDeckCard(int number) {
        for (int i = 0; i < number; i ++) {
            if(deck.getChildren().size() > 0)
                deck.getChildren().remove(deck.getChildren().size() - 1);
        }
    }

    public void addDeckCard(int number) {
        for (int i = 0; i < number; i ++) {
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
    }

    public Pair<ImageView, ImageView> addPlayedCard(Card card) {
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
        ImageView hist = new ImageView(card.getImage());
        hist.setPreserveRatio(true);
        hist.setFitHeight(69);
        cardHistory.getChildren().add(hist);
        return new Pair<>(view, hist);
    }

    public void removePlayedCard(ImageView view) {
        playedStack.getChildren().remove(view);
    }

    public void removeCardHistory(ImageView view) {
        cardHistory.getChildren().remove(view);
    }

    public Hand addPlayer(String name, int id) {
        if(hands.size() >= positions.keySet().size())
            return null;
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-border-color: #000");
        pane.setPrefWidth(441);
        pane.setScaleX(.7);
        pane.setScaleY(.7);
        pane.setPrefHeight(161);
        area.getChildren().add(pane);

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
                } else if (s.equals("playedStack")) {
                    object.addProperty("source", "playedStack");
//                    hand.add(PlayedStack.pickLastCard());
                }
                RabbitMQManager.sendGameAction(object.toString());
            }
        });

        hands.put(hand, pane);

        updateHands();
        return hand;
    }

    private void updateHands() {
        List<Point2D> positions = this.positions.get(hands.size() - 1);
        double angle = 360. / (double) (hands.size() + 1);
        int j = 0;
        List<Pair<Integer, Hand>> sortedHandId = hands.keySet().stream()
                .map(h -> new Pair<>(h.getId(), h))
                .sorted(Comparator.comparingInt(o -> rotate(o.getKey(), hands.size())))
                .collect(Collectors.toList());
        for(Pair<Integer, Hand> pair : sortedHandId) {
            Hand hand = pair.getValue();
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
        return Math.floorMod(i - ownId - 1, size + 1);
    }

    public void removePlayer(int id) {
        Hand hand = hands.keySet().stream().filter(h -> h.getId() == id).findFirst().get();

        if(!hand.getCards().isEmpty()) {
            for(Card card : new ArrayList<>(hand.getCards())) {
                hand.getCards().remove(card);
            }
        }
        area.getChildren().remove(hands.get(hand));
        hands.remove(hand);
        for (Hand h : hands.keySet().stream().filter(h -> h.getId() > id).collect(Collectors.toList())) {
            h.setId(h.getId() - 1);
        }
        if(ownId > id) {
            this.ownId --;
            this.ownHand.setId(ownId);
        }
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
            return hands.keySet().stream().filter(h -> h.getId() == id).findFirst().get();
    }

    public void animateMove(double time, Node container, ImageView card, Node dest, Runnable endTask) {
        try {
            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.seconds(time));
            Point2D pointDest = container.parentToLocal(getCenter(dest.getBoundsInParent()));
            Point2D pointFrom = getCenter(card.getBoundsInParent());
            transition.setByX(-pointFrom.getX() + pointDest.getX());
            transition.setByY(-pointFrom.getY() + pointDest.getY());
            transition.setNode(card);
            transition.play();
            transition.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue.greaterThanOrEqualTo(Duration.seconds(time))) {
                    endTask.run();
                }
            });
            transition.play();
//            endTask.run();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private Point2D getCenter(Bounds bounds) {
        return new Point2D((bounds.getMinX() + bounds.getMaxX()) * .5, (bounds.getMinY() + bounds.getMaxY()) * .5);
    }

    public StackPane getLayout() {
        return layout;
    }

    public StackPane getDeck() {
        return deck;
    }

    public StackPane getPlayedStack() {
        return playedStack;
    }

    public void toggleChat() {
        MainApp.toggleChat();
    }

}
