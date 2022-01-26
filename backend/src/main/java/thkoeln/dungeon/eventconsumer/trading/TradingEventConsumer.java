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
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.PlayerRepository;

import java.util.Optional;

@Service
public class TradingEventConsumer {
    private final PlayerApplicationService playerApplicationService;
    private final CommandRepository commandRepository;
    private final BankCreatedEventRepository bankCreatedEventRepository;
    private final TradingEventRepository tradingEventRepository;
    private final Logger logger = LoggerFactory.getLogger(TradingEventConsumer.class);

    @Autowired
    public TradingEventConsumer(PlayerApplicationService playerApplicationService,
                                BankCreatedEventRepository bankCreatedEventRepository,
                                CommandRepository commandRepository,
                                TradingEventRepository tradingEventRepository) {
        this.playerApplicationService = playerApplicationService;
        this.bankCreatedEventRepository = bankCreatedEventRepository;
        this.commandRepository = commandRepository;
        this.tradingEventRepository = tradingEventRepository;
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
        logger.info("Saving trading event with money value = "+tradingEvent.getMoneyChangedBy());
        tradingEventRepository.save(tradingEvent);
        Optional<Command> commandOptional = commandRepository.findByTransactionId(tradingEvent.getTransactionId());
        commandOptional.ifPresent(command -> playerApplicationService.changeMoneyOfPlayer(
                command.getPlayer().getPlayerId(),
                tradingEvent.getMoneyChangedBy()));
    }
}
