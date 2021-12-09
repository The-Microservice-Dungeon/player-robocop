package thkoeln.dungeon.game.adapter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameStatus;

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
    public Game fetchCurrentGameState() {
        Game game = new Game();
        game.setGameId( gameId );
        GameStatus initialState = initialDelayUntilStart > 0 ? GameStatus.CREATED : GameStatus.GAME_RUNNING;
        game.setStatus( initialState );
        return game;
    }
}
