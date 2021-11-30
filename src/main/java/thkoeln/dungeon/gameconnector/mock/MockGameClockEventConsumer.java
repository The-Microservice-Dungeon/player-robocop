package thkoeln.dungeon.gameconnector.mock;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Profile( "mock" )
public class MockGameClockEventConsumer implements ApplicationRunner {
    @Value("${dungeon.mock.roundlength}")
    private int roundLength;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while( true ) {
            System.out.println( "played" );
            TimeUnit.SECONDS.sleep( roundLength );
        }
    }
}
