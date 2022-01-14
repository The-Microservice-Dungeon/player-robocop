package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameException;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.game.domain.game.GameStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.*;

@Service
public class GameApplicationService {
    ModelMapper modelMapper = new ModelMapper();
    private final GameRepository gameRepository;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final Logger logger = LoggerFactory.getLogger(GameApplicationService.class);

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter,
                                  PlayerApplicationService playerApplicationService) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }


    public List<Game> retrieveActiveGames() {
        return gameRepository.findAllByGameStatusEquals(GameStatus.GAME_RUNNING);
    }


    /**
     * Makes sure that our own game state is consistent with what GameService says.
     * We take a very simple approach here. We, as a Player, don't manage any game
     * state - we just assume that GameService does a proper job. So we just store
     * the incoming games. Only in the case that a game should suddenly "disappear",
     * we keep it and mark it as ORPHANED - there may be local references to it.
     */
    public void synchronizeGameState() {
        GameDto[] gameDtos = new GameDto[0];
        try {
            gameDtos = gameServiceRESTAdapter.fetchCurrentGameState();
        } catch (UnexpectedRESTException | RESTConnectionFailureException e) {
            logger.warn("Problems with GameService while synchronizing game state (" + e.getMessage() + ") - need to try again later.\n" +
                    Arrays.toString(e.getStackTrace()).replaceAll(",", "\n"));
        }

        // We need to treat the new games (those we haven't stored yet) and those we
        // already have in a different way. Therefore let's split the list.
        List<GameDto> unknownGameDtos = new ArrayList<>();
        List<GameDto> knownGameDtos = new ArrayList<>();
        for (GameDto gameDto : gameDtos) {
            if (gameRepository.existsByGameId(gameDto.getGameId())) knownGameDtos.add(gameDto);
            else unknownGameDtos.add(gameDto);
        }

        List<Game> storedGames = gameRepository.findAll();
        for (Game game : storedGames) {
            Optional<GameDto> foundDtoOptional = knownGameDtos.stream()
                    .filter(dto -> game.getGameId().equals(dto.getGameId())).findAny();
            if (foundDtoOptional.isPresent()) {
                modelMapper.map(foundDtoOptional.get(), game);
                gameRepository.save(game);
                logger.info("Updated game " + game);
            } else {
                game.makeOrphan();
                gameRepository.save(game);
            }
        }
        for (GameDto gameDto : unknownGameDtos) {
            this.storeGame(gameDto);
            logger.info("Received game " + gameDto + " for the first time");
        }
        logger.info("Retrieval of new game state finished");
    }

    public void storeGame (GameDto gameDto) {
        Game game = modelMapper.map(gameDto, Game.class);
        gameRepository.save(game);
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created
     */
    public void gameStatusExternallyChanged(UUID gameId, GameStatus gameStatus) {
        switch (gameStatus) {
            case CREATED:
                gameExternallyCreated(gameId);
                break;
        }
    }


    /**
     * To be called by event consumer listening to GameService event
     *
     * @param gameId ID of the new game
     */
    public void gameExternallyCreated(UUID gameId) {
        logger.info("Processing external event that the game has been created");
        List<Game> fittingGames = gameRepository.findByGameId(gameId);
        Game game = null;
        if (fittingGames.size() == 0) {
            game = Game.newlyCreatedGame(gameId);
            gameRepository.save(game);
        } else if (fittingGames.size() > 1){
            game = mergeGamesIntoOne(fittingGames);
            game.resetToNewlyCreated();
            gameRepository.save(game);
        }
    }


    /**
     * Repair the situation that there are seemingly several games sharing the same gameId. This should not
     * happen. Do this by "merging" the games.
     *
     * @param fittingGames
     */
    public Game mergeGamesIntoOne(List<Game> fittingGames) {
        if (fittingGames == null) throw new GameException("List of games to be merged must not be null!");
        if (fittingGames.size() <= 1)
            throw new GameException("List of games to be merged must contain at least 2 entries!");

        // todo - needs to be properly implemented
        return fittingGames.get(0);
    }


    /**
     * To be called by event consumer listening to GameService event
     *
     * @param eventId
     */
    public void gameExternallyStarted(UUID eventId) {
        logger.info("Processing external event that the game with id " + eventId + " has started");
        List<Game> foundGames = gameRepository.findAllByGameStatusEquals(GameStatus.GAME_RUNNING);
    }


    /**
     * To be called by event consumer listening to GameService event
     *
     * @param gameId
     */
    public void gameEnded(UUID gameId) {
        logger.info("Processing 'game ended' event");
        // todo
    }

    /**
     * To be called by event consumer listening to GameService event
     *
     * @param gameId
     */
    public void newRound(UUID gameId, Integer roundNumber) {
        logger.info("Processing 'new round' event for round no. " + roundNumber);
        // todo
    }
}
