package thkoeln.dungeon.planet.domain;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class PlanetException extends DungeonPlayerRuntimeException {
    public PlanetException( String message ) {
        super( message );
    }
}
