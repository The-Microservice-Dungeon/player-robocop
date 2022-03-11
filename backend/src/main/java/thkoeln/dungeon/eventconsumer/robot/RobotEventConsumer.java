package thkoeln.dungeon.eventconsumer.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.Command;
import thkoeln.dungeon.command.CommandRepository;
import thkoeln.dungeon.eventconsumer.trading.TradingEvent;
import thkoeln.dungeon.eventconsumer.trading.TradingEventRepository;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import javax.swing.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class RobotEventConsumer {
    private final RobotApplicationService robotApplicationService;
    private final CommandRepository commandRepository;
    private final PlanetRepository planetRepository;
    private final MovementEventRepository movementEventRepository;
    private final NeighboursEventRepository neighboursEventRepository;
    private final PlanetApplicationService planetApplicationService;
    private final MapApplicationService mapApplicationService;
    private final SpawnEventRepository spawnEventRepository;
    private final TradingEventRepository tradingEventRepository;
    private final Logger logger = LoggerFactory.getLogger(RobotEventConsumer.class);


    @Autowired
    public RobotEventConsumer(RobotApplicationService robotApplicationService, SpawnEventRepository spawnEventRepository, MapApplicationService mapApplicationService, PlanetApplicationService planetApplicationService, RobotRepository robotRepository, CommandRepository commandRepository, PlanetRepository planetRepository, MovementEventRepository movementEventRepository, NeighboursEventRepository neighboursEventRepository, TradingEventRepository tradingEventRepository) {
        this.robotApplicationService = robotApplicationService;
        this.commandRepository = commandRepository;
        this.planetRepository = planetRepository;
        this.movementEventRepository = movementEventRepository;
        this.neighboursEventRepository = neighboursEventRepository;
        this.planetApplicationService = planetApplicationService;
        this.mapApplicationService = mapApplicationService;
        this.spawnEventRepository = spawnEventRepository;
        this.tradingEventRepository = tradingEventRepository;
    }

    // THIS DOES NOT HAVE A TRANSACTION ID. AAAAAAAAAAAAAAAAAAAAAAAH
    // "SAKE OF OBSCURITY?????????????????"
    @KafkaListener(topics = "movement")
    public void consumeMovementEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                     @Payload String payload){
        try {
            logger.info("Consuming movement Event");
            MovementEvent movementEvent = new MovementEvent()
                    .fillWithPayload(payload)
                    .fillHeader(eventId,timestamp,transactionId);
            /*
            List<Robot> affectedRobots = robotRepository.findAllByRobotIdIn(movementEvent.getRobots());
            if (affectedRobots.isEmpty()) {
                throw new GameException("This movement event does not match any of our robots!");
            }
            movementEventRepository.save(movementEvent);
            Optional<Planet> targetPlanet = planetRepository.findById(movementEvent.getPlanet().getPlanetId());
            if (targetPlanet.isPresent()){
                planetApplicationService.fillPlanetInformation(targetPlanet.get(),movementEvent.getPlanet());
                for (Robot robot: affectedRobots){
                    mapApplicationService.updateRobotPosition(robot, targetPlanet.get());
                }
            }else {
                logger.error("Planet for movement not found.");
            }
             */
            Optional<Command> found = commandRepository.findByTransactionId(movementEvent.getTransactionId());
            if (found.isPresent()){
                logger.info("Saving movement event with transaction id "+transactionId);
                movementEventRepository.save(movementEvent);
                Robot originRobot = found.get().getRobot();
                UUID targetPlanetId = movementEvent.getPlanet().getPlanetId();
                Optional<Planet> targetPlanetOptional = planetRepository.findById(targetPlanetId);
                if (targetPlanetOptional.isPresent()) {
                    logger.info("Robot with ID " + originRobot.getRobotId() + " moved to planet with ID " + targetPlanetId);
                    planetApplicationService.fillPlanetInformation(targetPlanetOptional.get(),movementEvent.getPlanet());
                    robotApplicationService.updateRobotEnergy(originRobot.getRobotId(),movementEvent.getRemainingEnergy());
                    mapApplicationService.updateRobotPosition(originRobot, targetPlanetOptional.get());
                }
            }
            else {
                logger.info("Movement event isn't relevant. Skipping.");
            }

        } catch (Exception e) {
            logger.error("Can't consume Movement Event. " + e.getMessage());
        }
    }

    @KafkaListener(topics = "neighbours")
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 100))
    public void consumeNeighbourEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                      @Payload String payload){

            logger.info("Consuming neighbour event");
            NeighbourEvent neighboursEvent = new NeighbourEvent()
                    .fillWithPayload(payload)
                    .fillHeader(eventId,timestamp,transactionId);
            // If this was triggered by us, we save and process
            Optional<Command> triggeringCommandOptional = commandRepository.findByTransactionId(neighboursEvent.getTransactionId());
            if (triggeringCommandOptional.isPresent()){
                logger.info("Neighbour event with payload " + payload + " was for us. Consuming in 300ms");

                Timer timer = new Timer(300, arg0 -> {
                    logger.info("Consuming own neighbour event with payload:" +payload);

                    neighboursEventRepository.save(neighboursEvent);
                    Command command = triggeringCommandOptional.get();
                    //movement command triggered this
                    switch (command.getCommandType()){
                        case movement ->  mapApplicationService.handleNewPlanetNeighbours(
                                command.getTargetPlanet(), neighboursEvent.getNeighbours());
                        case buying -> {
                            //match transaction ID with trading event and get planet by trading id
                            Optional<TradingEvent> tradingEvent = tradingEventRepository.findByTransactionId(neighboursEvent.getTransactionId());
                            if (tradingEvent.isPresent()){
                                UUID planetId = tradingEvent.get().getData().getPlanet();
                                Optional<Planet> planetOptional = planetRepository.findById(planetId);
                                planetOptional.ifPresent(planet -> mapApplicationService.handleNewPlanetNeighbours(planet, neighboursEvent.getNeighbours()));
                            }
                        }
                    }
                });

                timer.setRepeats(false);
                timer.start();
            }
    }

    @KafkaListener(topics = "spawn-notification")
    public void consumeSpawnEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                  @Payload String payload){
        logger.info("Consuming spawn event");
        SpawnEvent spawnEvent = new SpawnEvent()
                .fillWithPayload(payload)
                .fillHeader(eventId,timestamp,transactionId);
        Optional<Command> found = commandRepository.findByTransactionId(spawnEvent.getTransactionId());
        if (found.isPresent()){
            logger.info("Saving spawn event");
            spawnEventRepository.save(spawnEvent);
            UUID robotId = spawnEvent.getRobotId();
            logger.info("robot with id "+robotId+" spawned");
            //TODO handle this
        }
        else {
            logger.info("spawn event wasn't for us.");
        }
    }
}
