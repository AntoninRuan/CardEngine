package fr.antoninruan.mao.utils.rabbitmq;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.Card;
import fr.antoninruan.mao.model.cardcontainer.Hand;
import fr.antoninruan.mao.utils.DialogUtils;
import javafx.application.Platform;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQManager {

    private static final String EXCHANGE_GAME_UPDATES = "game_updates";
    private static final String QUEUE_GAME_ACTION = "game_actions";
    private static final String EXCHANGE_CHAT_UPDATE = "chat_update";
    private static final String QUEUE_MESSAGE_SENDING = "message_sending";
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
                if (!e.getMessage().contains("clean channel shutdown"))
                    e.printStackTrace();
            });
            channel.exchangeDeclare(EXCHANGE_GAME_UPDATES, "fanout");
            channel.queueDeclare(QUEUE_GAME_ACTION, true, false, false, null);
            channel.exchangeDeclare(EXCHANGE_CHAT_UPDATE, "fanout");

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

    public static void sendChatMessage(String user, String message) {
        JsonObject object = new JsonObject();
        object.addProperty("user", user);
        object.addProperty("content", message);
        try {
            channel.basicPublish("", QUEUE_MESSAGE_SENDING, MessageProperties.PERSISTENT_TEXT_PLAIN, object.toString().getBytes(StandardCharsets.UTF_8));
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
                switch (type) {
                    case "new_player":
                        String name = update.get("name").getAsString();
                        int id = update.get("id").getAsInt();
                        if (update.get("id").getAsInt() != MainApp.getRootController().getOwnId())
                            Platform.runLater(() -> MainApp.getRootController().addPlayer(name, id));
                        break;
                    case "player_leave":
                        int leaveId = update.get("id").getAsInt();
                        if (leaveId == MainApp.getRootController().getOwnId()) {
                            Platform.runLater(() -> {
                                DialogUtils.showKickDialog();
                                System.exit(0);
                            });
                        } else {
                            Platform.runLater(() -> MainApp.getRootController().removePlayer(leaveId));
                            MainApp.getDeck().setFromJson(update.get("deck").getAsJsonArray());
                        }
                        break;
                    case "card_move":
                        String dest = update.get("destination").getAsString();
                        String from = update.get("source").getAsString();
                        JsonObject cardJson = update.get("moved_card").getAsJsonObject();
                        Card.Suit suit = Card.Suit.valueOf(cardJson.get("suit").getAsString());
                        Card.Value value = Card.Value.valueOf(cardJson.get("value").getAsString());
                        Card card = Card.getCard(suit, value);
                        if (from.equals("deck")) {
//                            Card card = MainApp.getDeck().getLastCard();
                            if (card != null) {
                                if (dest.equals("playedStack")) {
                                    MainApp.getDeck().moveCardTo(card, MainApp.getPlayedStack());
                                } else {
                                    int destId = Integer.parseInt(dest);
                                    Hand hand = MainApp.getRootController().getHand(destId);
                                    MainApp.getDeck().moveCardTo(card, hand);
                                }
                            }
                        } else if (from.equals("playedStack")) {
//                            Card card = MainApp.getPlayedStack().getLastCard();
                            if (card != null) {
                                if (dest.equals("deck")) {
                                    MainApp.getPlayedStack().moveCardTo(card, MainApp.getDeck());
                                } else {
                                    int destId = Integer.parseInt(dest);
                                    Hand hand = MainApp.getRootController().getHand(destId);
                                    MainApp.getPlayedStack().moveCardTo(card, hand);
                                }
                            }
                        } else {
                            Hand hand = MainApp.getRootController().getHand(Integer.parseInt(from));
//                            Card card = hand.getCard(update.get("card_id").getAsInt());
                            if (dest.equals("deck")) {
                                hand.moveCardTo(card, MainApp.getDeck());
                            } else if (dest.equals("playedStack")) {
                                hand.moveCardTo(card, MainApp.getPlayedStack());
                            } else {
                                int destId = Integer.parseInt(dest);
                                Hand target = MainApp.getRootController().getHand(destId);
                                hand.moveCardTo(card, target);
                            }
                        }
                        Platform.runLater(() -> {
                            MainApp.CARD_MOVE_SOUND.seek(Duration.ZERO);
                            MainApp.CARD_MOVE_SOUND.play();
                        });
                        break;
                    case "shuffle":
                        MainApp.getDeck().setFromJson(update.get("deck").getAsJsonArray());
                        Platform.runLater(() -> {
                            MainApp.SHUFFLE_SOUND.seek(Duration.ZERO);
                            MainApp.SHUFFLE_SOUND.play();
                        });
                        break;
                    case "rollback":
                        MainApp.getDeck().setFromJson(update.get("deck").getAsJsonArray());
                        MainApp.getPlayedStack().clear();
                        break;
                    case "knock":
                        Platform.runLater(() -> {
                            MainApp.KNOCK_SOUND.seek(Duration.ZERO);
                            MainApp.KNOCK_SOUND.play();
                        });
                        break;
                    case "rub":
                        Platform.runLater(() -> {
                            MainApp.RUB_SOUND.seek(Duration.ZERO);
                            MainApp.RUB_SOUND.play();
                        });
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
        });
    }

    public static void listenChatUpdate() throws IOException {
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, EXCHANGE_CHAT_UPDATE, "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                JsonObject object = JsonParser.parseString(new String(delivery.getBody(), StandardCharsets.UTF_8)).getAsJsonObject();
                if (MainApp.getChatController() != null)
                    Platform.runLater(() -> MainApp.getChatController().addMessage(object.get("user").getAsString(), object.get("content").getAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
        });
    }

}
