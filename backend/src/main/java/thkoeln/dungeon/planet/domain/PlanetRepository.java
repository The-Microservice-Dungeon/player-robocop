package thkoeln.dungeon.planet.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanetRepository extends CrudRepository<Planet, UUID> {
    List<Planet> findAll();
}
