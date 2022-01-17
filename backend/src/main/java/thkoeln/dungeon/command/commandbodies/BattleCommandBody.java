package thkoeln.dungeon.command.commandbodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BattleCommandBody extends CommandBody {
    private UUID targetId; // Robot UUID
}
