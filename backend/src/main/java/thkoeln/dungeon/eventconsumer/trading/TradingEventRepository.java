package thkoeln.dungeon.eventconsumer.trading;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface TradingEventRepository extends CrudRepository<TradingEvent, UUID> {
    Optional<TradingEvent> findByTransactionId(UUID transactionId);
}
