package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Player {
    @Id
    private final UUID id = UUID.randomUUID();
    private String name;
    private String email;
    private UUID bearerToken;


    public Player() {
        assignRandomName();
    }

    /**
     * Choose a random and unique name and email for the player
     */
    public void assignRandomName() {
        String randomNickname = NameGenerator.generateName();
        setName( randomNickname );
        setEmail( randomNickname + "@microservicedungeon.com" );
    }

    public boolean hasBeenRegistered() {
        return ( bearerToken != null );
    }

    public void playRound() {
        // todo
    }

    @Override
    public String toString() {
        return "Player '" + name + "' (" + email + "), bearerToken: " + bearerToken;
    }
}
