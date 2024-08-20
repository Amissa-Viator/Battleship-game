package gamefiles.database.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
public class WarCraft {
    private Integer id;
    private Integer classOfShip;
    private List<ShipLocations> shipLocations = new ArrayList<>();

}
