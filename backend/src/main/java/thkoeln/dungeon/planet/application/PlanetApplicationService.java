package thkoeln.dungeon.planet.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.eventconsumer.robot.NeighboursEvent;
import thkoeln.dungeon.eventconsumer.robot.PlanetMovementDto;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetException;
import thkoeln.dungeon.planet.domain.PlanetRepository;

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

    public void newPlanet (PlanetMovementDto planetMovementDto) {
        Planet newPlanet = new Planet(planetMovementDto);
        newPlanet.setPlanetType(planetMovementDto.getPlanetType());
        newPlanet.setMovementDifficulty(planetMovementDto.getMovementDifficulty());
        newPlanet.setResourceType(planetMovementDto.getResourceType());
        this.planetRepository.save(newPlanet);
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
                this.planetRepository.save(planet);
                this.planetRepository.save(existingPlanet);
            }else {
                Planet newPlanet = new Planet(neighbour.getPlanetId());
                planet.defineNeighbour(newPlanet, neighbour.getDirection());
                this.planetRepository.save(planet);
                this.planetRepository.save(newPlanet);
            }
        }
    }
}
