package gamefiles.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"numberOfColumn", "numberOfRow"})
public class ShipLocations {
    private Integer numberOfColumn;
    private Integer numberOfRow;
    private Boolean loss_battle;
}
