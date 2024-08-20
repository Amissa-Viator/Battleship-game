package gamefiles;

import gamefiles.database.entity.Player;
import gamefiles.database.util.ConnectionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static final Connection CONNECTION = ConnectionManager.get();
    public static final Player player = new Player();
    public static final Player enemy = new Player();

    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("progress.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Battleship");
            stage.setScene(scene);
            stage.show();
            ProgressController controller = fxmlLoader.getController();
            controller.setStage(stage);
            controller.initialize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        List<String> tables = new ArrayList<>();
        tables.add("warcraft_locations");
        tables.add("locations");
        tables.add("warcraft");
        tables.add("player");
        for(String table: tables) {
            try {
                String resetId = "ALTER SEQUENCE " + table + "_id_seq RESTART WITH 1";
                PreparedStatement resetState = CONNECTION.prepareStatement(resetId);
                resetState.executeUpdate();

                String clearTable = "DELETE FROM " + table;
                PreparedStatement clearState = CONNECTION.prepareStatement(clearTable);
                clearState.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}