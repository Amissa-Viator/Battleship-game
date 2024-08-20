package gamefiles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Registration {
    @FXML
    private TextField NicknameText;
    @FXML
    private AnchorPane RegisterForm;
    @FXML
    private Button StartGameBtn;
    @FXML
    private Label WelcomeLabel;
    private String nameOfPlayer;
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    public void initialize() {
        StartGameBtn.setOnAction(event-> {
            nameOfPlayer = NicknameText.getText();
            if(!nameOfPlayer.isEmpty()) {
                loginPlayer();
                switchScenesToTheNextOne();
            }
            else {
               showWarning();
            }
        });
    }

    private void loginPlayer() {
        Main.player.setName(nameOfPlayer);
        String insertPlayer = "INSERT INTO player (name, enemyid) VALUES (?,?)";
        try {
            PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertPlayer, Statement.RETURN_GENERATED_KEYS);
            insertState.setString(1, nameOfPlayer);
            insertState.setInt(2, 0);
            insertState.executeUpdate();
            var generatedKeys = insertState.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt("id");
                Main.player.setId(generatedId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void showWarning() {
        try {
            Stage warning = new Stage();
            warning.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("warning.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            warning.setScene(scene);
            warning.setTitle("Warning");
            WarningController controller = fxmlLoader.getController();
            controller.setInfoAboutError("You don't enter the name");
            warning.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchScenesToTheNextOne() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gamefield.fxml"));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Battleship");
            ArrangementController controller = fxmlLoader.getController();
            controller.setStage(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNameOfPlayer() {
        return nameOfPlayer;
    }
}