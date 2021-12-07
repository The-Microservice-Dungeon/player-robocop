package thkoeln.dungeon.player;

import thkoeln.dungeon.GameConnectorException;

public class PlayerRegistryException extends GameConnectorException {
    public PlayerRegistryException(String message ) {
        super( message );
    }
}
