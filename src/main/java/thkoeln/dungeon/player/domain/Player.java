package thkoeln.dungeon.player.domain;


import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.game.domain.Game;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Entity
@Setter
@Getter
public class Player {
    @Id
    private final UUID id = UUID.randomUUID();
    private String name;
    private String email;
    private UUID bearerToken;

    @ManyToOne
    private Game game;

    public Player() {
        assignRandomName();
    }

    /**
     * Choose a random and unique name and email for the player
     */
    public void assignRandomName() {
        Faker faker = new Faker();
        Random randomGenerator = new Random();
        String randomNumberString = String.valueOf( randomGenerator.nextInt( 1000 ) );
        String nameString = faker.animal().toString() + faker.artist().toString() + randomNumberString;
        setName( nameString );
        setEmail( nameString + "@microservicedungeon.com" );
    }


    public void playRound() {
        // todo
    }

    @Override
    public String toString() {
        return "Player '" + name + "' and email '" + email + "'";
    }
}
