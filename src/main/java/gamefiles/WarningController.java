package gamefiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class WarningController {
        @FXML
        private Label InfoAboutError;

        @FXML
        private Label Message;

        @FXML
        private AnchorPane messageBox;

        @FXML
        private Label messageLabel;

        @FXML
        private ImageView warningImage;

        public void setInfoAboutError(String info) {
                InfoAboutError.setText(info);
        }
}
