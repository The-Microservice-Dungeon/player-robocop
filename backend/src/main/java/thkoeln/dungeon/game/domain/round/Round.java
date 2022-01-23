package thkoeln.dungeon.game.domain.round;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
        if (roundNumber==0){
            this.roundStatus = RoundStatus.BEFORE_FIRST_ROUND;
        }
        else {
            this.roundStatus = RoundStatus.STARTED;
        }
        this.roundNumber = roundNumber;
        logger.info("round status set to "+ this.getRoundStatus());
    }

    public void commandInputEnded(){
        this.roundStatus = RoundStatus.COMMAND_INPUT_ENDED;
        logger.info("round status set to "+ this.getRoundStatus());
    }
    public void roundEnded(){
        this.roundStatus = RoundStatus.ENDED;
        logger.info("round status set to "+ this.getRoundStatus());
    }
}
