package thkoeln.dungeon.robot.application;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.map.PositionVO;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.util.UUID;

@Service
public class RobotApplicationService {
    private final RobotRepository robotRepository;

    @Autowired
    public RobotApplicationService (
            RobotRepository robotRepository
    ){
        this.robotRepository = robotRepository;
    }

    // TODO: Call on robot spawned event
    public Robot createNewRobot (UUID id) {
        Robot newRobot = new Robot(id);
        this.robotRepository.save(newRobot);
        return newRobot;
    }

    public Robot getById (UUID id) {
        return this.robotRepository.findById(id).get();
    }

    /***
     * Set bot Position
     * @param bot
     * @param position
     */
    public void setRobotPosition(@NotNull Robot bot, PositionVO position){
            bot.setPosition(position);
            position.setReferencingRobotId(bot.getRobotId());
            robotRepository.save(bot);
    }

}
