package thkoeln.dungeon.eventconnector;

import thkoeln.dungeon.PlayerServiceCallback;

public interface GameClockTMP {

    public void hookupToCurrentGameAtStartup();

    public void gameStarted( PlayerServiceCallback playerServiceCallback );

    public void gameFinished( PlayerServiceCallback playerServiceCallback );

    public void nextGameRoundStarted( PlayerServiceCallback playerServiceCallback );

}
