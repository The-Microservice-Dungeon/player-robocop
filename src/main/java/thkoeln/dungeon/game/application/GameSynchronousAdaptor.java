package thkoeln.dungeon.game.application;

import thkoeln.dungeon.game.domain.Game;

public interface GameSynchronousAdaptor {
    public Game fetchCurrentGameState();
}
