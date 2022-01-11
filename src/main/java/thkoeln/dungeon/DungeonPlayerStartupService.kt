package thkoeln.dungeon

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import thkoeln.dungeon.game.application.GameApplicationService
import thkoeln.dungeon.game.domain.GameRepository
import thkoeln.dungeon.player.application.PlayerApplicationService

@Service
class DungeonPlayerStartupService @Autowired constructor(
    private val gameRepository: GameRepository, private val gameApplicationService: GameApplicationService,
    private val playerApplicationService: PlayerApplicationService
) : ApplicationListener<ContextRefreshedEvent?> {
    private val logger = LoggerFactory.getLogger(DungeonPlayerStartupService::class.java)

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        gameApplicationService.synchronizeGameState()
        playerApplicationService.createPlayers()
        playerApplicationService.obtainBearerTokensForPlayers()
    }
}