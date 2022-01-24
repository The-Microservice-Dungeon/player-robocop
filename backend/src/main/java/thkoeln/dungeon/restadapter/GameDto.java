package thkoeln.dungeon.restadapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import thkoeln.dungeon.game.domain.game.GameStatus;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDto {
    private UUID gameId;
    private GameStatus gameStatus;
    private Integer currentRoundCount;
    private List<UUID> participatingPlayers;

    /***
     * Constructor for gameDTO
     * @param gameId0
     * @param created
     * @param i
     */
    public GameDto(UUID gameId0, GameStatus created, int i) {
    }
}
