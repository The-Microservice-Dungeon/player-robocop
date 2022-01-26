package thkoeln.dungeon.eventconsumer.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.command.CommandRepository;
import thkoeln.dungeon.map.MapApplicationService;
import thkoeln.dungeon.planet.application.PlanetApplicationService;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetRepository;
import thkoeln.dungeon.robot.application.RobotApplicationService;
import thkoeln.dungeon.robot.domain.Robot;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RobotEventConsumer {
    private final RobotRepository robotRepository;
    private final CommandRepository commandRepository;
    private final PlanetRepository planetRepository;
    private final MovementEventRepository movementEventRepository;
    private final NeighboursEventRepository neighboursEventRepository;
    private final PlanetApplicationService planetApplicationService;
    private final MapApplicationService mapApplicationService;
    private final SpawnEventRepository spawnEventRepository;
    private final Logger logger = LoggerFactory.getLogger(RobotEventConsumer.class);

    private final RobotApplicationService robotApplicationService;

    @Autowired
    public RobotEventConsumer(SpawnEventRepository spawnEventRepository, MapApplicationService mapApplicationService,PlanetApplicationService planetApplicationService, RobotRepository robotRepository, CommandRepository commandRepository, PlanetRepository planetRepository, MovementEventRepository movementEventRepository, NeighboursEventRepository neighboursEventRepository, RobotApplicationService robotApplicationService) {
        this.robotRepository = robotRepository;
        this.commandRepository = commandRepository;
        this.planetRepository = planetRepository;
        this.movementEventRepository = movementEventRepository;
        this.neighboursEventRepository = neighboursEventRepository;
        this.planetApplicationService = planetApplicationService;
        this.mapApplicationService = mapApplicationService;
        this.spawnEventRepository = spawnEventRepository;
        this.robotApplicationService = robotApplicationService;
    }

    // THIS DOES NOT HAVE A TRANSACTION ID. AAAAAAAAAAAAAAAAAAAAAAAH
    // "SAKE OF OBSCURITY?????????????????"
    @KafkaListener(topics = "movement")
    public void consumeMovementEvent(@Header String eventId, @Header String timestamp,
                                     @Payload String payload){
        logger.info("Consuming movement Event");
        MovementEvent movementEvent = new MovementEvent()
                .fillWithPayload(payload)
                .fillHeader(eventId,timestamp,"");
        logger.info("Saving movement event");
        movementEventRepository.save(movementEvent);
        List<Robot> affectedRobots = robotRepository.findAllByRobotIdIn(movementEvent.getRobots());
        Optional<Planet> targetPlanet = planetRepository.findById(movementEvent.getPlanet().getPlanetId());
        if (targetPlanet.isPresent()){
            planetApplicationService.fillPlanetInformation(targetPlanet.get(),movementEvent.getPlanet());
            for (Robot robot: affectedRobots){
                mapApplicationService.updateRobotPosition(robot, targetPlanet.get());
            }
        }else {
            logger.error("Planet for movement not found.");
        }
    }

    @KafkaListener(topics = "neighbours")
    public void consumeNeighbourEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                      @Payload String payload){
        logger.info("Consuming neighbour event");
        logger.info("Payload:" +payload);
        NeighboursEvent neighboursEvent = new NeighboursEvent()
                .fillWithPayload(payload)
                .fillHeader(eventId,timestamp,transactionId);
        neighboursEventRepository.save(neighboursEvent);
        // TODO: call handleNewPlanetNeighbours -> WORKING MAAAP
        //TODO make some calls
    }

    @KafkaListener(topics = "spawn-notification")
    public void consumeSpawnEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                  @Payload String payload){
        logger.info("Consuming spawn event");
        SpawnEvent spawnEvent = new SpawnEvent()
                .fillWithPayload(payload)
                .fillHeader(eventId,timestamp,transactionId);
        spawnEventRepository.save(spawnEvent);
        //TODO make some calls
    }
}
