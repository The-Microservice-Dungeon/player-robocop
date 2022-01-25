package thkoeln.dungeon.eventconsumer.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.CommandDispatcherService;
import thkoeln.dungeon.eventconsumer.core.KafkaErrorService;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.round.RoundStatus;

@Service
public class GameEventConsumer {
    private final GameApplicationService gameApplicationService;
    private final GameStatusEventRepository gameStatusEventRepository;
    private final KafkaErrorService kafkaErrorService;
    private final RoundStatusEventRepository roundStatusEventRepository;
    private final Logger logger = LoggerFactory.getLogger(GameEventConsumer.class);
    private final SimpMessagingTemplate template;
    private final CommandDispatcherService commandDispatcherService;

    @Autowired
    public GameEventConsumer(GameApplicationService gameApplicationService,
                             GameStatusEventRepository gameStatusEventRepository,
                             KafkaErrorService kafkaErrorService,
                             RoundStatusEventRepository roundStatusEventRepository,
                             SimpMessagingTemplate template,
                             CommandDispatcherService commandDispatcherService) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.kafkaErrorService = kafkaErrorService;
        this.roundStatusEventRepository = roundStatusEventRepository;
        this.template = template;
        this.commandDispatcherService = commandDispatcherService;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener(topics = "status")  // that is what the documentation says
    public void consumeGameStatusEvent(@Header String eventId,
                                        @Header String timestamp,
                                        @Header String transactionId,
                                        @Payload String payload) {
        try {
            GameStatusEvent gameStatusEvent = new GameStatusEvent(eventId, timestamp, transactionId, payload);
            gameStatusEventRepository.save(gameStatusEvent);
            logger.info("saved game event with status " + gameStatusEvent.getStatus().toString());
            gameApplicationService.gameStatusExternallyChanged(gameStatusEvent.getGameId(), gameStatusEvent.getStatus());
            commandDispatcherService.init();
            this.template.convertAndSend("game_events", "game_status_change");
        } catch (KafkaException e) {
            this.kafkaErrorService.newKafkaError("status", payload, e.getMessage());
        }
    }

    @KafkaListener( topics = "roundStatus" )  // that is what the documentation says
    public void consumeRoundStatusEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload) {
        try {
            RoundStatusEvent roundStatusEvent = new RoundStatusEvent(eventId, timestamp, transactionId, payload);
            roundStatusEventRepository.save(roundStatusEvent);
            logger.info("saved round event with status "+roundStatusEvent.getRoundStatus().toString());
            gameApplicationService.roundStatusExternallyChanged(roundStatusEvent.getEventId(), roundStatusEvent.getRoundNumber(), roundStatusEvent.getRoundStatus());
            if (roundStatusEvent.getRoundNumber() == 1 && roundStatusEvent.getRoundStatus()== RoundStatus.STARTED){
                commandDispatcherService.buyRobot();
            }
            this.template.convertAndSend("game_events", "round_status_change");
        } catch (KafkaException e) {
            this.kafkaErrorService.newKafkaError("roundStatus", payload, e.getMessage());
        }
    }
}
