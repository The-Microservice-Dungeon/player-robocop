package thkoeln.dungeon;

import java.util.UUID;

public interface PlayerServiceCallback {

    public void playRound( Integer roundNumber );

    public void receiveCommandAnswer( UUID transactionId, String payload );

    public void learnAboutMoveByEnemyRobot();

}
