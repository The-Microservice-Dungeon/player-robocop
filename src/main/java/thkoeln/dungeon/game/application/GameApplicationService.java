package thkoeln.dungeon.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.GameServiceMockEventConsumer;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.UUID;

@Service
public class GameApplicationService {
    private GameRepository gameRepository;
    private Logger logger = LoggerFactory.getLogger( GameApplicationService.class );

    @Autowired
    public GameApplicationService( GameRepository gameRepository ) {
        this.gameRepository = gameRepository;
    }

    public void gameStarted( UUID gameId ) {
        logger.info( "Processing 'game started' event");
        Iterable<Game> foundGames = gameRepository.findAllByStatusEquals( GameStatus.CREATED );

    }

    public void gameEnded( UUID gameId ) {
        logger.info( "Processing 'game ended' event");
        // todo
    }

    public void newRound( UUID gameId ) {
        logger.info( "Processing 'new round' event");
        // todo
    }
}
