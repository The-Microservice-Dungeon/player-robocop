package thkoeln.dungeon.gameconnector.localkafka;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile( "localKafka" )
public class LocalKafkaEventConsumer {

    public void listenToAnswers() {

    }

    public void listenToOtherMoveEvents() {

    }

}
