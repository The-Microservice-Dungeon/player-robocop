package thkoeln.dungeon.game.application;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.game.domain.Game;
import thkoeln.dungeon.game.domain.GameStatus;

import java.util.UUID;

@Component
@Profile( "mock" )
public class GameServiceMockAdaptor implements GameExternalAdaptor {

    private Logger logger = LoggerFactory.getLogger( GameServiceMockAdaptor.class );

    @Override
    public Game fetchCurrentGameState() {
        Game game = new Game();
        game.setId( UUID.randomUUID() );
        game.setStatus( GameStatus.CREATED );
        return game;
    }
}
