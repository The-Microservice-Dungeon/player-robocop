package thkoeln.dungeon.robot.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.robot.domain.RobotRepository;

@Service
public class RobotApplicationService {
    private final RobotRepository robotRepository;

    @Autowired
    public RobotApplicationService (
            RobotRepository robotRepository
    ){
        this.robotRepository = robotRepository;
    }

    // implement setRobotPosition with repo save etc
}
