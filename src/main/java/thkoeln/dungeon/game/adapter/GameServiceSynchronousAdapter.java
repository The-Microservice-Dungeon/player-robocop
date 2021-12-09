package thkoeln.dungeon.game.adapter;

import thkoeln.dungeon.game.domain.Game;

public interface GameServiceSynchronousAdapter {
    public Game fetchCurrentGameState();
}
