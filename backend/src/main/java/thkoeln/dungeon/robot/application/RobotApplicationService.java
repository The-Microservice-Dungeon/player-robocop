package thkoeln.dungeon.robot.application;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.map.PositionVO;
import thkoeln.dungeon.robot.domain.Robot;
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

    /***
     * Set bot Position
     * @param bot
     * @param position
     */
    public void setRobotOnPosition(@NotNull Robot bot, PositionVO position){
            bot.setPosition(position);
            position.setRobot(bot);
            robotRepository.save(bot);
    }


}
