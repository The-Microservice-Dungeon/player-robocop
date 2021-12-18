package thkoeln.dungeon.game;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.game.domain.GameRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DummyTest {
    @Autowired
    private GameRepository gameRepository;

    @Test
    public void letsSee() {
        assertTrue( true );
    }
}
