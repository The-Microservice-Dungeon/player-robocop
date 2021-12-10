package thkoeln.dungeon.player.adapter;

import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.player.application.PlayerRegistryDto;

public interface PlayerSynchronousAdapter {
    public PlayerRegistryDto registerPlayer( PlayerRegistryDto playerRegistryDto );
}
