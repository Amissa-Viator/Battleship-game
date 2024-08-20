package gamefiles;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MessageController {
    @FXML
    private Button closeWindowsOfThat;

    @FXML
    private AnchorPane messageAboutEnemy;

    @FXML
    private Label noEnemyLabel;

    @FXML
    private ImageView smile;

    public Button getCloseWindowsOfThat() {
        return closeWindowsOfThat;
    }

}
