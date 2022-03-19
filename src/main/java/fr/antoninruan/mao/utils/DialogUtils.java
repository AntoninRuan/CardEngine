package fr.antoninruan.mao.utils;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.ConnectionInfo;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class DialogUtils {

    public static Optional<ConnectionInfo> connect(int preselectScale) {
        Dialog<ConnectionInfo> dialog = new Dialog<>();

        dialog.setTitle("Connexion");
        dialog.setHeaderText("Connectez vous à un serveur de jeu");
        dialog.initModality(Modality.APPLICATION_MODAL);

        final ButtonType validationButtonType = new ButtonType("Connexion", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(validationButtonType, closeButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField host = new TextField();
        host.setPromptText("Serveur");

        TextField username = new TextField();
        username.setPromptText("Pseudo");

        ChoiceBox<Integer> scale = new ChoiceBox<>();
        scale.getItems().setAll(100, 75, 50);
        scale.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer integer) {
                return integer + " %";
            }

            @Override
            public Integer fromString(String s) {
                return Integer.parseInt(s.split(" ")[0]);
            }
        });
        scale.getSelectionModel().select((Integer) preselectScale);

        gridPane.add(new Label("Serveur"), 0, 0);
        gridPane.add(host, 1, 0);
        gridPane.add(new Label("Pseudo"), 0, 1);
        gridPane.add(username, 1, 1);
        gridPane.add(new Label("Échelle"), 0, 2);
        gridPane.add(scale, 1, 2);

        dialog.getDialogPane().setContent(gridPane);
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(MainApp.ICON);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == validationButtonType) {
                return new ConnectionInfo(host.getText(), username.getText(), ((double) scale.getSelectionModel().getSelectedItem()/ 100.));
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static void showKickDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vous avez été kick");
        alert.setHeaderText("Vous avez été kick (sad pour vous)");
        alert.setContentText(null);

        VBox vBox = new VBox();
        vBox.setSpacing(3);
//        vBox.setPrefWidth(424.0);

        Hyperlink changelog = createHyperLink("Voir les raisons de mon kick",
                "https://antonin-ruan.fr/kick_reason.html", alert);

        vBox.getChildren().add(changelog);

        alert.getDialogPane().setContent(vBox);

        alert.showAndWait();
    }

    public static Pair<ProgressBar, Label> loadingInfo() {

        Stage stage = new Stage();
        stage.setTitle("Chargement des emotes");
        stage.initOwner(MainApp.getPrimaryStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(MainApp.ICON);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setResizable(false);
        stage.setOnCloseRequest(Event::consume);

        VBox vBox = new VBox();
        vBox.setSpacing(5d);
        vBox.setPrefSize(375, 50);
        vBox.setPadding(new Insets(5));
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);

        vBox.getStyleClass().add("vbox");
        vBox.getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/download-progress.css").toString());

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(Double.MAX_VALUE);

        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() >= 1) {
                stage.close();
            }
        });

        Label label = new Label("Début du chargement des emotes");
        vBox.getChildren().addAll(progressBar, label);

        // Set the max status
        int maxStatus = 12;
        // Create the Property that holds the current status count
        IntegerProperty statusCountProperty = new SimpleIntegerProperty(1);
        // Create the timeline that loops the statusCount till the maxStatus
        Timeline timelineBar = new Timeline(
                new KeyFrame(
                        // Set this value for the speed of the animation
                        Duration.millis(1000),
                        new KeyValue(statusCountProperty, maxStatus)
                )
        );
        // The animation should be infinite
        timelineBar.setCycleCount(Timeline.INDEFINITE);
        timelineBar.play();
        // Add a listener to the statusproperty
        statusCountProperty.addListener((ov, statusOld, statusNewNumber) -> {
            int statusNew = statusNewNumber.intValue();
            // Remove old status pseudo from progress-bar
            progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + statusOld.intValue()), false);
            // Add current status pseudo from progress-bar
            progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + statusNew), true);
        });

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();

        return new Pair<>(progressBar, label);
    }

    private static Hyperlink createHyperLink(String text, String link, Dialog alert) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setPadding(new Insets(0));
        hyperlink.setOnAction(event -> {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                Thread thread = new Thread(() -> {
                    try {
                        Desktop.getDesktop().browse(new URL(link).toURI());
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            } else {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(link), null);
                Tooltip tooltip = new Tooltip("Le lien a bien été copié");
                tooltip.show(alert.getDialogPane().getScene().getWindow(),
                        MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y - 30);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(tooltip::hide);
                        timer.cancel();
                    }
                }, 750);
            }
            hyperlink.setVisited(false);
        });
        return hyperlink;
    }

}
