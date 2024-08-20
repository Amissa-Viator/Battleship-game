package gamefiles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgressController {
        @FXML
        private Label LoadLabel;
        @FXML
        private ImageView ProgressBackground;
        @FXML
        private ProgressBar myProgressBar;

        private Stage stage;
        public void setStage(Stage stage) {
            this.stage = stage;
        }
        public void initialize() {
            myProgressBar.setStyle("-fx-accent:#1B1212;");
            fillProgressBar();
        }
        public void fillProgressBar() {
            double finalProgress = 1.0;
            int frameCount = 6;
            Duration duration = Duration.seconds(2);
            double frameInterval = duration.toMillis() / frameCount;
            Timeline timeline = new Timeline();
            for (int i = 0; i < frameCount; i++) {
                double frameProgress = (i + 1.0) / frameCount;
                KeyFrame keyFrame = new KeyFrame(
                        Duration.millis(i * frameInterval),
                        event -> myProgressBar.setProgress(frameProgress)
                );
                timeline.getKeyFrames().add(keyFrame);
            }

            timeline.setOnFinished(event -> {
                myProgressBar.setProgress(finalProgress);
                switchScenes();
            });
            timeline.play();
        }
        public void switchScenes() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("starter.fxml"));
                Parent root = fxmlLoader.load();
                stage.setScene(new Scene(root));
                stage.setTitle("Battleship");
                Registration controller = fxmlLoader.getController();
                controller.setStage(stage);
                controller.initialize();
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
