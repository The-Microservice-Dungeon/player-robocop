package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.game.domain.game.GameStatus;
import thkoeln.dungeon.game.domain.round.RoundStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.game.domain.game.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.*;

@Service
public class GameApplicationService {
    private final GameRepository gameRepository;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final Logger logger = LoggerFactory.getLogger(GameApplicationService.class);
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter,
                                  PlayerApplicationService playerApplicationService) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }

    public Game retrieveCreatedGame() throws IllegalStateException{
        List<Game> found = gameRepository.findAllByGameStatusEquals(GameStatus.CREATED);
        if (found.size()!=1){
            throw new IllegalStateException("There has to be exactly one created game. Found "+found.size());
        }
        return found.get(0);
    }

    public Game retrieveStartedGame() throws IllegalStateException{
        List<Game> found = gameRepository.findAllByGameStatusEquals(GameStatus.STARTED);
        if (found.size()!=1){
            throw new IllegalStateException("There has to be exactly one started game. Found "+found.size());
        }
        return found.get(0);
    }

    public List<Game> retrieveActiveGames() {
        return gameRepository.findAllByGameStatusEquals(GameStatus.STARTED);
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

    public void storeGame(GameDto gameDto) {
        Game game = modelMapper.map(gameDto, Game.class);
        gameRepository.save(game);
    }
    /**
     * "Status changed" event published by GameService, esp. after a game has been created
     */
    public void gameStatusExternallyChanged(UUID gameId, GameStatus gameStatus) {
        List<Game> foundGames = gameRepository.findByGameId(gameId);
        switch (gameStatus) {
            case CREATED -> gameExternallyCreated(gameId);
            case STARTED -> gameExternallyStarted(gameId);
            case ENDED -> gameExternallyEnded(gameId);
        }
    }

    /**
     * To be called by event consumer listening to GameService event
     *
     * @param gameId ID of the new game
     */
    public void gameExternallyCreated(UUID gameId) {
        logger.info("Processing external event that the game has been created");
        List<Game> foundGames = gameRepository.findByGameId(gameId);
        if (foundGames.isEmpty()){
            logger.info("Synchronizing game state via REST...");
            synchronizeGameState();
        }
        else {
            throw new IllegalStateException("Found "+ foundGames.size() + " matching games with same gameId.");
        }
    }

    /**
     * To be called by event consumer listening to GameService event
     *
     * @param gameId
     * The id of the triggering event
     */
    public void gameExternallyStarted(UUID gameId) {
        logger.info("Processing external event that the game with id " + gameId + " has started");
        List<Game> foundGames = gameRepository.findByGameId(gameId);
        if (foundGames.size()!=1){
            logger.warn("Found "+ foundGames.size() + " matching games with gameId. Expected 1"+ gameId);
            return;
        }
        Game game = foundGames.get(0);
        game.start();
        gameRepository.save(game);
    }

    /**
     * To be called by event consumer listening to GameService event
     */
    public void gameExternallyEnded(UUID gameId) {
        logger.info("Processing external event that the game with id " + gameId + " has ended");
        List<Game> foundGames = gameRepository.findByGameId(gameId);
        if (foundGames.size()!=1){
            logger.warn("Found "+ foundGames.size() + " matching games with gameId: "+ gameId+". Expected 1");
            return;
        }
        Game game = foundGames.get(0);
        game.end();
        gameRepository.save(game);
    }

    public void roundStatusExternallyChanged(UUID eventId, Integer roundNumber, RoundStatus roundStatus){
        logger.info("Processing 'roundStatus' event with eventId "+ eventId);
        List<Game> foundGames = gameRepository.findAllByGameStatusEquals(GameStatus.STARTED);
        if (foundGames.size() != 1){
            logger.warn("Found "+ foundGames.size() + " matching games with game status STARTED. Expected 1 ");
            return;
        }
        Game game = foundGames.get(0);
        switch (roundStatus){
            case STARTED -> game.startRound(roundNumber);
            case COMMAND_INPUT_ENDED -> game.getRound().commandInputEnded();
            case ENDED -> game.getRound().roundEnded();
        }
        gameRepository.save(game);
    }

}
