package thkoeln.dungeon.game.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.game.adapter.GameSynchronousAdapter;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.List;
import java.util.UUID;

@Service
public class GameApplicationService {
    private GameRepository gameRepository;
    private GameSynchronousAdapter gameExternalAdaptor;
    private Logger logger = LoggerFactory.getLogger( GameApplicationService.class );

    @Autowired
    public GameApplicationService(GameRepository gameRepository, GameSynchronousAdapter gameExternalAdaptor ) {
        this.gameRepository = gameRepository;
        this.gameExternalAdaptor = gameExternalAdaptor;
    }


    public Game retrieveActiveGame() {
        List<Game> games = gameRepository.findAllByStatusBetween( GameStatus.CREATED, GameStatus.GAME_RUNNING );
        if ( games.size() > 1 ) {
            throw new DungeonPlayerException( "More than one game - consider resetting player!" );
        }
        else if (games.size() == 0 ) {
            logger.warn( "No active game!" );
            return null;
        }
        else {
            return games.get( 0 );
        }
    }


    /**
     * Makes sure that our own game state is consistent with what GameService says
     */
    public void synchronizeGameState() {
        Game game = gameExternalAdaptor.fetchCurrentGameState();
        gameRepository.save( game );
        logger.info( "Game " + game + " saved." );
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void gameStarted( UUID gameId ) {
        logger.info( "Processing 'game started' event");
        Iterable<Game> foundGames = gameRepository.findAllByStatusEquals( GameStatus.CREATED );

    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void gameEnded( UUID gameId ) {
        logger.info( "Processing 'game ended' event");
        // todo
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void newRound( UUID gameId ) {
        logger.info( "Processing 'new round' event");
        // todo
    }
}
