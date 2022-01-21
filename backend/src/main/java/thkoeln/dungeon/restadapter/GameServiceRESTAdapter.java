package thkoeln.dungeon.restadapter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.RESTRequestDeniedException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

@Component
public class GameServiceRESTAdapter {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(GameServiceRESTAdapter.class);
    @Value("${GAME_SERVICE}")
    private String gameServiceUrlString;

    @Autowired
    public GameServiceRESTAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * GET /games
     * Get current Games from Game Service
     *
     * @return Array of GameDTOs
     * @throws UnexpectedRESTException
     * @throws RESTConnectionFailureException
     */
    public GameDto[] fetchCurrentGameState()
            throws UnexpectedRESTException, RESTConnectionFailureException {
        GameDto[] gameDtos;
        String urlString = gameServiceUrlString + "/games";
        try {
            gameDtos = restTemplate.getForObject(urlString, GameDto[].class);
            if (gameDtos == null) throw new UnexpectedRESTException("Received a null GameDto array - wtf ...?");
            logger.info("Got " + gameDtos.length + " game(s) via REST ...");
            Iterator<GameDto> iterator = Arrays.stream(gameDtos).iterator();
            while (iterator.hasNext()) {
                logger.info("... " + iterator.next());
            }
        } catch (RestClientException e) {
            throw new RESTConnectionFailureException(urlString, e.getMessage());
        }
        return gameDtos;
    }


    /**
     * POST /games
     * Create a new Game
     *
     * @param maxPlayers maximum Players for the new Game
     * @param maxRounds  maximum Rounds for the new Game
     * @return true if successful
     * @throws UnexpectedRESTException
     * @throws RESTConnectionFailureException
     */
    public boolean createNewGame(Integer maxPlayers, Integer maxRounds)
            throws UnexpectedRESTException, RESTConnectionFailureException {
        GameDto gameDto;
        String urlString = gameServiceUrlString + "/games";
        String json = new JSONObject()
                .put("maxPlayers", maxPlayers)
                .put("maxRounds", maxRounds)
                .toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        try {
            gameDto = restTemplate.postForObject(urlString, request, GameDto.class);
            if (gameDto == null) throw new UnexpectedRESTException("Received a null GameDto");
            logger.info("Created new Game " + gameDto);
            return true;
        } catch (RestClientException e) {
            throw new RESTConnectionFailureException(urlString, e.getMessage());
        }
    }


    /**
     * PUT /games/<gameId>/players/<playerToken>
     * Register a specific player for a specific game via call to GameService endpoint.
     * Caveat: GameService returns somewhat weird error codes (non-standard).
     *
     * @param gameId      of the game
     * @param playerToken of the player
     * @return true if successful
     */
    public boolean registerPlayerForGame(UUID gameId, UUID playerToken)
            throws RESTConnectionFailureException, RESTRequestDeniedException {
        String urlString = gameServiceUrlString + "/games/" + gameId + "/players/" + playerToken;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(headers);
            restTemplate.put(urlString, request);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE)) {
                // this is a business logic problem - so let the application service handle this
                throw new RESTRequestDeniedException("Player with bearer token " + playerToken +
                        " already registered in game with id " + gameId);
            } else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new RESTRequestDeniedException("For player with bearer token " + playerToken +
                        " and game with id " + gameId + " the player registration went wrong; original error msg: "
                        + e.getMessage());
            } else {
                throw new RESTConnectionFailureException(urlString, "Status code " + e.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RESTConnectionFailureException(urlString, e.getMessage());
        }
    }


    /**
     * POST /players
     * Registers a new Player
     *
     * @param playerRegistryDto
     * @return
     * @throws UnexpectedRESTException
     * @throws RESTConnectionFailureException
     * @throws RESTRequestDeniedException
     */
    public PlayerRegistryDto registerNewPlayer(PlayerRegistryDto playerRegistryDto)
            throws UnexpectedRESTException, RESTConnectionFailureException, RESTRequestDeniedException {
        PlayerRegistryDto returnedPlayerRegistryDto = null;
        String urlString = gameServiceUrlString + "/players";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(playerRegistryDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(json, headers);
            returnedPlayerRegistryDto =
                    restTemplate.postForObject(urlString, request, PlayerRegistryDto.class);
        } catch (JsonProcessingException e) {
            throw new UnexpectedRESTException(
                    "Unexpected error converting playerRegistryDto to JSON: " + playerRegistryDto);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                // this is a business logic problem - so let the application service handle this
                throw new RESTRequestDeniedException("Player " + playerRegistryDto + " already registered");
            } else {
                throw new RESTConnectionFailureException(urlString, "Status code " + e.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RESTConnectionFailureException("/players", e.getMessage());
        }
        logger.info("Registered player via REST, got bearer token: " + returnedPlayerRegistryDto.getBearerToken());
        return returnedPlayerRegistryDto;
    }

    /**
     * GET /players
     * Retrieves Info for a already created player
     *
     * @param playerRegistryDto playerDTO to request info for
     * @return PlayerRegistryDTO if successful
     * @throws RESTConnectionFailureException
     */
    public PlayerRegistryDto getPlayerDetails(PlayerRegistryDto playerRegistryDto)
            throws RESTConnectionFailureException {
        PlayerRegistryDto returnedPlayerRegistryDto;
        URI targetURL = UriComponentsBuilder.fromUriString(gameServiceUrlString)
                .path("/players")
                .queryParam("name", playerRegistryDto.getName())
                .queryParam("mail", playerRegistryDto.getEmail())
                .build()
                .encode()
                .toUri();
        logger.info("Fetching URI: " + targetURL);
        try {
            returnedPlayerRegistryDto = restTemplate.getForObject(targetURL, PlayerRegistryDto.class);
            if (returnedPlayerRegistryDto == null)
                throw new UnexpectedRESTException("Received a no data for Player " + playerRegistryDto.getName() + " with mail " + playerRegistryDto.getEmail());
        } catch (HttpClientErrorException e) {
            throw new RESTConnectionFailureException(targetURL.toString(), "Status code " + e.getStatusCode());
        } catch (RestClientException | UnexpectedRESTException e) {
            throw new RESTConnectionFailureException("/players", e.getMessage());
        }
        logger.info("Got Information for player" + returnedPlayerRegistryDto.getName() + "! Bearer token is: " + returnedPlayerRegistryDto.getBearerToken());
        return returnedPlayerRegistryDto;
    }

    /**
     * POST /commands
     * Registers a new Player
     *
     * @param commandDto
     * @return
     * @throws UnexpectedRESTException
     * @throws RESTConnectionFailureException
     */
    public CommandDto sendCommand(CommandDto commandDto)
            throws UnexpectedRESTException, RESTConnectionFailureException {
        CommandDto returnedCommandDto = null;
        String urlString = gameServiceUrlString + "/commands";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(commandDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(json, headers);
            returnedCommandDto =
                    restTemplate.postForObject(urlString, request, CommandDto.class);
        } catch (JsonProcessingException e) {
            throw new UnexpectedRESTException(
                    "Unexpected error converting commandDto to JSON: " + commandDto);
        } catch (HttpClientErrorException e) {
            throw new RESTConnectionFailureException(urlString, "Status code " + e.getStatusCode());
        } catch (RestClientException e) {
            throw new RESTConnectionFailureException("/commands", e.getMessage());
        }
        logger.info("Successfully send command of type: " + commandDto.getCommandType());
        return returnedCommandDto;
    }

}
