package thkoeln.dungeon.game.adapter;

import thkoeln.dungeon.game.domain.Game;

public interface GameSynchronousAdapter {
    public Game fetchCurrentGameState();
}
