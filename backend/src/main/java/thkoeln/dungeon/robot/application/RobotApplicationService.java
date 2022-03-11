package thkoeln.dungeon.robot.application;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.trading.TradingData;
import thkoeln.dungeon.map.PositionVO;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.util.List;
import java.util.Optional;
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

    public Robot createNewRobot (TradingData tradingData) {
        Robot newRobot = new Robot(tradingData.getRobotId());
        newRobot.setEnergy(tradingData.getEnergy());
        newRobot.setHealth(tradingData.getHealth());
        newRobot.setMaxEnergy(tradingData.getMaxEnergy());
        newRobot.setMaxHealth(tradingData.getMaxHealth());
        this.robotRepository.save(newRobot);
        return newRobot;
    }

    public void updateRobotEnergy(UUID robotId, Integer newEnergy){
        Optional<Robot> robotOptional = robotRepository.findById(robotId);
        if (robotOptional.isPresent()){
            Robot robot = robotOptional.get();
            robot.setEnergy(newEnergy);
            robotRepository.save(robot);
        }
        //else whoops
    }

    public void deleteRobots () {
        robotRepository.deleteAll();
    }

    public Robot getById (UUID id) {
        return this.robotRepository.findById(id).get();
    }

    public List<Robot> getAllRobots () {
        return this.robotRepository.findAll();
    }

    /***
     * Set bot Position
     * @param bot
     * @param position
     */
    public Robot setRobotPosition(Robot bot, PositionVO position){
            bot.setPosition(position);
            robotRepository.save(bot);
            return bot;
    }

}
