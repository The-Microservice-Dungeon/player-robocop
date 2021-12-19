package thkoeln.dungeon.restadapter;

import thkoeln.dungeon.DungeonPlayerException;

public class PlayerAlreadyRegisteredException extends DungeonPlayerException {
    public PlayerAlreadyRegisteredException(String message ) {
        super( message );
    }
}
