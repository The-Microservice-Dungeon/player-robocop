package thkoeln.dungeon.gameconnector.mock;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.gameconnector.PlayerCallback;
import thkoeln.dungeon.player.player.PlayerService;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.concurrent.TimeUnit;

@Component
@Profile( "mock" )
public class MockGameClock implements ApplicationRunner {
    @Value("${dungeon.mock.roundlength}")
    private int roundLength;

    @Autowired
    private PlayerService playerService;

    public void run(ApplicationArguments args) throws Exception {
        Integer roundNumber = 0;
        while( true ) {
            playerService.playRound( roundNumber );
            TimeUnit.SECONDS.sleep( roundLength );
            roundNumber++;
        }
    }


}
