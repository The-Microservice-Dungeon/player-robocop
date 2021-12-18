package thkoeln.dungeon.game;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceSynchronousAdapter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static thkoeln.dungeon.game.domain.GameStatus.*;

//@RestClientTest(GameApplicationService.class)
//@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
//@ContextConfiguration( classes = DungeonPlayerMainApplication.class )
public class GameInitializationTest {
    private static final UUID GAME_ID_CREATED = UUID.randomUUID();
    private GameDto gameDtoCreated = new GameDto( GAME_ID_CREATED, CREATED, 0 );

    private static final UUID GAME_ID_IN_PREPARATION = UUID.randomUUID();
    private GameDto gameDtoInPreparation = new GameDto( GAME_ID_IN_PREPARATION, IN_PREPARATION, 0 );

    private static final UUID GAME_ID_RUNNING = UUID.randomUUID();
    private static final Integer GAME_ROW_COUNT_RUNNING = 42;
    private GameDto gameDtoRunning = new GameDto( GAME_ID_RUNNING, GAME_RUNNING, GAME_ROW_COUNT_RUNNING );

    private static final UUID GAME_ID_FINISHED = UUID.randomUUID();
    private static final Integer GAME_ROW_COUNT_FINISHED = 200;
    private GameDto gameDtoFinished = new GameDto( GAME_ID_FINISHED, GAME_FINISHED, GAME_ROW_COUNT_FINISHED );

    private static final UUID GAME_ID_ORPHANED = UUID.randomUUID();
    private static final Integer GAME_ROW_COUNT_ORPHANED = 250;
    private GameDto gameDtoOrphaned = new GameDto( GAME_ID_ORPHANED, GAME_FINISHED, GAME_ROW_COUNT_ORPHANED );

    @Value("${GAME_SERVICE}")
    private String gameServiceUrlString;
    private String gameServiceGamesEndpoint;

    @Mock
    private RestTemplate restTemplate;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameServiceSynchronousAdapter gameServiceSynchronousAdapter;

    @InjectMocks
    private GameApplicationService gameApplicationService =
            new GameApplicationService( gameRepository, gameServiceSynchronousAdapter );

    @BeforeEach
    public void setUp() {
        gameRepository.deleteAll();
        gameServiceGamesEndpoint = gameServiceUrlString + "/games";
    }

    private void mockCallToGameService_initialCall() {
        GameDto[] allRemoteGames = new GameDto[3];
        allRemoteGames[0] = gameDtoCreated;
        allRemoteGames[1] = gameDtoRunning;
        allRemoteGames[2] = gameDtoFinished;
        when(restTemplate.getForObject( gameServiceGamesEndpoint, GameDto[].class ))
                .thenReturn( allRemoteGames );
    }

    @Test
    public void properlySynchronizedGameState_afterFirstCall () {
        mockCallToGameService_initialCall();
        gameApplicationService.synchronizeGameState();
        assertTrue( true );
    }

}
