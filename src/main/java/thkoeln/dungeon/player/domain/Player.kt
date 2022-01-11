package thkoeln.dungeon.player.domain

import lombok.*
import thkoeln.dungeon.game.domain.Game
import java.util.*
import javax.persistence.*

@Entity
@Setter
@Getter
@NoArgsConstructor
class Player {
    @Id
    private val id = UUID.randomUUID()
    var name: String? = null
    var email: String? = null
    var bearerToken: UUID? = null

    @OneToMany(cascade = [CascadeType.MERGE, CascadeType.REMOVE], fetch = FetchType.EAGER)
    private val gameParticipations: MutableList<GameParticipation> = ArrayList()

    /**
     * Choose a random and unique name and email for the player
     */
    fun assignRandomName() {
        val randomNickname = NameGenerator.generateName()
        this.name = randomNickname
        this.email = "$randomNickname@microservicedungeon.com"
    }

    val isReadyToPlay: Boolean
        get() = bearerToken != null

    fun participateInGame(game: Game?) {
        val gameParticipation = GameParticipation(game)
        gameParticipations.add(gameParticipation)
    }

    fun isParticipantInGame(game: Game): Boolean {
        return findParticipationFor(game) != null
    }

    private fun findParticipationFor(game: Game): GameParticipation? {
        val found = gameParticipations.stream()
            .filter { gp: GameParticipation -> gp.game == game }.findFirst()
        return if (found.isPresent) found.get() else null
    }

    fun playRound() {
        // todo
    }

    override fun toString(): String {
        return "Player '$name' ($email), bearerToken: $bearerToken"
    }
}