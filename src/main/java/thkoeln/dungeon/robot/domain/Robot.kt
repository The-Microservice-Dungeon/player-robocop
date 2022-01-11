package thkoeln.dungeon.robot.domain

import lombok.Getter
import lombok.Setter
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@Getter
@Setter
class Robot {
    @Id
    private val id = UUID.randomUUID()
}