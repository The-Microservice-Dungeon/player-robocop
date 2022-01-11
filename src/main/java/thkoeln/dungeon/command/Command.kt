package thkoeln.dungeon.command

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import thkoeln.dungeon.robot.domain.Robot
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Should be abstract, but that conflicts with JPS
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Command {
    @Id
    private val id = UUID.randomUUID()
    private val commandType: CommandType? = null

    @ManyToOne
    private val robot: Robot? = null
}