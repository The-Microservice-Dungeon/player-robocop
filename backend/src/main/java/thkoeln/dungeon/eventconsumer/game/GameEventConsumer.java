package thkoeln.dungeon.eventconsumer.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.core.KafkaErrorService;
import thkoeln.dungeon.game.application.GameApplicationService;
import com.google.gson.Gson;

@Service
public class GameEventConsumer {
    private final GameApplicationService gameApplicationService;
    private final GameStatusEventRepository gameStatusEventRepository;
    private final KafkaErrorService kafkaErrorService;
    private final Logger logger = LoggerFactory.getLogger(GameEventConsumer.class);

    @Autowired
    public GameEventConsumer(GameApplicationService gameApplicationService,
                             GameStatusEventRepository gameStatusEventRepository, KafkaErrorService kafkaErrorService) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.kafkaErrorService = kafkaErrorService;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener( topics = "status" )  // that is what the documentation says
    public void consumeRoundStatusEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload) {
        try {
            GameStatusEvent gameStatusEvent = new GameStatusEvent(eventId, timestamp, transactionId, payload);
            gameStatusEventRepository.save(gameStatusEvent);
            logger.info("saved game event with status "+gameStatusEvent.getGameStatus().toString());
            gameApplicationService.gameStatusExternallyChanged(gameStatusEvent.getGameId(), gameStatusEvent.getGameStatus());
        } catch (Exception e) {
            this.kafkaErrorService.newKafkaError("status", payload, e.getMessage());
        }
    }
    //TODO: roundStartedEventListener
}
