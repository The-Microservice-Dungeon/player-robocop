package thkoeln.dungeon.command.commandbodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.command.ItemType;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BattleItemUseCommandBody extends CommandBody {
    private ItemType itemType;
    private UUID targetId; // Id of target Robot
}
