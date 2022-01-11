package thkoeln.dungeon.eventconsumer.game

import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameStatusEventRepository : CrudRepository<GameStatusEvent?, UUID?>