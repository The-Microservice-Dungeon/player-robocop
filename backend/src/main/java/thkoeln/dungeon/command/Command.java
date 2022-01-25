package thkoeln.dungeon.command;

import com.sun.istack.NotNull;
import lombok.*;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Command {
    @Id
    private final UUID id = UUID.randomUUID();
    private UUID transactionId;
    @NotNull
    private CommandType commandType;

    @NotNull
    @ManyToOne
    private Game game;
    private Integer roundNumber;
    @NotNull
    @ManyToOne
    private Player player;
    @ManyToOne
    private Robot robot;
    @ManyToOne
    private Planet targetPlanet;

    private ItemType itemType;
    private Integer itemQuantity;

    public Command(Game game, Player player, CommandType commandType, Robot originRobot, Planet targetPlanet, ItemType itemType, Integer itemQuantity) {
        this.game = game;
        this.player = player;
        this.commandType = commandType;
        this.robot = originRobot;
        this.targetPlanet = targetPlanet;
        this.roundNumber = game.getRound().getRoundNumber();
        this.itemType = itemType;
        this.itemQuantity = itemQuantity;
    }
    /*
        This might be a bit messy, but since the command structure is messy, I will leave it like this.
        Maybe there is a more efficient way to map everything that is there, but I am really changing the Entity to DTO here,
        because we have a different understanding of commands than game.
     */
    public CommandDto toDto(){
        CommandBody commandBody = new CommandBody();
        commandBody.setCommandType(this.getCommandType());
        if (getRobot()!=null) {
            commandBody.setRobotId(getRobot().getExternalId());
        }
        if (getTargetPlanet()!=null){
            commandBody.setTargetId(getTargetPlanet().getExternalId());
        }
        if (getItemType()!=null){
            commandBody.setItemType(getItemType());
        }
        if (getItemQuantity()!=null){
            commandBody.setItemQuantity(getItemQuantity());
        }
        CommandDto commandDto = new CommandDto(getGame().getGameId(),getPlayer().getBearerToken(),null,getCommandType(),commandBody);
        if (getRobot()!=null) {
            commandDto.setRobotId(getRobot().getExternalId());
        }
        return commandDto;
    }
}
