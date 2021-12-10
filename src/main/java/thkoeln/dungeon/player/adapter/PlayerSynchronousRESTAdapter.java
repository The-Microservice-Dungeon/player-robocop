package thkoeln.dungeon.player.adapter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.application.PlayerRegistryDto;

@Component
@Profile( "prod" )
public class PlayerSynchronousRESTAdapter implements PlayerSynchronousAdapter {

    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( PlayerSynchronousRESTAdapter.class );
    @Value("${GAME_SERVICE}" + "/players" )
    private String gameServicePlayersEndpoint;

    @Autowired
    public PlayerSynchronousRESTAdapter(RestTemplateBuilder builder ) {
        this.restTemplate = builder.build();
    }

    @Override
    public PlayerRegistryDto registerPlayer( PlayerRegistryDto playerRegistryDto ) {
        PlayerRegistryDto returnedPlayerRegistryDto = null;
                //restTemplate.put( gameServicePlayersEndpoint );
        logger.info( "Registered player via REST, got bearer token: " + returnedPlayerRegistryDto.getBearerToken() );
        return returnedPlayerRegistryDto;
    }
}
