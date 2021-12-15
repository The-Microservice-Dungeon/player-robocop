package thkoeln.dungeon.restadapter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.game.domain.Game;

import java.util.UUID;

@Component
@Profile( "mock" )
public class GameServiceSynchronousMockAdapter implements GameServiceSynchronousAdapter {

    private Logger logger = LoggerFactory.getLogger( GameServiceSynchronousMockAdapter.class );

    @Value( "${dungeon.mock.game.initialDelayUntilStart}" )
    private int initialDelayUntilStart;
    @Value( "${dungeon.mock.game.gameId}" )
    private UUID gameId;


    /**
     * @return just a randomly created Game object in fitting state
     */
    @Override
    public GameDto fetchCurrentGameState() {
        GameDto gameDto = new GameDto();
        gameDto.setGameId( gameId );
        GameStatus initialState = initialDelayUntilStart > 0 ? GameStatus.CREATED : GameStatus.GAME_RUNNING;
        gameDto.setStatus( initialState );
        return gameDto;
    }

    /**
     * Return the same object, enriched by a random beared token
     * @param playerRegistryDto
     * @return
     */
    @Override
    public PlayerRegistryDto registerPlayer( PlayerRegistryDto playerRegistryDto ) {
        playerRegistryDto.setBearerToken( UUID.randomUUID() );
        return playerRegistryDto;
    }


}
