package thkoeln.dungeon.planet.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.robot.NeighboursEvent;
import thkoeln.dungeon.eventconsumer.robot.PlanetMovementDto;
import thkoeln.dungeon.map.PositionVO;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetException;
import thkoeln.dungeon.planet.domain.PlanetRepository;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetApplicationService {
    private final PlanetRepository planetRepository;

    @Autowired
    public PlanetApplicationService(
            PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    // TODO: Call on robot spawned event
    public Planet createStartPlanet (UUID id) {
        Planet newPlanet = new Planet(id, true);
        this.planetRepository.save(newPlanet);
        return newPlanet;
    }

    public void fillPlanetInformation (Planet targetPlanet, PlanetMovementDto planetInformation){
        targetPlanet.setPlanetType(planetInformation.getPlanetType());
        targetPlanet.setMovementDifficulty(planetInformation.getMovementDifficulty());
        targetPlanet.setResourceType(planetInformation.getResourceType());
        this.planetRepository.save(targetPlanet);
    }

    public void setPlanetPosition (Planet planet, PositionVO positionVO) {
        planet.setPosition(positionVO);
        this.planetRepository.save(planet);
    }


    public void generateNeighboursForPlanet (Planet pPlanet, NeighboursEvent[] neighbours) {
        Optional<Planet> planetOption = this.planetRepository.findById(pPlanet.getPlanetId());
        if (planetOption.isEmpty()) throw new PlanetException("Cant generate Neighbours for unset Planet");
        Planet planet = planetOption.get();

        for (NeighboursEvent neighbour : neighbours) {
            Optional<Planet> existingPlanetOption = this.planetRepository.findById(neighbour.getPlanetId());
            if (existingPlanetOption.isPresent()) {
                Planet existingPlanet = existingPlanetOption.get();
                planet.defineNeighbour(existingPlanet, neighbour.getDirection());
                this.planetRepository.save(existingPlanet);
            }else {
                Planet newPlanet = new Planet(neighbour.getPlanetId());
                planet.defineNeighbour(newPlanet, neighbour.getDirection());
                this.planetRepository.save(newPlanet);
            }
            this.planetRepository.save(planet);
        }
    }

    public Planet refreshPlanet (Planet planet) {
        return this.planetRepository.findById(planet.getPlanetId()).get();
    }

    public Planet getById (UUID id) {
        return this.planetRepository.findById(id).get();
    }

    public Boolean isFirstPlanet () {
        return this.planetRepository.findAll().size() == 1;
    }
}
