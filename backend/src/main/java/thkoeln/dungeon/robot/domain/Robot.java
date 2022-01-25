package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.map.Map;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

    private int energy;
    private int health;

    @Getter
    @Setter
    private boolean dummy = false;


    @ManyToOne(optional = false)
    private Map maps;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "map_ID")
    private Map map;

    public Map getMaps() {
        return maps;
    }

    public void setMaps(Map maps) {
        this.maps = maps;
    }

    public Robot(boolean isDummy) {
        this.dummy = isDummy;
    }
}

