package thkoeln.dungeon.eventconsumer.trading;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BankCreatedEventRepository extends CrudRepository<BankCreatedEvent, UUID> {
}
