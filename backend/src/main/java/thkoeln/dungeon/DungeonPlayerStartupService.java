package thkoeln.dungeon;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.player.application.PlayerApplicationService;

@Service
public class DungeonPlayerStartupService implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(DungeonPlayerStartupService.class);
    private final GameRepository gameRepository;
    private final GameApplicationService gameApplicationService;
    private final PlayerApplicationService playerApplicationService;

    @Autowired
    public DungeonPlayerStartupService(
            GameRepository gameRepository, GameApplicationService gameApplicationService,
            PlayerApplicationService playerApplicationService) {
        this.gameRepository = gameRepository;
        this.gameApplicationService = gameApplicationService;
        this.playerApplicationService = playerApplicationService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        gameApplicationService.synchronizeGameState();
        playerApplicationService.createPlayers();
        playerApplicationService.obtainBearerTokensForPlayers();
    }
}
