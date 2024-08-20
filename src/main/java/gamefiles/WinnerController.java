package gamefiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class WinnerController {
    @FXML
    private Label CongratsLabel;
    @FXML
    private Pane FrontPane;
    @FXML
    private AnchorPane WinnerCanvas;
    @FXML
    private Label WinnerNameLabel;
    @FXML
    private ImageView trophyImage;

    public void setNameOfWinner(String info) {
        WinnerNameLabel.setText(WinnerNameLabel.getText() + " " + info);
    }
}
