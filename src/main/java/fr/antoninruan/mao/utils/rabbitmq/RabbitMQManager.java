package fr.antoninruan.mao.utils.rabbitmq;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.Card;
import fr.antoninruan.mao.model.Deck;
import fr.antoninruan.mao.model.Hand;
import fr.antoninruan.mao.model.PlayedStack;
import fr.antoninruan.mao.view.RootLayoutController;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQManager {

    private static final String EXCHANGE_GAME_UPDATES = "game_updates";
    private static final String QUEUE_GAME_ACTION = "game_actions";
    private static final String RPC_QUEUE_CONNECTION = "connection_queue";

    private static Channel channel;
    private static Connection connection;

    public static void init(String host, int port, String user, String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setVirtualHost("card_engine");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.addShutdownListener(e -> {
                if(!e.getMessage().contains("clean channel shutdown")) {
                    e.printStackTrace();
                }
            });
            channel.exchangeDeclare(EXCHANGE_GAME_UPDATES, "fanout");
            channel.queueDeclare(QUEUE_GAME_ACTION, true, false, false, null);

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            MainApp.getPrimaryStage().close();
        }
    }

    public static void stop() {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("type", "player_leave");
            object.addProperty("id", MainApp.getRootController().getOwnHand().getId());
            sendGameAction(object.toString());
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static String connect(String name) {
        try {
            final String corrId = UUID.randomUUID().toString();

            String replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            channel.basicPublish("", RPC_QUEUE_CONNECTION, props, name.getBytes(StandardCharsets.UTF_8));

            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
                }
            }, consumerTag -> {
            });

            String result = response.take();
            channel.basicCancel(ctag);
            return result;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void sendGameAction(String action) {
        try {
            channel.basicPublish("", QUEUE_GAME_ACTION, MessageProperties.PERSISTENT_TEXT_PLAIN, action.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listenGameUpdate() throws IOException {
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, EXCHANGE_GAME_UPDATES, "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                JsonObject update = JsonParser.parseString(new String(delivery.getBody(), StandardCharsets.UTF_8)).getAsJsonObject();
                String type = update.get("type").getAsString();
                System.out.println("Receive: " + update.toString());
                if(type.equals("new_player")) {
//                    System.out.println("broadcast=" + update);
                    String name = update.get("name").getAsString();
                    int id = update.get("id").getAsInt();
                    if(update.get("id").getAsInt() != MainApp.getRootController().getOwnId())
                        Platform.runLater(() -> MainApp.getRootController().addPlayer(name, id));
                } else if(type.equals("player_leave")) {
                    int leaveId = update.get("id").getAsInt();
                    Platform.runLater(() -> MainApp.getRootController().removePlayer(leaveId));
                    Deck.setFromJson(update.get("deck").getAsJsonArray());
                } else if (type.equals("card_move")) {
                    String dest = update.get("destination").getAsString();
                    String from = update.get("source").getAsString();
                    if(from.equals("deck")) {
                        Card card = Deck.draw();
                        if(card != null) {
                            if(dest.equals("playedStack")) {
                                PlayedStack.addCard(card);
                            } else {
                                int destId = Integer.parseInt(dest);
                                Hand hand = MainApp.getRootController().getHand(destId);
                                hand.add(card);
                            }
                        }
                    } else if (from.equals("playedStack")) {
                        Card card = PlayedStack.pickLastCard();
                        if(card != null) {
                            if(dest.equals("deck")) {
                                Deck.put(card);
                            } else {
                                int destId = Integer.parseInt(dest);
                                Hand hand = MainApp.getRootController().getHand(destId);
                                hand.add(card);
                            }
                        }
                    } else {
                        Hand hand = MainApp.getRootController().getHand(Integer.parseInt(from));
                        Card card = hand.getCard(update.get("card_id").getAsInt());
                        if(dest.equals("deck")) {
                            Deck.put(card);
                            hand.remove(card);
                        } else if (dest.equals("playedStack")) {
                            PlayedStack.addCard(card);
                            hand.remove(card);
                        } else {
                            int destId = Integer.parseInt(dest);
                            Hand target = MainApp.getRootController().getHand(destId);
                            target.add(card);
                            hand.remove(card);
                        }
                    }
                } else if (type.equals("shuffle")) {
                    Deck.setFromJson(update.get("deck").getAsJsonArray());
                    Platform.runLater(() -> new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/shuffle.mp3").toString())).play());
                } else if(type.equals("rollback")) {
                    Deck.setFromJson(update.get("deck").getAsJsonArray());
                    PlayedStack.getCards().clear();
                } else if(type.equals("knock")) {
                    Platform.runLater(() -> new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/knock.mp3").toString())).play());
                } else if(type.equals("rub")) {
                    Platform.runLater(() -> new MediaPlayer(new Media(MainApp.class.getClassLoader().getResource("sound/rub.mp3").toString())).play());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
    }


}
