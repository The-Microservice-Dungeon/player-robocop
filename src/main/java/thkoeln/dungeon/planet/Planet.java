package thkoeln.dungeon.planet;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Planet {
    @Id
    private final UUID id = UUID.randomUUID();
}
