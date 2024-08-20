package gamefiles.database.entity;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private Integer id;
    private String name;
    private List<WarCraft> battleWarCrafts = new ArrayList<>();
    private Integer enemy_id;
}
