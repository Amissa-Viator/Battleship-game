package gamefiles.database.entity;

import lombok.Getter;

@Getter
public enum ClassOfShip {
    DESTROYER(1), SUBMARINE(2), CRUISER(3), BATTLESHIP(4);

    private final int number;
    ClassOfShip(int number) {
        this.number = number;
    }

}
