package thkoeln.dungeon.robot.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    List<Robot> findAll();
    List<Robot> findAllByRobotIdIn(List<UUID> robotIds);
}
