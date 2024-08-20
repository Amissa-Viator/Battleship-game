package gamefiles;

import gamefiles.database.entity.ShipLocations;
import gamefiles.database.entity.WarCraft;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BattleshipController {
    @FXML
    private GridPane enemyField;

    @FXML
    private Label enemyFieldLabel;

    @FXML
    private AnchorPane fields;

    @FXML
    private GridPane yourField;

    @FXML
    private Label yourFieldLabel;
    private Rectangle battleship;
    private static final boolean HIT = true;
    private int numberOfLostShips = 0;
    private int numberOfEnemyLostShips = 0;

    public void showWinner(String info) {
        try {
            Stage winner = new Stage();
            winner.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("winner.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            winner.setScene(scene);
            winner.setTitle("Winner");
            WinnerController controller = fxmlLoader.getController();
            controller.setNameOfWinner(info);
            winner.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillFieldWithShips(ArrayList<Ship> listOfShips) {
        int columnIndex, rowIndex, numberOfDecks;
        boolean isRotated;
        for(Ship ship: listOfShips) {
            columnIndex = ship.getNumberOfColumn();
            rowIndex = ship.getNumberOfRow();
            numberOfDecks = ship.getNumberOfDecks();
            isRotated = ship.isRotationOfShip();
            if(isRotated) {
                battleship = new Rectangle(30, 30 * numberOfDecks);
                battleship.setFill(Color.rgb(0, 0, 139));
                yourField.add(battleship, columnIndex, rowIndex, 1, numberOfDecks);
            } else {
                battleship = new Rectangle(30 * numberOfDecks, 30);
                battleship.setFill(Color.rgb(0, 0, 139));
                yourField.add(battleship, columnIndex, rowIndex, numberOfDecks, 1);
            }
            battleship = null;
        }
    }

    private void MissTheShip(int column, int row) {
        Rectangle missAttack = new Rectangle(30, 30);
        missAttack.setFill(Color.rgb(250, 249, 246));
        enemyField.add(missAttack, column, row);
    }

  private void GotIntoTheShip(int column, int row) {
      Image explode = new Image(getClass().getResourceAsStream("/image/explode.png"));
      ImageView explosion = new ImageView(explode);
      explosion.setFitWidth(30);
      explosion.setFitHeight(30);
      enemyField.add(explosion, column, row);

      Timeline timeline = new Timeline(
              new KeyFrame(Duration.seconds(0.5), e -> {
                  Image hit = new Image(getClass().getResourceAsStream("/image/hit.png"));
                  ImageView gotIntoShip = new ImageView(hit);
                  gotIntoShip.setFitWidth(30);
                  gotIntoShip.setFitHeight(30);
                  enemyField.getChildren().remove(explosion);
                  enemyField.add(gotIntoShip, column, row);

                  for (WarCraft warCraft : Main.enemy.getBattleWarCrafts()) {
                      if (isShipTotallyDestroyed(warCraft)) {
                          TheShipIsDestroyed();
                      }
                  }
              })
      );
      timeline.play();
  }

    private void TheShipIsDestroyed() {
        List<Node> nodesToRemove = new ArrayList<>();
        for (Node node : enemyField.getChildren()) {
            if (node instanceof ImageView) {
                nodesToRemove.add(node);
            }
        }
        for (Node node : nodesToRemove) {
            Rectangle destroyed = new Rectangle(30, 30);
            destroyed.setFill(Color.rgb(220, 20, 60));
            enemyField.getChildren().remove(node);  // Видаляємо один елемент
            enemyField.add(destroyed, GridPane.getColumnIndex(node), GridPane.getRowIndex(node));  // Додаємо прямокутник на місце видаленого елемента
        }
    }

    private void DestroyTheShip(int column, int row) {
        Rectangle attack = new Rectangle(30, 30);
        attack.setFill(Color.rgb(220, 20, 60));
        enemyField.add(attack, column, row);
    }

    private void GetInfoAboutShipsThatAreLost() {
        int yourId = Main.player.getId();
        ArrayList<Integer> IdWarCraft = new ArrayList<>();
        ArrayList<Integer> IdLocations = new ArrayList<>();
        String idWarcrafts = "SELECT id FROM warcraft WHERE player_id = ?";
        String idLocations = "SELECT locations_id FROM warcraft_locations WHERE warcraft_id=?";
        String state = "SELECT COUNT(*) AS count FROM locations WHERE id=? AND loss_battle=?"; //поверне кількість рядків

        try {
            PreparedStatement selectIdWarcraft = Main.CONNECTION.prepareStatement(idWarcrafts);
            selectIdWarcraft.setInt(1, yourId);
            var resultOfFirstSelection = selectIdWarcraft.executeQuery();
            while(resultOfFirstSelection.next()) {
                int id = resultOfFirstSelection.getInt("id");
                IdWarCraft.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            for(int ID: IdWarCraft) {
                PreparedStatement selectIdLocation = Main.CONNECTION.prepareStatement(idLocations);
                selectIdLocation.setInt(1, ID);
                var resultOfSecondSelection = selectIdLocation.executeQuery();
                while (resultOfSecondSelection.next()) {
                    int id = resultOfSecondSelection.getInt("locations_id");
                    IdLocations.add(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            int count = 0;
            for(int id: IdLocations) {
                PreparedStatement selectState = Main.CONNECTION.prepareStatement(state);
                selectState.setInt(1, id);
                selectState.setBoolean(2, true);
                var resultOfThirdSelection = selectState.executeQuery();
                if (resultOfThirdSelection.next()) {
                    count = resultOfThirdSelection.getInt("count");
                }
            }
            if(count > 0) {
                numberOfLostShips++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        IdWarCraft.clear();
        IdLocations.clear();
    }

    private void SendInfoAboutEnemyShip(int idOfEnemy, WarCraft warcraft) {
        int numberOfDecks = warcraft.getClassOfShip();
        ArrayList<Integer> IdWarCraft = new ArrayList<>();
        ArrayList<Integer> IdLocations = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        String updateInfo = "UPDATE locations SET loss_battle=? WHERE id=?";
        String idWarcrafts = "SELECT id FROM warcraft WHERE player_id = ? AND numberofdecks= ?";
        String idLocations = "SELECT locations_id FROM warcraft_locations WHERE warcraft_id=?";
        String selectValues = "SELECT numberofcolumn, numberofrow FROM locations WHERE id=?";

        try {
            PreparedStatement selectIdWarcraft = Main.CONNECTION.prepareStatement(idWarcrafts);
            selectIdWarcraft.setInt(1, idOfEnemy);
            selectIdWarcraft.setInt(2, numberOfDecks);
            var resultOfFirstSelection = selectIdWarcraft.executeQuery();
            while(resultOfFirstSelection.next()) {
                int id = resultOfFirstSelection.getInt("id");
                IdWarCraft.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            for(int ID: IdWarCraft) {
                PreparedStatement selectIdLocation = Main.CONNECTION.prepareStatement(idLocations);
                selectIdLocation.setInt(1, ID);
                var resultOfSecondSelection = selectIdLocation.executeQuery();
                while (resultOfSecondSelection.next()) {
                    int id = resultOfSecondSelection.getInt("locations_id");
                    IdLocations.add(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            for(int id: IdLocations) {
                PreparedStatement selectIdValues = Main.CONNECTION.prepareStatement(selectValues);
                selectIdValues.setInt(1, id);
                var resultOfThirdSelection = selectIdValues.executeQuery();
                while (resultOfThirdSelection.next()) {
                    int column = resultOfThirdSelection.getInt("numberofcolumn");
                    int row = resultOfThirdSelection.getInt("numberofrow");
                    for(ShipLocations ship: warcraft.getShipLocations()) {
                        if(column == ship.getNumberOfColumn() && row == ship.getNumberOfRow()) {
                            values.add(id);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try{
            for(int i=0; i<values.size(); i++) {
                PreparedStatement updateState = Main.CONNECTION.prepareStatement(updateInfo);
                int id = values.get(i);
                updateState.setBoolean(1, true);
                updateState.setInt(2, id);
                updateState.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        IdWarCraft.clear();
        values.clear();
        IdLocations.clear();
    }

    private void checkWinner() {
       if(numberOfEnemyLostShips == 10) {
           showWinner(Main.player.getName());
       } else if (numberOfLostShips == 10){
           showWinner(Main.enemy.getName());
       }
    }

    void onMouseClickedOnGridPane(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        int column = (int) (mouseX / 30);
        int row = (int) (mouseY / 30);
        boolean isfound = false;
        for(WarCraft warCraft: Main.enemy.getBattleWarCrafts()) {
            for(ShipLocations ship: warCraft.getShipLocations()) {
                int numCol = ship.getNumberOfColumn();
                int numRow = ship.getNumberOfRow();
                if(numRow==row && numCol==column) {
                    isfound=true;
                    if(!ship.getLoss_battle()) {
                        ship.setLoss_battle(HIT);
                        if(warCraft.getClassOfShip() == 1) {
                            DestroyTheShip(column, row);
                            numberOfEnemyLostShips++;
                        } else {
                            GotIntoTheShip(column, row);
                            if(isShipTotallyDestroyed(warCraft)) {
                                numberOfEnemyLostShips++;
                            }
                        }
                    }
                }
            }
        }
        if(!isfound) { // якщо не попав
            MissTheShip(column, row);
        }
        for (WarCraft warCraft : Main.enemy.getBattleWarCrafts()) {
            if (isShipTotallyDestroyed(warCraft)) {
                SendInfoAboutEnemyShip(Main.player.getEnemy_id(), warCraft);
            }
        }
        GetInfoAboutShipsThatAreLost();
        checkWinner();
    }
    private boolean isShipTotallyDestroyed(WarCraft warCraft) {
        for(ShipLocations ship: warCraft.getShipLocations()) {
            if(!ship.getLoss_battle()) {
                return false;
            }
        }
        return true;
    }

    private void ReadInformationAboutEnemy() {
        int EnemyId = Main.player.getEnemy_id();
        String selectWarcraft = "SELECT id, numberofdecks FROM warcraft WHERE player_id = " + EnemyId;
        try {
            PreparedStatement selectState = Main.CONNECTION.prepareStatement(selectWarcraft);
            var enemyResult = selectState.executeQuery();
            while(enemyResult.next()) {
                WarCraft warcraft = new WarCraft();
                int idWarcraft = enemyResult.getInt("id");
                int decks = enemyResult.getInt("numberofdecks");
                warcraft.setClassOfShip(decks);
                String selectLocationsId = "SELECT locations_id FROM warcraft_locations WHERE warcraft_id="+idWarcraft;
                try(PreparedStatement selectLocation = Main.CONNECTION.prepareStatement(selectLocationsId)) {
                    var result = selectLocation.executeQuery();
                    while(result.next()) {
                        int idForLocation = result.getInt("locations_id");
                        String shipInfoselect = "SELECT numberofcolumn, numberofrow,loss_battle FROM locations WHERE id="+idForLocation;
                        try(PreparedStatement select = Main.CONNECTION.prepareStatement(shipInfoselect)) {
                            var resultLocation = select.executeQuery();
                            if(resultLocation.next()){
                                int column = resultLocation.getInt("numberofcolumn");
                                int row = resultLocation.getInt("numberofrow");
                                boolean lossbattle = resultLocation.getBoolean("loss_battle");
                                ShipLocations shipLocations = new ShipLocations(column, row, lossbattle);
                                warcraft.getShipLocations().add(shipLocations);
                            }
                        }
                    }
                }
                Main.enemy.getBattleWarCrafts().add(warcraft);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void GetNameOfEnemy(int idOfEnemy) {
      String getName = "SELECT name FROM player WHERE id = " + idOfEnemy;
        try {
            PreparedStatement selectState = Main.CONNECTION.prepareStatement(getName);
            var nameResult = selectState.executeQuery();
            if(nameResult.next()) {
                String name = nameResult.getString("name");
                Main.enemy.setName(name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void SetEnemyForBattleShip(int yourId, int idOfEnemy) {
        String updateEnemyId = "UPDATE player SET enemyid=? WHERE id=?";
        try {
            PreparedStatement updateState = Main.CONNECTION.prepareStatement(updateEnemyId);
            updateState.setInt(1, idOfEnemy);
            updateState.setInt(2, yourId);
            updateState.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void FindEnemyForBattleShip() {
        String selectEnemy = "SELECT id FROM player WHERE enemyid=0";
        int yourId = Main.player.getId();
        try {
            PreparedStatement selectState = Main.CONNECTION.prepareStatement(selectEnemy);
            var potentialEnemyResult = selectState.executeQuery();
            while(potentialEnemyResult.next()) {
                int idOfEnemy = potentialEnemyResult.getInt("id");
                if(idOfEnemy != yourId) {
                    SetEnemyForBattleShip(yourId, idOfEnemy);
                    SetEnemyForBattleShip(idOfEnemy, yourId);
                    Main.player.setEnemy_id(idOfEnemy);
                    Main.enemy.setEnemy_id(yourId);
                    GetNameOfEnemy(idOfEnemy);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private void CheckThePresenceOfEnemy() {
        int yourId = Main.player.getId();
        String check = "SELECT enemyid FROM player WHERE id="+yourId;
        try {
            PreparedStatement selectState = Main.CONNECTION.prepareStatement(check);
            var resultOfSelect = selectState.executeQuery();
            if(resultOfSelect.next()) {
                int idOfEnemy = resultOfSelect.getInt("enemyid");
                if(idOfEnemy != 0) {
                    Main.player.setEnemy_id(idOfEnemy);
                    Main.enemy.setEnemy_id(yourId);
                    GetNameOfEnemy(idOfEnemy);
                } else {
                    FindEnemyForBattleShip();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void showMessage() {
        try {
            Stage errorWindow = new Stage();
            errorWindow.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("message.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            errorWindow.setScene(scene);
            errorWindow.setTitle("Error");
            MessageController controller = fxmlLoader.getController();
            controller.getCloseWindowsOfThat().setOnAction(event -> {
                errorWindow.close();
                CheckThePresenceOfEnemy();
                CheckDatabase();
            });
            errorWindow.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CheckDatabase() {
        boolean existenceOfEnemy = false;
        String selectEnemy = "SELECT id FROM player WHERE enemyid=0";
        int yourId = Main.player.getId();
        if(Main.player.getEnemy_id()!=null) {
            existenceOfEnemy = true;
        } else {
            try {
                PreparedStatement selectState = Main.CONNECTION.prepareStatement(selectEnemy);
                var result = selectState.executeQuery();
                while (result.next()) {
                    int id = result.getInt("id");
                    if (id != yourId) {
                        existenceOfEnemy = true;
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(existenceOfEnemy) {
            CheckThePresenceOfEnemy();
            ReadInformationAboutEnemy();
            enemyField.setOnMouseClicked(this::onMouseClickedOnGridPane);
        } else {
            showMessage();
        }
    }

    public void initialize() {
        CheckDatabase();
    }
}


