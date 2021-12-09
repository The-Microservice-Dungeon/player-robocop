package thkoeln.dungeon.player.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerCreator implements ApplicationListener<ContextRefreshedEvent> {

    private PlayerRepository playerRepository;
    Logger logger = LoggerFactory.getLogger(PlayerCreator.class);

    @Value("${dungeon.numberOfPlayers}")
    private int numberOfPlayers;

    @Autowired
    public PlayerCreator(PlayerRepository playerRepository ) {
        this.playerRepository = playerRepository;
    }

    @Override
    /**
     * Creates a number of players to play concurrently
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for( int iPlayer = 0; iPlayer<numberOfPlayers; iPlayer++ ) {
            Player player = new Player();
            player.setName( "P" + iPlayer );
            playerRepository.save( player );
            logger.info( "Created player no. " + player.getName() );
        }
    }
}
