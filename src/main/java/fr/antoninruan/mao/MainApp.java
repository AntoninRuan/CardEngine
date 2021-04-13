package fr.antoninruan.mao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.mao.model.*;
import fr.antoninruan.mao.utils.DialogUtils;
import fr.antoninruan.mao.utils.rabbitmq.RabbitMQManager;
import fr.antoninruan.mao.view.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.commons.math3.util.Precision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class MainApp extends Application {

    // TODO trier ses cartes

    private static final double HEIGHT = 850;
    private static final double WIDTH = 1383;

    public static final MediaPlayer CARD_MOVE_SOUND =
            new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/card_move.mp3").toString()));
    public static final MediaPlayer SHUFFLE_SOUND =
            new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/shuffle.mp3").toString()));
    public static final MediaPlayer KNOCK_SOUND =
            new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/knock.mp3").toString()));
    public static final MediaPlayer RUB_SOUND =
            new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/rub.mp3").toString()));

    private static Pane rootLayout;
    private static RootLayoutController rootController;

    private static Stage primaryStage;
    public static final Image ICON = new Image(MainApp.class.getClassLoader().getResource("icon.png").toString());

    @Override
    public void start(Stage stage) throws IOException {
        MainApp.primaryStage = stage;
        MainApp.primaryStage.setTitle("Cartes");
        MainApp.primaryStage.getIcons().add(ICON);

        primaryStage.setOnCloseRequest(windowEvent -> {
            RabbitMQManager.stop();
            Platform.exit();
        });

        Optional<ConnectionInfo> connectionInfo = DialogUtils.connect();
        if (connectionInfo.isPresent()) {
            ConnectionInfo info = connectionInfo.get();
            initRootLayout(Precision.round(info.getScale(), 2));
            RabbitMQManager.init(info.getHost(), 5672, "card_engine", "zHaBdgLr388");
            JsonObject response = JsonParser.parseString(RabbitMQManager.connect(info.getName())).getAsJsonObject();
            rootController.setOwnId(response.get("id").getAsInt());
//            System.out.println("response=" + response);
            for (JsonElement element : response.get("players").getAsJsonArray()) {
                JsonObject p = element.getAsJsonObject();
//                System.out.println("Player=" + p.toString());
                Hand h = rootController.addPlayer(p.get("name").getAsString(), p.get("id").getAsInt());
                for(JsonElement card : p.get("cards").getAsJsonArray()) {
                    String suit = card.getAsJsonObject().get("suit").getAsString();
                    String value = card.getAsJsonObject().get("value").getAsString();
                    h.add(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
                }
            }
            Deck.setFromJson(response.get("deck").getAsJsonArray());
            ArrayList<Card> playedStack = new ArrayList<>();
            for(JsonElement element : response.get("played_stack").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                String suit = object.get("suit").getAsString();
                String value = object.get("value").getAsString();
                playedStack.add(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
            }
            PlayedStack.getCards().setAll(playedStack);
            primaryStage.setTitle(info.getName());
            RabbitMQManager.listenGameUpdate();
        } else {
            primaryStage.close();
        }

    }

    private void initRootLayout(double scale) {

//        Deck.init();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/RootLayout.fxml"));

            rootLayout = loader.load();

            rootController = loader.getController();

            Scene scene = new Scene(rootLayout);
            /*scene.setOnKeyPressed(event -> {

                if(event.getCode() == KeyCode.SPACE) {
                    rootController.addPlayedCard(Deck.draw());
                } else if(event.getCode() == KeyCode.A) {
                    rootController.getOwnHand().add(Deck.draw());
                } else if (event.getCode() == KeyCode.S) {
                    Deck.shuffle();
                } else if (event.getCode() == KeyCode.NUMPAD0) {
                    rootController.removePlayer(0);
                } else if (event.getCode() == KeyCode.NUMPAD1) {
                    rootController.removePlayer(1);
                } else if (event.getCode() == KeyCode.NUMPAD2) {
                    rootController.removePlayer(2);
                }
            });*/

            int offsetX = 0, offsetY = 0;
            if(scale == .75) {
                offsetX = -173;
                offsetY = -106;
            } else if (scale == .5) {
                offsetX = -346;
                offsetY = -212;
            }

            rootController.getLayout().setScaleX(scale);
            rootController.getLayout().setScaleY(scale);
            rootController.getLayout().setLayoutX(offsetX);
            rootController.getLayout().setLayoutY(offsetY);
            rootLayout.setPrefWidth(WIDTH * scale - 1);
            rootLayout.setPrefHeight(HEIGHT * scale - 1);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.toFront();
            primaryStage.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static RootLayoutController getRootController() {
        return rootController;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }


}
