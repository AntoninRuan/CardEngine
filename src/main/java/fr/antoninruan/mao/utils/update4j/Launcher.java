package fr.antoninruan.mao.utils.update4j;

import fr.antoninruan.mao.MainApp;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.update4j.LaunchContext;
import org.update4j.inject.InjectTarget;

import java.io.IOException;
import java.util.Arrays;

public class Launcher implements org.update4j.service.Launcher {

    @InjectTarget
    private Stage primaryStage;

    public static void main(String[] args) throws IOException {
        System.out.println("Main: " + Arrays.toString(args));
        Platform.runLater(() -> {
            System.out.println("Run later");
            try {
                Stage stage = new Stage();
                stage.initOwner(null);
                stage.initModality(Modality.NONE);
                System.out.println("Launching");
                MainApp mainApp = new MainApp();
                mainApp.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void run(LaunchContext context) {
        System.out.println("Launching via launcher");
        System.out.println("Primary stage: " + primaryStage);
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
