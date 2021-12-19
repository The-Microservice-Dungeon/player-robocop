package thkoeln.dungeon.restadapter;

import thkoeln.dungeon.DungeonPlayerException;

public class RestAdapterException extends DungeonPlayerException {
    public RestAdapterException(String message ) {
        super( message );
    }
}
