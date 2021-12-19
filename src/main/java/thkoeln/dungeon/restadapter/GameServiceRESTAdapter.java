package thkoeln.dungeon.restadapter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Iterator;

@Component
public class GameServiceRESTAdapter {

    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameServiceRESTAdapter.class );
    @Value("${GAME_SERVICE}")
    private String gameServiceUrlString;

    @Autowired
    public GameServiceRESTAdapter(RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }


    // TODO - Retry doesn't work yet
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public GameDto[] fetchCurrentGameState() {
        GameDto[] gameDtos = new GameDto[0];
        try {
            gameDtos = restTemplate.getForObject( gameServiceUrlString + "/games", GameDto[].class );
            if ( gameDtos == null ) throw new UnexpectedRestAdapterException( "Received a null GameDto array - wtf ...?" );
            logger.info( "Got " + gameDtos.length + " game(s) via REST ...");
            Iterator<GameDto> iterator = Arrays.stream(gameDtos).iterator();
            while ( iterator.hasNext() ) { logger.info( "... " + iterator.next() ); }
        }
        catch ( RestClientException e ) {
            logger.warn( "Unexpected error in communication with GameService calling /games endpoint. Message: "
                    + e.getMessage() + "\nWill retry later." );
        }
        return gameDtos;
    }

    // TODO - Retry doesn't work yet
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public PlayerRegistryDto registerPlayer( PlayerRegistryDto playerRegistryDto ) {
        PlayerRegistryDto returnedPlayerRegistryDto = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(playerRegistryDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            HttpEntity<String> request = new HttpEntity<String>( json, headers);
            returnedPlayerRegistryDto =
                     restTemplate.postForObject(gameServiceUrlString + "/players", request, PlayerRegistryDto.class);
        }
        catch ( JsonProcessingException e ) {
            logger.error( "Unexpected error converting playerRegistryDto to JSON: " + playerRegistryDto );
            return null;
        }
        catch ( HttpClientErrorException e ) {
            if (e.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE)) {
                // this is a business logic problem - so let the application service handle this
                throw new PlayerAlreadyRegisteredException("Player " + playerRegistryDto + " already registered");
            }
        }
        catch ( RestClientException e ) {
            logger.warn( "Unexpected error in communication with GameService calling /players endpoint. Message: "
                    + e.getMessage() + "\nWill retry later." );
            return null;
        }
        logger.info( "Registered player via REST, got bearer token: " + returnedPlayerRegistryDto.getBearerToken() );
        return returnedPlayerRegistryDto;
    }
}
