package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.robot.domain.Robot;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Player {
    @Id
    private final UUID id = UUID.randomUUID();
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private final List<GameParticipation> gameParticipations = new ArrayList<>();
    private String name;
    private String email;
    private UUID bearerToken;

    private Float money = 200F;

    // TODO: Fill with data when new Robot gets spawned
    @OneToMany
    private List<Robot> robots = new ArrayList<>();

    /**
     * Choose a random and unique name and email for the player
     */
    public void assignRandomName() {
        String randomNickname = NameGenerator.generateName();
        setName(randomNickname);
        setEmail(randomNickname + "@microservicedungeon.com");
    }

    public boolean isReadyToPlay() {
        return (bearerToken != null);
    }


    public void participateInGame(Game game) {
        GameParticipation gameParticipation = new GameParticipation(game);
        gameParticipations.add(gameParticipation);
    }


    public boolean isParticipantInGame(Game game) {
        return (findParticipationFor(game) != null);
    }


    private GameParticipation findParticipationFor(Game game) {
        Optional<GameParticipation> found = gameParticipations.stream()
                .filter(gp -> gp.getGame().equals(game)).findFirst();
        return found.isPresent() ? found.get() : null;
    }


    public void playRound() {
        // todo
    }

    public Integer getRobotCount () {
        return this.robots.size();
    }

    @Override
    public String toString() {
        return "Player '" + name + "' (" + email + "), bearerToken: " + bearerToken;
    }
}
