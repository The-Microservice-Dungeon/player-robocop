package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class GameApplicationService {
    private GameRepository gameRepository;
    private GameServiceRESTAdapter gameServiceRESTAdapter;
    private Logger logger = LoggerFactory.getLogger( GameApplicationService.class );
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter ) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    public Game retrieveActiveGame() {
        List<Game> games = gameRepository.findAllByGameStatusBetween( GameStatus.CREATED, GameStatus.GAME_RUNNING );
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
        GameDto[] gameDtos = gameServiceRESTAdapter.fetchCurrentGameState();

        // We take a very simple approach here. We, as a Player, don't manage any game
        // state - we just assume that GameService does a proper job. So we just store
        // the incoming games.
        Iterator<GameDto> iterator = Arrays.stream(gameDtos).iterator();
        while ( iterator.hasNext() ) {
            GameDto gameDto = iterator.next();
            Game game = modelMapper.map(gameDto, Game.class);
            gameRepository.save(game);
        }
        logger.info( "Retrieved new game state" );
    }

    /**
     * To be called by event consumer listening to GameService event
     * @param gameId
     */
    public void processGameStartedEvent( UUID gameId ) {
        logger.info( "Processing external event that the game has started");
        List<Game> foundGames = gameRepository.findAllByGameStatusEquals( GameStatus.CREATED );

    }

    /**
     * Makes sure that there is only one "active" game in order to be in a consistent state
     */
    private void cleanupGames( Game activeGame ) {

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
    public void newRound( UUID gameId, Integer roundNumber ) {
        logger.info( "Processing 'new round' event for round no. " + roundNumber );
        // todo
    }
}
