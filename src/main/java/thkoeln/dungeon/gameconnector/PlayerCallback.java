package thkoeln.dungeon.gameconnector;

import java.util.UUID;

public interface PlayerCallback {

    public void playRound( Integer roundNumber );

    public void receiveCommandAnswer( UUID transactionId, String payload );

    public void learnAboutMoveByEnemyRobot();

}
