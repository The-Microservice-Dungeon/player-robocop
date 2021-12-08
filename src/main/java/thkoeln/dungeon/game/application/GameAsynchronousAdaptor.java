package thkoeln.dungeon.game.application;

import thkoeln.dungeon.game.domain.Game;

public interface GameAsynchronousAdaptor {
    public void consumeGameStartedEvent();
    public void consumeGameEndedEvent();
    public void consumeNewRoundStartedEvent();
}
