package thkoeln.dungeon.game.mock;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.PlayerServiceCallback;
import thkoeln.dungeon.player.PlayerService;

@Component
@Profile( "mock" )
public class MockGameClockService {
    @Value("${dungeon.player.mock.roundlength}")
    private int roundLength;

    @Autowired
    private PlayerService playerService;

    /*
    public void run(ApplicationArguments args) throws Exception {
        Integer roundNumber = 0;
        while( true ) {
            playerService.playRound( roundNumber );
            TimeUnit.SECONDS.sleep( roundLength );
            roundNumber++;
        }
    }
*/
    public void gameStarted( PlayerServiceCallback playerServiceCallback ) {

    }

}
