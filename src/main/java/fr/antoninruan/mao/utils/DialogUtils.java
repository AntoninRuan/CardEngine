package fr.antoninruan.mao.utils;

import fr.antoninruan.mao.MainApp;
import fr.antoninruan.mao.model.ConnectionInfo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Optional;

public class DialogUtils {

    public static Optional<ConnectionInfo> connect() {
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
        scale.getSelectionModel().select((Integer) 75);

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

}
