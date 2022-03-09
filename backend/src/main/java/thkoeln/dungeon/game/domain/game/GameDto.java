package thkoeln.dungeon.game.domain.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import thkoeln.dungeon.game.domain.game.GameStatus;

import java.util.LinkedList;
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
    private Integer maxPlayers;
    private Integer maxRounds;
    private Integer currentRoundCount;
    @JsonProperty("participatingPlayers")
    private List<UUID> participatingPlayers = null;

    public GameDto(UUID gameId0, GameStatus created, int i) {
    }
    @JsonProperty("participatingPlayers")
    public List<UUID> getParticipatingPlayers() {
        return participatingPlayers;
    }

    @JsonProperty("participatingPlayers")
    public void setParticipatingPlayers(List<String> participatingPlayers) {
        List<UUID> result = new LinkedList<>();
        for (String playerId: participatingPlayers){
            result.add(UUID.fromString(playerId));
        }
        this.participatingPlayers = result;;
    }
}
