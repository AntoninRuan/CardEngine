package fr.antoninruan.mao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.antoninruan.mao.model.ConnectionInfo;
import fr.antoninruan.mao.model.card.Card;
import fr.antoninruan.mao.model.card.cardcontainer.Deck;
import fr.antoninruan.mao.model.card.cardcontainer.Hand;
import fr.antoninruan.mao.model.card.cardcontainer.PlayedStack;
import fr.antoninruan.mao.model.chat.Emote;
import fr.antoninruan.mao.utils.DialogUtils;
import fr.antoninruan.mao.utils.rabbitmq.RabbitMQManager;
import fr.antoninruan.mao.view.ChatLayoutController;
import fr.antoninruan.mao.view.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.math3.util.Precision;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class MainApp extends Application {

    // TODO trier ses cartes
    // TODO ajoute réglage volume sonore

    private static final double HEIGHT = 850;
    private static final double WIDTH = 1383;

    public static final MediaPlayer CARD_MOVE_SOUND =
            new MediaPlayer(new Media(Objects.requireNonNull(MainApp.class.getClassLoader().getResource("sound/card_move.mp3")).toString()));
    public static final MediaPlayer SHUFFLE_SOUND =
            new MediaPlayer(new Media(Objects.requireNonNull(MainApp.class.getClassLoader().getResource("sound/shuffle.mp3")).toString()));
    public static final MediaPlayer KNOCK_SOUND =
            new MediaPlayer(new Media(Objects.requireNonNull(MainApp.class.getClassLoader().getResource("sound/knock.mp3")).toString()));
    public static final MediaPlayer RUB_SOUND =
            new MediaPlayer(new Media(Objects.requireNonNull(MainApp.class.getClassLoader().getResource("sound/rub.mp3")).toString()));
    private static final File rootFolder = new File("");

    private static RootLayoutController rootController;
    private static ChatLayoutController chatController;
    private static final Deck deck = new Deck();
    private static final PlayedStack playedStack = new PlayedStack();
    private static String username;
    private static String hostAdress;

    private static Stage chatStage;
    private static boolean chatOpen = false;

    private static Stage primaryStage;
    public static final Image ICON = new Image(Objects.requireNonNull(MainApp.class.getClassLoader().getResource("icon.png")).toString());

    @Override
    public void start(Stage stage) throws IOException {
        MainApp.primaryStage = stage;
        MainApp.primaryStage.setTitle("Cartes");
        MainApp.primaryStage.getIcons().add(ICON);

        primaryStage.setOnCloseRequest(windowEvent -> {
            RabbitMQManager.stop();
            Platform.exit();
        });

        int preselectScale = preselectScale();

        Optional<ConnectionInfo> connectionInfo = DialogUtils.connect(preselectScale);
        if (connectionInfo.isPresent()) {
            ConnectionInfo info = connectionInfo.get();
            initRootLayout(Precision.round(info.getScale(), 2));
            hostAdress = info.getHost();
            RabbitMQManager.init(info.getHost(), 5672, "card_engine", "pgN4KRTrc74");
            JsonObject response = JsonParser.parseString(RabbitMQManager.connect(info.getName())).getAsJsonObject();
            username = info.getName();
            rootController.setOwnId(response.get("id").getAsInt());
            for (JsonElement element : response.get("players").getAsJsonArray()) {
                JsonObject p = element.getAsJsonObject();
                Hand h = rootController.addPlayer(p.get("name").getAsString(), p.get("id").getAsInt());
                for(JsonElement card : p.get("cards").getAsJsonArray()) {
                    String suit = card.getAsJsonObject().get("suit").getAsString();
                    String value = card.getAsJsonObject().get("value").getAsString();
                    h.add(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
                }
            }
            MainApp.getDeck().setFromJson(response.get("deck").getAsJsonArray());
            playedStack.getCards().clear();
            for(JsonElement element : response.get("played_stack").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                String suit = object.get("suit").getAsString();
                String value = object.get("value").getAsString();
                playedStack.add(Card.getCard(Card.Suit.valueOf(suit), Card.Value.valueOf(value)));
            }
            loadEmotes(response.get("emotes").getAsJsonArray());
            primaryStage.setTitle(info.getHost() + " - " + username);
            RabbitMQManager.listenGameUpdate();
            RabbitMQManager.listenChatUpdate();
        } else {
            primaryStage.close();
        }

    }

    private int preselectScale() {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        int defaultScale = 75;
        if(screen.getWidth() >= 1400 && screen.getHeight() >= 900)
            defaultScale = 100;
        else if (screen.getWidth() < 975 || screen.getHeight() < 600)
            defaultScale = 50;

        return defaultScale;
    }

    private void initRootLayout(double scale) {

//        Deck.init();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/RootLayout.fxml"));

            Pane rootLayout = loader.load();

            rootController = loader.getController();

            Scene scene = new Scene(rootLayout);

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

    private void loadEmotes(JsonArray array) {
        Pair<ProgressBar, Label> pair = DialogUtils.loadingInfo();

        Thread thread = new Thread(() -> {
            File emoteFolder = new File(rootFolder.getAbsolutePath() + "/emotes/");
            if (!emoteFolder.exists())
                emoteFolder.mkdir();

            int size = array.size();
            int current = 1;
            for (JsonElement element : array) {
                JsonObject emote = element.getAsJsonObject();
                String name = emote.get("name").getAsString();
                File f = new File(emoteFolder.getAbsolutePath() + "/" + name + ".png");
                int finalCurrent = current;
                Platform.runLater(() -> {
                    pair.getValue().setText("Loading " + f.getAbsolutePath());
                    pair.getKey().setProgress((double) finalCurrent / (double) size);
                });
                if (!f.exists()) {
                    downloadEmote(name);
                }
                Image image = new Image(f.toURI().toString());
                Emote.getEmotes().put(name, new Emote(name, image));
                current++;
            }
        }, "emote_load");

        thread.start();
    }

    private void downloadEmote(String name) {
        try {
            System.out.println("Downloading " + name);
            URL url = new URL("http://" + hostAdress + ":5673/emotes/" + name + ".png");
            InputStream in = new BufferedInputStream(url.openStream());
            File file = new File(rootFolder.getAbsolutePath() + "/emotes/" + name + ".png");
            System.out.println(file.getAbsolutePath());
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void toggleChat() {
        if (chatOpen) {
            chatStage.close();
            chatStage = null;
            chatOpen = false;
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/ChatLayout.fxml"));

            AnchorPane pane = loader.load();

            chatController = loader.getController();

            Scene scene = new Scene(pane);
            chatStage = new Stage();
            chatStage.setScene(scene);
            chatStage.initModality(Modality.NONE);
            chatStage.initOwner(primaryStage);
            chatStage.setX(primaryStage.getX() + primaryStage.getWidth() + 30);
            chatStage.setY(primaryStage.getY());
            chatStage.setTitle("Chat");
            chatStage.setOnCloseRequest(windowEvent -> {
                chatOpen = false;
                chatStage.close();
                windowEvent.consume();
            });
            chatOpen = true;
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsername() {
        return username;
    }

    public static RootLayoutController getRootController() {
        return rootController;
    }

    public static ChatLayoutController getChatController() {
        return chatController;
    }

    public static Deck getDeck() {
        return deck;
    }

    public static PlayedStack getPlayedStack() {
        return playedStack;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }


}
