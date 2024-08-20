package gamefiles;

public class Ship {
    private int numberOfDecks;
    private int numberOfColumn;
    private int numberOfRow;
    private boolean rotationOfShip;

    public Ship(int numberOfDecks, int numberOfColumn, int numberOfRow, boolean rotationOfShip) {
        this.numberOfDecks = numberOfDecks;
        this.numberOfColumn = numberOfColumn;
        this.numberOfRow = numberOfRow;
        this.rotationOfShip = rotationOfShip;
    }

    public int getNumberOfDecks() {
        return numberOfDecks;
    }

    public void setNumberOfDecks(int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
    }

    public int getNumberOfColumn() {
        return numberOfColumn;
    }

    public void setNumberOfColumn(int numberOfColumn) {
        this.numberOfColumn = numberOfColumn;
    }

    public int getNumberOfRow() {
        return numberOfRow;
    }

    public void setNumberOfRow(int numberOfRow) {
        this.numberOfRow = numberOfRow;
    }

    public boolean isRotationOfShip() {
        return rotationOfShip;
    }

    public void setRotationOfShip(boolean rotationOfShip) {
        this.rotationOfShip = rotationOfShip;
    }

}
