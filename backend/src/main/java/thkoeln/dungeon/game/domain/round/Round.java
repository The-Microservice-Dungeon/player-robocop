package thkoeln.dungeon.game.domain.round;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@NoArgsConstructor
@Getter
@Embeddable
public class Round {
    private Integer roundNumber;
    private RoundStatus roundStatus;

    @Transient
    private final Logger logger = LoggerFactory.getLogger(Round.class);

    public Round(Integer roundNumber){
        this.roundStatus = RoundStatus.INITIALIZED;
        this.roundNumber = roundNumber;
        logger.info("round status set to ${roundStatus}");
    }

    public void commandInputEnded(){
        this.roundStatus = RoundStatus.RUNNING;
        logger.info("round status set to RUNNING");
    }
    public void roundEnded(){
        this.roundStatus = RoundStatus.ENDED;
        logger.info("round status set to ENDED");
    }
}
