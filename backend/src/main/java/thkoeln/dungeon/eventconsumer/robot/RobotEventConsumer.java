package thkoeln.dungeon.eventconsumer.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final TradingEventRepository tradingEventRepository;
    private final Logger logger = LoggerFactory.getLogger(RobotEventConsumer.class);


    @Autowired
    public RobotEventConsumer(SpawnEventRepository spawnEventRepository, MapApplicationService mapApplicationService, PlanetApplicationService planetApplicationService, RobotRepository robotRepository, CommandRepository commandRepository, PlanetRepository planetRepository, MovementEventRepository movementEventRepository, NeighboursEventRepository neighboursEventRepository, TradingEventRepository tradingEventRepository, RobotApplicationService robotApplicationService) {
        this.robotRepository = robotRepository;
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
    public void consumeMovementEvent(@Header String eventId, @Header String timestamp,
                                     @Payload String payload){
        try {
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
        } catch (Exception e) {
            logger.error("Can't consume Movement Event. " + e.getMessage());
        }
    }

    @KafkaListener(topics = "neighbours")
    public void consumeNeighbourEvent(@Header String eventId, @Header String timestamp, @Header String transactionId,
                                      @Payload String payload){

        logger.info("Neighbour event received. Consuming in 300ms");
        Timer timer = new Timer(300, arg0 -> {
            logger.info("Consuming neighbour event");
            logger.info("Payload:" +payload);
            NeighbourEvent neighboursEvent = new NeighbourEvent()
                    .fillWithPayload(payload)
                    .fillHeader(eventId,timestamp,transactionId);
            neighboursEventRepository.save(neighboursEvent);
            Optional<Command> triggeringCommandOptional = commandRepository.findByTransactionId(neighboursEvent.getTransactionId());
            if (triggeringCommandOptional.isPresent()){
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
            }
        });
        timer.setRepeats(false);
        timer.start();
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
