package thkoeln.dungeon.eventconsumer.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.GameLogic;
import thkoeln.dungeon.command.CommandDispatcherService;
import thkoeln.dungeon.eventconsumer.core.KafkaErrorService;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.application.GameStatusException;
import thkoeln.dungeon.game.application.NoGameAvailableException;
import thkoeln.dungeon.game.domain.game.GameStatus;
import thkoeln.dungeon.game.domain.round.RoundStatus;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.application.PlayerRegistryException;

import javax.swing.*;

@Service
public class GameEventConsumer {
    private final GameApplicationService gameApplicationService;
    private final GameStatusEventRepository gameStatusEventRepository;
    private final KafkaErrorService kafkaErrorService;
    private final RoundStatusEventRepository roundStatusEventRepository;
    private final Logger logger = LoggerFactory.getLogger(GameEventConsumer.class);
    private final PlayerApplicationService playerApplicationService;
    private final CommandDispatcherService commandDispatcherService;
    private final PlayerStatusEventRepository playerStatusEventRepository;
    private final GameLogic gameLogic;

    @Autowired
    public GameEventConsumer(GameApplicationService gameApplicationService,
                             GameStatusEventRepository gameStatusEventRepository,
                             KafkaErrorService kafkaErrorService,
                             RoundStatusEventRepository roundStatusEventRepository,
                             CommandDispatcherService commandDispatcherService,
                             PlayerApplicationService playerApplicationService,
                             PlayerStatusEventRepository playerStatusEventRepository, GameLogic gameLogic) {
        this.gameApplicationService = gameApplicationService;
        this.gameStatusEventRepository = gameStatusEventRepository;
        this.kafkaErrorService = kafkaErrorService;
        this.roundStatusEventRepository = roundStatusEventRepository;
        this.commandDispatcherService = commandDispatcherService;
        this.playerApplicationService = playerApplicationService;
        this.playerStatusEventRepository = playerStatusEventRepository;
        this.gameLogic = gameLogic;
    }


    /**
     * "Status changed" event published by GameService, esp. after a game has been created, started, or finished
     */
    @KafkaListener(topics = "status")  // that is what the documentation says
    public void consumeGameStatusEvent(@Header String eventId,
                                        @Header String timestamp,
                                        @Header String transactionId,
                                        @Payload String payloadStr) {
        try {
            GameStatusEvent gameStatusEvent = new GameStatusEvent()
                    .fillHeader(eventId, timestamp, transactionId)
                    .fillWithPayload(payloadStr);
            gameStatusEventRepository.save(gameStatusEvent);
            logger.info("saved game event with status " + gameStatusEvent.getStatus().toString());
            switch (gameStatusEvent.getStatus()){
                case CREATED ->{
                    gameApplicationService.gameStatusExternallyChanged(gameStatusEvent.getGameId(), GameStatus.CREATED);
                    try {
                        commandDispatcherService.init();
                    }catch (GameStatusException e){
                        logger.error("Game Status error: "+ e.getMessage());
                    }catch (NoGameAvailableException e){
                        logger.warn("No game in DB, retrying once in 300ms");
                        try {
                            Thread.sleep(300);
                            commandDispatcherService.init();
                        }catch (Exception finalException){
                            logger.error("Something still went wrong: "+e.getMessage());
                        }
                    }
                }
                case STARTED, ENDED -> gameApplicationService.gameStatusExternallyChanged(gameStatusEvent.getGameId(), gameStatusEvent.getStatus());
            }
        } catch (KafkaException e) {
            this.kafkaErrorService.newKafkaError("status", payloadStr, e.getMessage());
        }
    }

    @KafkaListener( topics = "roundStatus" )
    public void consumeRoundStatusEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payloadStr) {
        try {
            RoundStatusEvent roundStatusEvent = new RoundStatusEvent()
                    .fillWithPayload(payloadStr)
                    .fillHeader(eventId, timestamp, transactionId);
            roundStatusEventRepository.save(roundStatusEvent);
            logger.info("saved round event with status "+roundStatusEvent.getRoundStatus().toString());
            gameApplicationService.roundStatusExternallyChanged(roundStatusEvent.getEventId(), roundStatusEvent.getRoundNumber(), roundStatusEvent.getRoundStatus());
            if (roundStatusEvent.getRoundStatus()== RoundStatus.STARTED) {
                Timer timer = new Timer(300, arg0 -> {
                    logger.info("Playing round " + roundStatusEvent.getRoundNumber() + " now");
                    gameLogic.playRound();
                });
                timer.setRepeats(false);
                timer.start();
            }
        } catch (KafkaException e) {
            this.kafkaErrorService.newKafkaError("roundStatus", payloadStr, e.getMessage());
        }
    }

    @KafkaListener( topics = "playerStatus" )
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 500))
    public void consumePlayerStatusEvent( @Header String eventId, @Header String timestamp, @Header String transactionId,
                                          @Payload String payload ) {
        logger.info("Consuming Player Status event with payload: " + payload);
        PlayerStatusEvent playerStatusEvent = new PlayerStatusEvent()
                .fillWithPayload( payload )
                .fillHeader( eventId, timestamp, transactionId );

        if ( playerStatusEvent.isValid() ) {
            logger.info("Player Status Event was valid, processing");

            if (playerApplicationService.registrationEventRelevantForUs(playerStatusEvent.getName())) {
                logger.info("Player Status Event is for us. Consuming!");
                logger.info("Saving playerStatusEvent: " + payload);
                playerStatusEventRepository.save( playerStatusEvent );
                playerApplicationService.handlePlayerRegistrationEvent(playerStatusEvent.getTransactionId(), playerStatusEvent.getPlayerId());
            } else {
                String errorMessage = "This event is not for us! It is for " + playerStatusEvent.getName();

                logger.error(errorMessage);
                throw new PlayerRegistryException(errorMessage);
            }
        }
        else {
            logger.warn( "Caught invalid PlayerStatusEvent " + playerStatusEvent );
        }
    }


}
