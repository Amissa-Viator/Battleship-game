package gamefiles;

import gamefiles.database.entity.ShipLocations;
import gamefiles.database.entity.WarCraft;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ArrangementController {
    @FXML
    private Button RotateBtn;

    @FXML
    private Button ShipFourUnits;

    @FXML
    private Button ShipSingleUnit;

    @FXML
    private Button ShipThreeUnits;

    @FXML
    private Button ShipTwoUnits;

    @FXML
    private Button StartBtn;
    @FXML
    private Label selectedLabel;

    @FXML
    private Label selectedUnitLabel;

    @FXML
    private Label yourFieldLabel;

    @FXML
    private GridPane yourfield;

    @FXML
    private AnchorPane yourgamefield;
    private Stage stage;
    private boolean isRotated = false; // за замовчуванням горизонтально
    private int countOfFourDecksShip = 0;
    private int countOfThreeDecksShip = 0;
    private int countOfTwoDecksShip = 0;
    private int countOfOneDecksShip = 0;
    private int numberOfDecks = 0; // кількість палуб в кораблі
    //private double x; // координати
    //private double y;
    private Rectangle ship;
    ArrayList<Ship> listOfShips = new ArrayList<>();

    public void messageInfoAboutMistake(String info) {
        try {
            Stage warning = new Stage();
            warning.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("warning.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            warning.setScene(scene);
            warning.setTitle("Warning");
            WarningController controller = fxmlLoader.getController();
            controller.setInfoAboutError(info);
            warning.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InsertDatesIntoDatabase() {
        ArrayList<Integer> IdLocations = new ArrayList<>();
        String insertWarcraft = "INSERT INTO warcraft (numberofdecks, player_id) VALUES (?,?)";
        String insertLocations = "INSERT INTO locations (numberofcolumn, numberofrow, loss_battle) VALUES (?,?,?)";
        String insertConnect = "INSERT INTO warcraft_locations (warcraft_id, locations_id) VALUES (?,?)";
        int yourId = Main.player.getId();
        try {
            for(WarCraft warcraft: Main.player.getBattleWarCrafts()) {
                PreparedStatement insertState = Main.CONNECTION.prepareStatement(insertWarcraft, Statement.RETURN_GENERATED_KEYS);
                int decks = warcraft.getClassOfShip();
                insertState.setInt(1, decks);
                insertState.setInt(2, yourId);
                insertState.executeUpdate();
                var generatedKeys = insertState.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt("id");
                    warcraft.setId(generatedId);
                }
                try(PreparedStatement insertLocation = Main.CONNECTION.prepareStatement(insertLocations, Statement.RETURN_GENERATED_KEYS)) {
                    for (ShipLocations shipLocation : warcraft.getShipLocations()) {
                        int column = shipLocation.getNumberOfColumn();
                        int row = shipLocation.getNumberOfRow();
                        boolean state = shipLocation.getLoss_battle();
                        insertLocation.setInt(1, column);
                        insertLocation.setInt(2, row);
                        insertLocation.setBoolean(3, state);
                        insertLocation.executeUpdate();
                        var generatedKey = insertLocation.getGeneratedKeys();
                        if (generatedKey.next()) {
                            int generatedId = generatedKey.getInt("id");
                            IdLocations.add(generatedId);
                        }
                    }
                }
                try(PreparedStatement insertConnection = Main.CONNECTION.prepareStatement(insertConnect, Statement.RETURN_GENERATED_KEYS)) {
                    int idOfWarcraft = warcraft.getId();
                    for (int idLocation : IdLocations) {
                        insertConnection.setInt(1, idOfWarcraft);
                        insertConnection.setInt(2, idLocation);
                        insertConnection.executeUpdate();
                    }
                }
                IdLocations.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void ConnectWithOpponentToGame(ActionEvent event) {
        int countOfAllShips = countOfFourDecksShip+countOfThreeDecksShip+countOfTwoDecksShip+countOfOneDecksShip;
        if(countOfAllShips < 10) {
           messageInfoAboutMistake("Don't put 10 ships on the field");
        } else {
            InsertDatesIntoDatabase();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fields.fxml"));
                Parent root = fxmlLoader.load();
                stage.setScene(new Scene(root));
                stage.setTitle("Battleship");
                BattleshipController controller = fxmlLoader.getController();
                controller.fillFieldWithShips(listOfShips);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void SelectedFourUnitsShip(ActionEvent event) {
        if(countOfFourDecksShip == 0) {
            setselectedUnitLabel("ship of 4 units");
            numberOfDecks = 4;
            countOfFourDecksShip++;
            ShipFourUnits.setStyle("-fx-background-color: #F0FFFF; -fx-border-color: #4169E1; -fx-text-fill: #4169E1;");
            ship = createShip();
        } else {
            setselectedUnitLabel("");
        }
    }

    @FXML
    void SelectedOneUnitShip(ActionEvent event) {
        if(countOfOneDecksShip <= 4) {
            setselectedUnitLabel("ship of 1 unit");
            numberOfDecks = 1;
            countOfOneDecksShip++;
            ship = createShip();
            if(countOfOneDecksShip == 4) {
                ShipSingleUnit.setStyle("-fx-background-color: #F0FFFF; -fx-border-color: #4169E1; -fx-text-fill: #4169E1;");
            }
        } else {
            setselectedUnitLabel("");
        }
    }

    @FXML
    void SelectedThreeUnitsShip(ActionEvent event) {
        if(countOfOneDecksShip <= 2) {
            setselectedUnitLabel("ship of 3 units");
            numberOfDecks = 3;
            countOfThreeDecksShip++;
            ship = createShip();
            if(countOfThreeDecksShip == 2) {
                ShipThreeUnits.setStyle("-fx-background-color: #F0FFFF; -fx-border-color: #4169E1; -fx-text-fill: #4169E1;");
            }
        } else {
            setselectedUnitLabel("");
        }
    }

    @FXML
    void SelectedTwoUnitsShip(ActionEvent event) {
        if(countOfTwoDecksShip <= 3){
            setselectedUnitLabel("ship of 2 units");
            numberOfDecks = 2;
            countOfTwoDecksShip++;
            ship = createShip();
            if (countOfTwoDecksShip == 3) {
                ShipTwoUnits.setStyle("-fx-background-color: #F0FFFF; -fx-border-color: #4169E1; -fx-text-fill: #4169E1;");
            }
        } else {
            setselectedUnitLabel("");
        }
    }

    @FXML
    void RotateShipPosition(ActionEvent event) {
        isRotated = !isRotated;
    }

    private void CreateShip(int columnIndex, int rowIndex) {
        WarCraft warCraft = new WarCraft();
        warCraft.setClassOfShip(numberOfDecks);
        ShipLocations shipLocations;
        for(int i=0; i < numberOfDecks; i++) {
            if(isRotated) {
                shipLocations = new ShipLocations(columnIndex, rowIndex+i, false);
                warCraft.getShipLocations().add(shipLocations);
            } else {
                shipLocations = new ShipLocations(columnIndex+i, rowIndex, false);
                warCraft.getShipLocations().add(shipLocations);
            }
        }
        Main.player.
                getBattleWarCrafts().
                add(warCraft);
    }
    @FXML
   void onMouseClickedOnGridPane(MouseEvent event) {
       if (ship != null) {
           double mouseX = event.getX();
           double mouseY = event.getY();
           int columnIndex = (int) (mouseX / 30);
           int rowIndex = (int) (mouseY / 30);
           int amountOfColumns = yourfield.getColumnConstraints().size();
           int amountOfRows = yourfield.getRowConstraints().size();
           if(isRotated) {
               if(rowIndex+numberOfDecks > amountOfRows) {
                   changeButtonColorAndCountOfCreatedShips();
               } else {
                   yourfield.add(ship, columnIndex, rowIndex, 1, numberOfDecks);
                   listOfShips.add(new Ship(numberOfDecks, columnIndex, rowIndex, isRotated));
                   CreateShip(columnIndex, rowIndex);
               }
           } else {
               if(columnIndex+numberOfDecks > amountOfColumns) {
                   changeButtonColorAndCountOfCreatedShips();
               } else {
                   yourfield.add(ship, columnIndex, rowIndex, numberOfDecks, 1);
                   listOfShips.add(new Ship(numberOfDecks, columnIndex, rowIndex, isRotated));
                   CreateShip(columnIndex, rowIndex);
               }
           }
           ship = null;
       }
   }

    public void changeButtonColorAndCountOfCreatedShips() {
        if(numberOfDecks == 1) {
            countOfOneDecksShip--;
            ShipSingleUnit.setStyle("-fx-background-color: #F0FFFF; -fx-border-color:  #00008B; -fx-text-fill: #191970;");
        } else if (numberOfDecks == 2) {
            countOfTwoDecksShip--;
            ShipTwoUnits.setStyle("-fx-background-color: #F0FFFF; -fx-border-color:  #00008B; -fx-text-fill: #191970;");
        } else if (numberOfDecks == 3) {
            countOfThreeDecksShip--;
            ShipThreeUnits.setStyle("-fx-background-color: #F0FFFF; -fx-border-color:  #00008B; -fx-text-fill: #191970;");
        } else {
            countOfFourDecksShip--;
            ShipFourUnits.setStyle("-fx-background-color: #F0FFFF; -fx-border-color:  #00008B; -fx-text-fill: #191970;");
        }
        messageInfoAboutMistake("Ship must be on the field");
    }

    public void initialize() {
        yourfield.setOnMouseClicked(this::onMouseClickedOnGridPane);
    }

    private Rectangle createShip() {
        Rectangle rectangle;
        if (isRotated) {
            rectangle = new Rectangle(30, 30 * numberOfDecks);
        } else {
            rectangle = new Rectangle(30 * numberOfDecks, 30);
        }
        rectangle.setFill(Color.rgb(0, 0, 139));
        return rectangle;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setselectedUnitLabel(String infoAboutShip) {
        if(infoAboutShip.isEmpty()) {
            selectedUnitLabel.setText("Selected unit: ");
        } else {
            String info = selectedUnitLabel.getText();
            if(info == "Selected unit:") {
                selectedUnitLabel.setText(info + " " + infoAboutShip);
            }
            else {
                selectedUnitLabel.setText("Selected unit:" + " " + infoAboutShip);
            }
        }

    }

}
