package thkoeln.dungeon.player;

import java.util.UUID;

public interface PlayerRegistry {

    /**
     *
     * @param playerRegistryDto Player to be registered
     * @return the bearer token if everything goes alright
     */
    public UUID registerPlayer( PlayerRegistryDto playerRegistryDto );

}
