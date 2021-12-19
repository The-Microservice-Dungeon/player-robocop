package thkoeln.dungeon.restadapter;

import thkoeln.dungeon.DungeonPlayerException;

public class UnexpectedRestAdapterException extends DungeonPlayerException {
    public UnexpectedRestAdapterException(String message ) {
        super( message );
    }
}
