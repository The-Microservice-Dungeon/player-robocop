package thkoeln.dungeon.command;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class CommandAnswer {
    private UUID transactionId;
}
