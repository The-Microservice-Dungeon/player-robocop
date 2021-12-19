package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.game.domain.GameStatus;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;

import java.util.*;

@Service
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


    public List<Game> retrieveActiveGames() {
        return gameRepository.findAllByGameStatusEquals( GameStatus.GAME_RUNNING );
    }


    /**
     * Makes sure that our own game state is consistent with what GameService says.
     * We take a very simple approach here. We, as a Player, don't manage any game
     * state - we just assume that GameService does a proper job. So we just store
     * the incoming games. Only in the case that a game should suddenly "disappear",
     * we keep it and mark it as ORPHANED - there may be local references to it.
     */
    public void synchronizeGameState() {
        GameDto[] gameDtos = gameServiceRESTAdapter.fetchCurrentGameState();

        // We need to treat the new games (those we haven't stored yet) and those we
        // already have in a different way. Therefore let's split the list.
        List<GameDto> unknownGameDtos = new ArrayList<>();
        List<GameDto> knownGameDtos = new ArrayList<>();
        for ( GameDto gameDto: Arrays.asList( gameDtos ) ) {
            if ( gameRepository.existsById( gameDto.getGameId() ) ) knownGameDtos.add( gameDto );
            else unknownGameDtos.add( gameDto );
        }

        List<Game> storedGames = gameRepository.findAll();
        for ( Game game: storedGames ) {
            Optional<GameDto> foundDtoOptional = knownGameDtos.stream()
                    .filter( dto -> game.getGameId().equals( dto.getGameId() )).findAny();
            if ( foundDtoOptional.isPresent() ) {
                Game updatedGame = modelMapper.map( foundDtoOptional.get(), Game.class );
                gameRepository.save( updatedGame );
                logger.info( "Updated game " + updatedGame );
            }
            else {
                game.setGameStatus( GameStatus.ORPHANED );
                gameRepository.save( game );
                logger.warn( "Marked game " + game + " as ORPHANED!" );
            }
        }
        for ( GameDto gameDto: unknownGameDtos ) {
            Game game = modelMapper.map( gameDto, Game.class );
            gameRepository.save( game );
            logger.info( "Received game " + game + " for the first time");
        }
        logger.info( "Retrieval of new game state finished" );
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
