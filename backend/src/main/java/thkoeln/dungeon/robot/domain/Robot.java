
package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.map.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();




}

package thkoeln.dungeon.robot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Robot {
    @Id
    private final UUID id = UUID.randomUUID();

    private int energy;
    private int health;

}

