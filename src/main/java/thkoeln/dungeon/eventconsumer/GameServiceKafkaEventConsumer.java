package thkoeln.dungeon.eventconsumer;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile( "prod" )
public class GameServiceKafkaEventConsumer {


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
