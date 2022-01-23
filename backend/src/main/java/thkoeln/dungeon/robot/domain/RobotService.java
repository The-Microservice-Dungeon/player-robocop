package thkoeln.dungeon.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RobotService {
    private final RobotRepository robotRepository;
    @Autowired
    public RobotService(
            RobotRepository robotRepository
    ){
        this.robotRepository = robotRepository;
    }

    public void moveRobot(UUID planetId, UUID robotId){
        //todo
    }
}
