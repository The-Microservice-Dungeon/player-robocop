package thkoeln.dungeon.command.commandbodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.dungeon.command.ItemType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoveItemUseCommandBody extends CommandBody{
    private ItemType itemType;
}
