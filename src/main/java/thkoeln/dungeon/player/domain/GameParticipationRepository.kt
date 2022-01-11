package thkoeln.dungeon.player.domain

import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameParticipationRepository : CrudRepository<GameParticipation?, UUID?>