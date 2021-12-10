package thkoeln.dungeon.player.application;

import thkoeln.dungeon.DungeonPlayerException;

public class PlayerRegistryException extends DungeonPlayerException {
    public PlayerRegistryException(String message ) {
        super( message );
    }
}
