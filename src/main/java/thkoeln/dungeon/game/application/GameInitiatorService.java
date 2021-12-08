package thkoeln.dungeon.game.application;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;

@Service
public class GameInitiatorService implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger( GameInitiatorService.class );
    private GameRepository gameRepository;
    private GameSynchronousAdaptor gameExternalAdaptor;

    @Autowired
    public GameInitiatorService( GameRepository gameRepository, GameSynchronousAdaptor gameExternalAdaptor ) {
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
