package thkoeln.dungeon.game;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile( "prod" )
public class GameClockService implements GameClock, ApplicationListener<ContextRefreshedEvent> {

    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger( GameClockService.class );

    @Autowired
    public GameClockService( RestTemplateBuilder builder ) {
        this.restTemplate = builder.build();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        hookupToCurrentGameAtStartup();
    }

    @Override
    public void hookupToCurrentGameAtStartup() {
        Game game = restTemplate.getForObject( "https://api.predic8.de/shop/products/", Game.class );
        logger.info( "Got game via REST: " + String.valueOf( game ) );

        // check if there is already a player (or several, in the test scenario). If not, create & register.
    }

    public void fetchCurrentGameState() {

    }
}
