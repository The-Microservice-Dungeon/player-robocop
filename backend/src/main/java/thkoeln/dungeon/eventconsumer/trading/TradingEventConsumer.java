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
import thkoeln.dungeon.game.domain.game.GameException;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.PlayerRepository;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.Optional;

@Service
public class TradingEventConsumer {
    private final PlayerApplicationService playerApplicationService;
    private final CommandRepository commandRepository;
    private final BankCreatedEventRepository bankCreatedEventRepository;
    private final TradingEventRepository tradingEventRepository;
    private final PlanetApplicationService planetApplicationService;
    private final RobotApplicationService robotApplicationService;
    private final MapApplicationService mapApplicationService;
    private final Logger logger = LoggerFactory.getLogger(TradingEventConsumer.class);

    @Autowired
    public TradingEventConsumer(PlayerApplicationService playerApplicationService,
                                PlanetApplicationService planetApplicationService,
                                BankCreatedEventRepository bankCreatedEventRepository,
                                CommandRepository commandRepository,
                                TradingEventRepository tradingEventRepository,
                                RobotApplicationService robotApplicationService,
                                MapApplicationService mapApplicationService) {
        this.playerApplicationService = playerApplicationService;
        this.bankCreatedEventRepository = bankCreatedEventRepository;
        this.commandRepository = commandRepository;
        this.planetApplicationService = planetApplicationService;
        this.tradingEventRepository = tradingEventRepository;
        this.robotApplicationService = robotApplicationService;
        this.mapApplicationService = mapApplicationService;
    }

    @KafkaListener(topics = "bank-created")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 500))
    public void consumeBankCreatedEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                        @Payload String payload){
        logger.info("Consuming bankCreatedEvent with eventId:"+eventId);
        logger.info("Payload: "+payload);
        BankCreatedEvent bankCreatedEvent = new BankCreatedEvent()
                .fillWithPayload(payload)
                .fillHeader(eventId,timestamp,transactionId);
        logger.info("Saving bankCreatedEvent with money value = "+bankCreatedEvent.getMoney());
        bankCreatedEventRepository.save(bankCreatedEvent);
        playerApplicationService.setMoneyOfPlayer(bankCreatedEvent.getPlayerId(), bankCreatedEvent.getMoney());
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
        commandOptional.ifPresent(command -> {
            logger.info("Saving trading event with money value = "+tradingEvent.getMoneyChangedBy());
            tradingEventRepository.save(tradingEvent);
            playerApplicationService.changeMoneyOfPlayer(
                command.getPlayer().getPlayerId(),
                tradingEvent.getMoneyChangedBy());

        });
        if (tradingEvent.getData()!=null && tradingEvent.getData().getPlanet()!=null){
            Robot robot = this.robotApplicationService.createNewRobot(tradingEvent.getData().getRobotId());
            this.mapApplicationService.handleNewRobotSpawn(robot, tradingEvent.getData().getPlanet());
        }
    }
}
