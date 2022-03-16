package fr.antoninruan.mao.utils;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.ConnectionInfo;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    private static Hyperlink createHyperLink(String text, String link, Dialog alert) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setPadding(new Insets(0));
        hyperlink.setOnAction(event -> {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
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
