package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.DungeonPlayerException;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceSynchronousAdapter;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.restadapter.GameStatus;

import java.util.List;
import java.util.UUID;

@Service
public class GameApplicationService {
    private GameRepository gameRepository;
    private GameServiceSynchronousAdapter gameServiceSynchronousAdapter;
    private Logger logger = LoggerFactory.getLogger( GameApplicationService.class );
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceSynchronousAdapter gameServiceSynchronousAdapter ) {
        this.gameRepository = gameRepository;
        this.gameServiceSynchronousAdapter = gameServiceSynchronousAdapter;
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
        GameDto gameDto = gameServiceSynchronousAdapter.fetchCurrentGameState();
        Game game = modelMapper.map( gameDto, Game.class );
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
