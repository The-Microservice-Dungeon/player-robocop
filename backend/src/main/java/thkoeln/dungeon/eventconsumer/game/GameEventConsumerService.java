package thkoeln.dungeon.eventconsumer.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.game.application.GameApplicationService;

@Service
public class GameEventConsumerService {
    private final GameApplicationService gameApplicationService;
    private final GameStatusEventRepository gameStatusEventRepository;

    @Autowired
    public GameEventConsumerService(GameApplicationService gameApplicationService,
                                    GameStatusEventRepository gameStatusEventRepository) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     * --- this extra line for testing purposes ---
     */
    //@KafkaListener( topics = "status" )  // that is what the documentation says
    public void consumeGameStatusEvent(@Payload GameStatusEventPayload gameStatusEventPayload, MessageHeaders headers) {
        GameStatusEvent gameStatusEvent = new GameStatusEvent(headers, gameStatusEventPayload);
        gameStatusEventRepository.save(gameStatusEvent);
        gameApplicationService.gameStatusExternallyChanged(gameStatusEvent.getGameId(), gameStatusEvent.getGameStatus());
    }


    public void consumeNewRoundStartedEvent() {
        // todo
    }
}
