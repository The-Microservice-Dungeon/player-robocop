package thkoeln.dungeon.restadapter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.DungeonPlayerException;

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


    public GameDto[] fetchCurrentGameState() {
        GameDto[] gameDtos = restTemplate.getForObject( gameServiceUrlString + "/games", GameDto[].class );
        if ( gameDtos == null ) throw new RestAdapterException( "Received a null GameDto array - wtf ...?" );
        logger.info( "Got " + gameDtos.length + " game(s) via REST ...");
        Iterator<GameDto> iterator = Arrays.stream(gameDtos).iterator();
        while ( iterator.hasNext() ) { logger.info( "... " + iterator.next() ); }
        return gameDtos;
    }


    public PlayerRegistryDto registerPlayer( PlayerRegistryDto playerRegistryDto ) {
        PlayerRegistryDto returnedPlayerRegistryDto = null;
        //restTemplate.put( gameServicePlayersEndpoint );
        logger.info( "Registered player via REST, got bearer token: " + returnedPlayerRegistryDto.getBearerToken() );
        return returnedPlayerRegistryDto;
    }
}
