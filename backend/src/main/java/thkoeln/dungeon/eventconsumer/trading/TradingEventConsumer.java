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
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.player.domain.PlayerRepository;

@Service
public class TradingEventConsumer {
    private PlayerApplicationService playerApplicationService;
    private PlayerRepository playerRepository;
    private BankCreatedEventRepository bankCreatedEventRepository;
    private final Logger logger = LoggerFactory.getLogger(TradingEventConsumer.class);

    @Autowired
    public TradingEventConsumer(PlayerApplicationService playerApplicationService, BankCreatedEventRepository bankCreatedEventRepository) {
        this.playerApplicationService = playerApplicationService;
        this.bankCreatedEventRepository = bankCreatedEventRepository;
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
        playerApplicationService.changeMoneyOfPlayer(bankCreatedEvent.getPlayerId(), bankCreatedEvent.getMoney());
    }


}
