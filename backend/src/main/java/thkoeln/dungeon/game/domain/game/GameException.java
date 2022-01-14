package thkoeln.dungeon.game.domain.game;

import thkoeln.dungeon.DungeonPlayerRuntimeException;

public class GameException extends DungeonPlayerRuntimeException {
    public GameException(String message) {
        super(message);
    }
}
