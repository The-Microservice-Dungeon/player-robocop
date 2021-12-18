package thkoeln.dungeon.game;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.game.application.GameApplicationService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DummyTest {
    @Autowired
    private GameApplicationService gameApplicationService;

    @Test
    public void letsSee() {
        assertNotNull( gameApplicationService );
        assertTrue( true );
    }
}
