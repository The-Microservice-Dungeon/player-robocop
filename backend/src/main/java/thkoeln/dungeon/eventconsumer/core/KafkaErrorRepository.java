package thkoeln.dungeon.eventconsumer.core;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface KafkaErrorRepository extends CrudRepository<KafkaError, UUID> {
}
