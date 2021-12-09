package thkoeln.dungeon.game.adapter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.game.domain.Game;

@Component
@Profile( "prod" )
public class GameServiceSynchronousRESTAdapter implements GameServiceSynchronousAdapter {

    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameServiceSynchronousRESTAdapter.class );
    @Value("${GAME_SERVICE}")
    private String gameServiceUrlString;

    @Autowired
    public GameServiceSynchronousRESTAdapter(RestTemplateBuilder builder ) {
        this.restTemplate = builder.build();
    }

    @Override
    public Game fetchCurrentGameState() {
        Game game = restTemplate.getForObject(gameServiceUrlString, Game.class);
        logger.info( "Got game via REST: " + String.valueOf( game ) );
        return game;
    }
}
