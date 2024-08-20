module com.example.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires lombok;


    opens gamefiles to javafx.fxml;
    exports gamefiles;
}