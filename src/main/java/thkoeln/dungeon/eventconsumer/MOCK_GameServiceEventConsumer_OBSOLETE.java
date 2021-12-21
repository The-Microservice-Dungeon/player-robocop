package thkoeln.dungeon.eventconsumer;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.game.application.GameApplicationService;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
@Profile( "mock" )
public class MOCK_GameServiceEventConsumer_OBSOLETE implements ApplicationListener<ContextRefreshedEvent> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private GameApplicationService gameApplicationService;
    private Logger logger = LoggerFactory.getLogger( MOCK_GameServiceEventConsumer_OBSOLETE.class );

    @Value( "${dungeon.mock.game.initialDelayUntilStart}" )
    private int initialDelayUntilStart;
    @Value( "${dungeon.mock.game.roundLength}" )
    private int roundLength;
    @Value( "${dungeon.mock.game.gameId}" )
    private UUID gameId;

    @Autowired
    public MOCK_GameServiceEventConsumer_OBSOLETE(GameApplicationService gameApplicationService ) {
        this.gameApplicationService = gameApplicationService;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info( "GameServiceMockEventConsumer started");

        // emulate the "game started" event after a delay. Otherwise, we assume the game has already started
        // before startup of this player - so in that case, there is no such event.
        if ( initialDelayUntilStart > 0 ) {
            TimeUnit.SECONDS.sleep( initialDelayUntilStart );
            gameApplicationService.gameExternallyStarted( gameId );
        }

        // ... and now the "newRound" event emulated in regular intervals
        final Runnable gameClock = new Runnable() {
            private Integer roundNumber = 0;
            public void run() {
                gameApplicationService.newRound( gameId, roundNumber );
                roundNumber++;
            }
        };
        final ScheduledFuture<?> gameClockHandle =
                scheduler.scheduleAtFixedRate( gameClock, roundLength, roundLength, SECONDS );
        scheduler.schedule(new Runnable() {
            public void run() { gameClockHandle.cancel(true); }
        }, 60 * 60, SECONDS);
    }
}
