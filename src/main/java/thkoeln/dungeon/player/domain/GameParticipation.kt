package thkoeln.dungeon.player.domain

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString
import thkoeln.dungeon.game.domain.Game
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
class GameParticipation(@field:ManyToOne val game: Game?) {
    @Id
    private val id = UUID.randomUUID()
    private val money: Int? = null
}