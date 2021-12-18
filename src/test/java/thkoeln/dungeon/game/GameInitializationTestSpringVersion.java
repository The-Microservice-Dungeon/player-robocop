package thkoeln.dungeon.game;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.DungeonPlayerConfiguration;
import thkoeln.dungeon.DungeonPlayerMainApplication;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.GameRepository;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceSynchronousAdapter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static thkoeln.dungeon.game.domain.GameStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {DungeonPlayerMainApplication.class, DungeonPlayerConfiguration.class})
public class GameInitializationTestSpringVersion {
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
    private String gameServiceURIString;
    private URI gameServiceGamesURI = new URI( gameServiceURIString + "/games" );

    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameServiceSynchronousAdapter gameServiceSynchronousAdapter;

    private GameApplicationService gameApplicationService =
            new GameApplicationService( gameRepository, gameServiceSynchronousAdapter );

    public GameInitializationTestSpringVersion() throws URISyntaxException {
    }

    @Before
    public void setUp() {
        gameRepository.deleteAll();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    private void mockCallToGameService_initialCall() throws Exception {
        GameDto[] allRemoteGames = new GameDto[3];
        allRemoteGames[0] = gameDtoCreated;
        allRemoteGames[1] = gameDtoRunning;
        allRemoteGames[2] = gameDtoFinished;

        mockServer.expect( ExpectedCount.manyTimes(),
                requestTo( gameServiceGamesURI ))
                .andExpect( method( GET ))
                .andRespond( withStatus( HttpStatus.OK )
                                .contentType( MediaType.APPLICATION_JSON )
                                .body( mapper.writeValueAsString(allRemoteGames) ) );
    }

    @Test
    public void properlySynchronizedGameState_afterFirstCall() throws Exception {
        mockCallToGameService_initialCall();
        gameApplicationService.synchronizeGameState();
        mockServer.verify();
        assertTrue( true );
    }

}
