package thkoeln.dungeon.planet.domain;

import lombok.*;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import thkoeln.dungeon.eventconsumer.robot.PlanetMovementDto;
import thkoeln.dungeon.map.Map;
import thkoeln.dungeon.map.PositionVO;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
public class Planet {
    @Id
    private UUID planetId = UUID.randomUUID();

    @Setter
    private Integer movementDifficulty = null;

    @Setter
    @Nullable
    @Embedded
    private PositionVO position = new PositionVO();

    @Setter
    @Getter(AccessLevel.NONE)
    private PlanetType planetType = PlanetType.DEFAULT;

    @Setter
    private ResourceType resourceType;

    @OneToOne
    @Setter(AccessLevel.PROTECTED)
    private Planet northNeighbour;

    @OneToOne
    @Setter(AccessLevel.PROTECTED)
    private Planet eastNeighbour;

    @OneToOne
    @Setter(AccessLevel.PROTECTED)
    private Planet southNeighbour;

    @OneToOne
    @Setter(AccessLevel.PROTECTED)
    private Planet westNeighbour;

    @Setter
    private Boolean visited = false;

    @Transient
    private Logger logger = LoggerFactory.getLogger(Planet.class);

    public Boolean isSpaceStation() {
        return this.planetType == PlanetType.SPACESTATION;
    }

    public Planet (UUID id) {
        this.planetId = id;
    }

    public Planet (PlanetMovementDto movementDto) {
        this.setPlanetType(movementDto.getPlanetType());
        this.setMovementDifficulty(movementDto.getMovementDifficulty());
        this.setResourceType(movementDto.getResourceType());
    }


    public Planet (UUID planetId, Boolean isSpaceStation, Boolean isResource) {
        this.planetId = planetId;
        if (isSpaceStation) {
            this.setPlanetType(PlanetType.SPACESTATION);
        } else if (isResource) {
            this.setResourceType(ResourceType.COAL);
        }
    }

    public Planet (UUID planetId, Boolean isSpaceStation) {
        this.planetId = planetId;
        if (isSpaceStation) {
            this.setPlanetType(PlanetType.SPACESTATION);
        }
        this.setMovementDifficulty(1);
    }

    protected Planet(){
        this.planetId = UUID.randomUUID();
    }

    /**
     * A neighbour relationship is always set on BOTH sides.
     *
     * @param otherPlanet
     * @param direction
     */
    public void defineNeighbour(Planet otherPlanet, CompassDirection direction) {
        if (otherPlanet == null)
            throw new PlanetException("Cannot establish neighbouring relationship with null planet!");
        try {
            Method otherGetter = neighbouringGetter(direction.getOppositeDirection());
            Method setter = neighbouringSetter(direction);
            setter.invoke(this, otherPlanet);
            Planet remoteNeighbour = (Planet) otherGetter.invoke(otherPlanet);
            if (!this.equals(remoteNeighbour)) {
                Method otherSetter = neighbouringSetter(direction.getOppositeDirection());
                otherSetter.invoke(otherPlanet, this);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PlanetException("Something went wrong that should not have happened ..." + e.getStackTrace());
        }
        logger.info("Established neighbouring relationship between planet '" + this + "' and '" + otherPlanet + "'.");
    }


    protected Method neighbouringGetter(CompassDirection direction) throws NoSuchMethodException {
        String name = "get" + WordUtils.capitalize(String.valueOf(direction)) + "Neighbour";
        return this.getClass().getDeclaredMethod(name);
    }


    protected Method neighbouringSetter(CompassDirection direction) throws NoSuchMethodException {
        String name = "set" + WordUtils.capitalize(String.valueOf(direction)) + "Neighbour";
        return this.getClass().getDeclaredMethod(name, this.getClass());
    }


    /**
     * Returns all Neighbours of the current Planet in CLOCKWISE order starting from north if present.
     * I.e.: NORTH, WEST, SOUTH, EAST
     * If one or more Neighbours are missing, they are just skipped and the resulting list does not
     * contain fields for each direction.
     *
     * @return List<Planet>
     */
    public List<Planet> allNeighbours() {
        List<Planet> allNeighbours = new ArrayList<>();
        if (getNorthNeighbour() != null) allNeighbours.add(getNorthNeighbour());
        if (getEastNeighbour() != null) allNeighbours.add(getEastNeighbour());
        if (getSouthNeighbour() != null) allNeighbours.add(getSouthNeighbour());
        if (getWestNeighbour() != null) allNeighbours.add(getWestNeighbour());
        return allNeighbours;
    }

    public Boolean hasNeighbours () {
        return !this.allNeighbours().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planet planet)) return false;
        return Objects.equals(planetId, planet.planetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planetId);
    }

    @Override
    public String toString() {
        return "Position: " + position + " (" + getPlanetId() + ") Gravity: " + getMovementDifficulty() + " Type: " + (isSpaceStation() ? "Spacestation" : getResourceType());
    }
}
