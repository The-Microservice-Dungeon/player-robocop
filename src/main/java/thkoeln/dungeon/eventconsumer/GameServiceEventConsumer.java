package thkoeln.dungeon.eventconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.game.application.GameApplicationService;

import java.util.UUID;

@Component
@Profile( "prod" )
public class GameServiceEventConsumer {
    private GameApplicationService gameApplicationService;

    @Autowired
    public GameServiceEventConsumer( GameApplicationService gameApplicationService ) {
        this.gameApplicationService = gameApplicationService;
    }

    public void consumeGameCreatedEvent() {
        // todo read + handle actual event
        UUID gameId = UUID.randomUUID();
        gameApplicationService.gameExternallyCreated( gameId );
    }

    public void consumeGameStartedEvent() {
        // todo
    }

    public void consumeGameEndedEvent() {
        // todo
    }

    public void consumeNewRoundStartedEvent() {
        // todo
    }
}
