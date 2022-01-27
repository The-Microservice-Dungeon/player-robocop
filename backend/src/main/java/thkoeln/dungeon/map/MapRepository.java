package thkoeln.dungeon.map;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MapRepository extends CrudRepository<Map, UUID> {
}
