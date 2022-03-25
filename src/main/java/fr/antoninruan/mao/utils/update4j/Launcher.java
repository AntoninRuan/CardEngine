package fr.antoninruan.mao.utils.update4j;

import fr.antoninruan.mao.MainApp;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.update4j.LaunchContext;
import org.update4j.inject.InjectTarget;

import java.io.IOException;

public class Launcher implements org.update4j.service.Launcher {

    @InjectTarget
    private Stage primaryStage;

    public static void main(String[] args) {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.initOwner(null);
                stage.initModality(Modality.NONE);
                MainApp mainApp = new MainApp();
                mainApp.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void run(LaunchContext context) {
        Platform.runLater(() -> {
            MainApp mainApp = new MainApp();
            try {
                mainApp.start(primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public long version() {
        return 0;
    }

}
