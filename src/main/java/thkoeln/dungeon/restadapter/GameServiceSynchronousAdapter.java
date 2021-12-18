package thkoeln.dungeon.restadapter;

import thkoeln.dungeon.game.domain.Game;

public interface GameServiceSynchronousAdapter {
    public GameDto[] fetchCurrentGameState();
    public PlayerRegistryDto registerPlayer( PlayerRegistryDto playerRegistryDto );
}
