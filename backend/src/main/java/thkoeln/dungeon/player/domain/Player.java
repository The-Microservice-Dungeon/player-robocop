package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
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
    private String name;
    private String email;
    private UUID bearerToken;
    private UUID registrationTransactionId;
    private UUID playerId;
    @OneToOne
    private Game currentGame;

    private Float money = 0F;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
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

    public void registerFor ( Game game, UUID registrationTransactionId ) throws PlayerDomainException {
        if ( game == null ) throw new PlayerDomainException( "Game must not be null!" );
        if ( registrationTransactionId == null ) throw new PlayerDomainException( "registrationTransactionId must not be null!" );
        this.currentGame = game;
        this.registrationTransactionId = registrationTransactionId;
    }


    public void playRound() {
        // todo
    }

    public void addRobot (Robot robot) {
        this.robots.add(robot);
    }

    public void removeRobot (Robot robot) {
        this.robots.remove(robot);
    }

    public Integer getRobotCount () {
        return this.robots.size();
    }

    @Override
    public String toString() {
        return "Player '" + name + "' (" + email + "), bearerToken: " + bearerToken + " playerId: " + playerId + " Id: " + id;
    }

    public void addMoney(Integer moneyChangedByAmount) {
        if (moneyChangedByAmount != null) this.money += moneyChangedByAmount;
    }
}
