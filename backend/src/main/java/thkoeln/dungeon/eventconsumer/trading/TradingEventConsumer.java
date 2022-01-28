package thkoeln.dungeon.eventconsumer.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.Command;
import thkoeln.dungeon.command.CommandRepository;
import thkoeln.dungeon.eventconsumer.robot.NeighbourEvent;
import thkoeln.dungeon.game.domain.game.GameException;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.application.PlayerRegistryException;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;

import javax.swing.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class TradingEventConsumer {
    private final PlayerApplicationService playerApplicationService;
    private final CommandRepository commandRepository;
    private final BankCreatedEventRepository bankCreatedEventRepository;
    private final TradingEventRepository tradingEventRepository;
    private final RobotApplicationService robotApplicationService;
    private final MapApplicationService mapApplicationService;
    private final Logger logger = LoggerFactory.getLogger(TradingEventConsumer.class);

    @Autowired
    public TradingEventConsumer(PlayerApplicationService playerApplicationService,
                                BankCreatedEventRepository bankCreatedEventRepository,
                                CommandRepository commandRepository,
                                TradingEventRepository tradingEventRepository,
                                RobotApplicationService robotApplicationService,
                                MapApplicationService mapApplicationService) {
        this.playerApplicationService = playerApplicationService;
        this.bankCreatedEventRepository = bankCreatedEventRepository;
        this.commandRepository = commandRepository;
        this.tradingEventRepository = tradingEventRepository;
        this.robotApplicationService = robotApplicationService;
        this.mapApplicationService = mapApplicationService;
    }

    @KafkaListener(topics = "bank-created")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 500))
    public void consumeBankCreatedEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload){

        logger.info("Bank Created event " + eventId + " received. Consuming in 300ms");

        Timer timer = new Timer(300, arg0 -> {
            logger.info("Consuming bankCreatedEvent " + eventId + " Payload: " + payload);
            BankCreatedEvent bankCreatedEvent = new BankCreatedEvent()
                    .fillWithPayload(payload)
                    .fillHeader(eventId,timestamp,transactionId);

            UUID playerId = bankCreatedEvent.getPlayerId();
            if (playerApplicationService.bankEventRelevantForUs(playerId)) {
                logger.info("Bank Created Event is for us. Consuming!");
                logger.info("Saving bankCreatedEvent: " + payload);

                bankCreatedEventRepository.save(bankCreatedEvent);
                playerApplicationService.setMoneyOfPlayer(playerId, bankCreatedEvent.getMoney());
            } else {
                String errorMessage = "Bank Created Event for Player with playerId "+ playerId +" is not for us.";
                logger.error(errorMessage);
                throw new PlayerRegistryException(errorMessage);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    @KafkaListener(topics = "trades")
    public void consumeTradingEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                    @Payload String payload){
        logger.info("Consuming trades event with eventId "+eventId);
        logger.info("Payload: "+payload);
        TradingEvent tradingEvent = new TradingEvent()
                .fillWithPayload(payload)
                .fillHeader(eventId,timestamp,transactionId);


        if (!tradingEvent.getSuccess()) {
            logger.error("Received unsuccessful response to trading command! Payload: " + payload);
            return;
        }

        //This checks our command repo, if we issued this command, then we can save + process this.
        Optional<Command> commandOptional = commandRepository.findByTransactionId(tradingEvent.getTransactionId());
        if (commandOptional.isPresent()) {
            Command command = commandOptional.get();

            logger.info("Saving trading event with money value = "+tradingEvent.getMoneyChangedBy());
            tradingEventRepository.save(tradingEvent);
            playerApplicationService.changeMoneyOfPlayer(
                    command.getPlayer().getPlayerId(),
                    tradingEvent.getMoneyChangedBy());

            // If this was a robot spawn event we handle it accordingly
            if (tradingEvent.getData()!=null && tradingEvent.getData().getPlanet()!=null){
                Robot robot = this.robotApplicationService.createNewRobot(tradingEvent.getData().getRobotId());
                this.mapApplicationService.handleNewRobotSpawn(robot, tradingEvent.getData().getPlanet());
            }
        }
    }
}
