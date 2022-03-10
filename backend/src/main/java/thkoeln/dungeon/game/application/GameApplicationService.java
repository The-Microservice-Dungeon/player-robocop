package thkoeln.dungeon.game.application;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.domain.game.*;
import thkoeln.dungeon.game.domain.round.RoundStatus;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;
import thkoeln.dungeon.robot.application.RobotApplicationService;

import java.util.*;

@Service
public class GameApplicationService {
    private final GameRepository gameRepository;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;
    private final Logger logger = LoggerFactory.getLogger(GameApplicationService.class);
    ModelMapper modelMapper = new ModelMapper();
    private final MapApplicationService mapService;
    private final PlanetApplicationService planetApplicationService;
    private final RobotApplicationService robotApplicationService;
    private final PlayerApplicationService playerApplicationService;

    @Autowired
    public GameApplicationService(GameRepository gameRepository,
                                  GameServiceRESTAdapter gameServiceRESTAdapter,
                                  PlayerApplicationService playerApplicationService,
                                  MapApplicationService mapService, PlanetApplicationService planetApplicationService, RobotApplicationService robotApplicationService, PlayerApplicationService playerApplicationService1) {
        this.gameRepository = gameRepository;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.mapService = mapService;
        this.planetApplicationService = planetApplicationService;
        this.robotApplicationService = robotApplicationService;
        this.playerApplicationService = playerApplicationService1;
    }

    public Game retrieveListedGameWithStatus(GameStatus gameStatus) throws GameStatusException, NoGameAvailableException{
        List<Game> found = gameRepository.findAllByGameStatusEquals(gameStatus);
        List<Game> allGames = gameRepository.findAll();
        if (allGames.isEmpty()){
            throw new NoGameAvailableException("No games in DB yet, retry in a bit...");
        }
        if (gameStatus==GameStatus.ORPHANED){
            logger.warn("This method only returns one game. Returning first orphaned game found");
            if (!found.isEmpty()){
                return found.get(0);
            }
            else {
                throw new GameStatusException("No Games in orphaned status");
            }
        }
        if (found.size()!=1){
            //"There can be only one" *Glass shatters*
            throw new GameStatusException("There has to be exactly ONE game with status "+ gameStatus.toString()+" But there are "+found.size()+".");
        }
        return found.get(0);
    }

    public List<Game> retrieveActiveGames() {
        return gameRepository.findAllByGameStatusEquals(GameStatus.STARTED);
    }

    public Game retrieveCurrentGame() {
        List<Game> games = gameRepository.findAllByGameStatusBetween(GameStatus.CREATED, GameStatus.ENDED);
        if (games.isEmpty()) return null;
        return games.get(0);
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
                mapService.createMapFromGame(game);
                playerApplicationService.reloadBearerToken();
                logger.info("Updated game " + game);
            } else {
                game.makeOrphan();
                gameRepository.save(game);
            }
        }
        for (GameDto gameDto : unknownGameDtos) {
            Game game = this.storeGame(gameDto);
            mapService.createMapFromGame(game);
            logger.info("Received game " + game + " for the first time");
        }
        logger.info("Retrieval of new game state finished");
    }

    public Game storeGame(GameDto gameDto) {
        Game game = modelMapper.map(gameDto, Game.class);
        game.setCurrentPlayers(getCurrentPlayers());
        gameRepository.save(game);
        return game;
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
    private void gameExternallyCreated(UUID gameId) {
        logger.info("Processing external event that the game has been created");

        deleteData();

        List<Game> foundGames = gameRepository.findByGameId(gameId);
        if (foundGames.isEmpty()){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Synchronizing game state via REST...");
            synchronizeGameState();
        }
        else {
            // TODO: Merge games instead of just dying here
            throw new IllegalStateException("Found "+ foundGames.size() + " matching games with same gameId.");
        }
    }

    /**
     * To be called by event consumer listening to GameService event
     *
     * @param gameId
     * The id of the triggering event
     */
    private void gameExternallyStarted(UUID gameId) {
        logger.info("Processing external event that the game with id " + gameId + " has started");
        List<Game> foundGames = gameRepository.findByGameId(gameId);
        if (foundGames.size()!=1){
            // TODO: Merge games instead of just dying here
            logger.warn("Found "+ foundGames.size() + " matching games with gameId. Expected 1"+ gameId);
            return;
        }

        Game game = foundGames.get(0);
        game.setCurrentPlayers(getCurrentPlayers());

        mapService.createMapFromGame(game);
        game.start();
        gameRepository.save(game);
    }

    public void deleteData () {
        mapService.deleteMap();
        planetApplicationService.deletePlanets();
        robotApplicationService.deleteRobots();
    }

    /**
     * To be called by event consumer listening to GameService event
     */
    private void gameExternallyEnded(UUID gameId) {
        logger.info("Processing external event that the game with id " + gameId + " has ended");
        List<Game> foundGames = gameRepository.findByGameId(gameId);
        if (foundGames.size()!=1){
            // TODO: Merge games instead of just dying here
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
            // TODO: Merge games instead of just dying here
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

    private Integer getCurrentPlayers () {
        GameDto[] gameDtos;
        try {
            gameDtos = this.gameServiceRESTAdapter.fetchCurrentGameState();
        } catch (UnexpectedRESTException | RESTConnectionFailureException e) {
            logger.error("Can't fetch current Game State! " + e.getMessage());
            return null;
        }
        return gameDtos[0].getParticipatingPlayers().size();
    }


}
