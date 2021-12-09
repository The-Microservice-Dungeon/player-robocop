package thkoeln.dungeon;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.adapter.GameServiceSynchronousAdapter;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;

@Service
public class StartupService implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger( StartupService.class );
    private GameRepository gameRepository;
    private GameServiceSynchronousAdapter gameExternalAdaptor;

    @Autowired
    public StartupService(GameRepository gameRepository, GameServiceSynchronousAdapter gameExternalAdaptor ) {
        this.gameRepository = gameRepository;
        this.gameExternalAdaptor = gameExternalAdaptor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Game game = gameExternalAdaptor.fetchCurrentGameState();
        gameRepository.save( game );
        logger.info( "Game " + game + " saved." );
    }
}
