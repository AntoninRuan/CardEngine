package fr.antoninruan.mao;

import fr.antoninruan.mao.model.Deck;
import fr.antoninruan.mao.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private static final int HEIGHT = 850;
    private static final int WIDTH = 1383;

    private static Pane rootLayout;
    private static RootLayoutController rootController;

    private static Stage primaryStage;
    private static final Image ICON = new Image(MainApp.class.getClassLoader().getResource("icon.png").toString());

    @Override
    public void start(Stage stage) {
        MainApp.primaryStage = stage;
        MainApp.primaryStage.setTitle("Cartes");
        MainApp.primaryStage.getIcons().add(ICON);

        initRootLayout();
    }

    private void initRootLayout() {

        Deck.init();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("fxml/RootLayout.fxml"));

            rootLayout = loader.load();

            rootController = loader.getController();

            Scene scene = new Scene(rootLayout);
            scene.setOnKeyPressed(event -> {

                if(event.getCode() == KeyCode.SPACE) {
                    rootController.addPlayedCard(Deck.draw());
                } else if(event.getCode() == KeyCode.A) {
                    rootController.getOwnHand().add(Deck.draw());
                } else if (event.getCode() == KeyCode.Z) {
                    rootController.addPlayer("");
                } else if (event.getCode() == KeyCode.S) {
                    Deck.shuffle();
                } else if (event.getCode() == KeyCode.NUMPAD0) {
                    rootController.removePlayer(0);
                } else if (event.getCode() == KeyCode.NUMPAD1) {
                    rootController.removePlayer(1);
                } else if (event.getCode() == KeyCode.NUMPAD2) {
                    rootController.removePlayer(2);
                }
            });
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Pane getRootLayout() {
        return rootLayout;
    }

    public static RootLayoutController getRootController() {
        return rootController;
    }
}
