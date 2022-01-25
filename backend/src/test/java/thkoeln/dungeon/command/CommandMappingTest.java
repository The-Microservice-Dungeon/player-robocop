package thkoeln.dungeon.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.dungeon.game.domain.game.Game;
import thkoeln.dungeon.game.domain.game.GameStatus;
import thkoeln.dungeon.game.domain.round.Round;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.player.domain.Player;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class CommandMappingTest {
    private final ModelMapper modelMapper = new ModelMapper();
    private final Game game = new Game();
    private final Player player = new Player();
    private final Robot robot = new Robot();
    private final Planet targetPlanet = new Planet();

    @BeforeEach
    public void setup(){
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        game.setGameId(UUID.randomUUID());
        game.setGameStatus(GameStatus.STARTED);
        game.setRound(new Round(1));
        player.setBearerToken(UUID.randomUUID());
        player.setEmail("Robocop@cop.pop");
        player.setName("Robocop");
        robot.setExternalId(UUID.randomUUID());
        targetPlanet.setExternalId(UUID.randomUUID());
    }

    @Test
    public void testRobotBuyCommandMapping(){
        Command robotBuyCommand = CommandBuilder.buildBuyRobotCommand(game, player, 1);
        CommandDto robotBuyCommandDto = robotBuyCommand.toDto();
        assertNull(robotBuyCommandDto.getRobotId());
        assertNull(robotBuyCommandDto.getCommandObject().getRobotId());
        assertNull(robotBuyCommandDto.getCommandObject().getTargetId());
        assertEquals(robotBuyCommandDto.getCommandType(), CommandType.buying);
        assertEquals(robotBuyCommandDto.getGameId(), game.getGameId());
        assertEquals(robotBuyCommandDto.getPlayerToken(), player.getBearerToken());
        assertEquals(robotBuyCommandDto.getCommandObject().getItemType(), ItemType.ROBOT);
    }

    @Test
    public void testRobotMovementCommandMapping(){
        Command moveCommand = CommandBuilder.buildMovementCommand(game, player, robot, targetPlanet);
        CommandDto moveCommandDto = moveCommand.toDto();
        assertEquals(moveCommandDto.getGameId(), game.getGameId());
        assertEquals(moveCommandDto.getPlayerToken(), player.getBearerToken());
        assertEquals(moveCommandDto.getRobotId(), robot.getExternalId());
        assertEquals(moveCommandDto.getCommandType(), CommandType.movement);
        assertEquals(moveCommandDto.getCommandObject().getCommandType(), CommandType.movement);
        assertEquals(moveCommandDto.getCommandObject().getRobotId(), robot.getExternalId());
        assertEquals(moveCommandDto.getCommandObject().getTargetId(), targetPlanet.getExternalId());
        assertNull(moveCommandDto.getCommandObject().getItemType());
        assertNull(moveCommandDto.getCommandObject().getItemQuantity());
    }

    @Test
    public void testMiningCommandMapping(){
        Command miningCommand = CommandBuilder.buildMiningCommand(game,player,robot);
        CommandDto miningCommandDto = miningCommand.toDto();
        assertEquals(miningCommandDto.getGameId(), game.getGameId());
        assertEquals(miningCommandDto.getPlayerToken(), player.getBearerToken());
        assertEquals(miningCommandDto.getRobotId(), robot.getExternalId());
        assertEquals(miningCommandDto.getCommandType(), CommandType.mining);
        assertEquals(miningCommandDto.getCommandObject().getCommandType(), CommandType.mining);
        assertEquals(miningCommandDto.getCommandObject().getRobotId(), robot.getExternalId());
        assertNull(miningCommandDto.getCommandObject().getItemType());
        assertNull(miningCommandDto.getCommandObject().getItemQuantity());
        assertNull(miningCommandDto.getCommandObject().getTargetId());
    }

    @Test
    public void testRegenerationCommandMapping() {
        Command command = CommandBuilder.buildRegenerationCommand(game, player, robot);
        CommandDto commandDto = command.toDto();
        assertEquals(commandDto.getGameId(), game.getGameId());
        assertEquals(commandDto.getPlayerToken(), player.getBearerToken());
        assertEquals(commandDto.getRobotId(), robot.getExternalId());
        assertEquals(commandDto.getCommandType(), CommandType.regeneration);
        assertEquals(commandDto.getCommandObject().getCommandType(), CommandType.regeneration);
        assertEquals(commandDto.getCommandObject().getRobotId(), robot.getExternalId());
        assertNull(commandDto.getCommandObject().getItemType());
        assertNull(commandDto.getCommandObject().getItemQuantity());
        assertNull(commandDto.getCommandObject().getTargetId());
    }

    @Test
    public void testItemBuyCommandMapping(){
        Command command = CommandBuilder.buildBuyItemCommand(game,player,robot,ItemType.LONG_RANGE_BOMBARDMENT,2);
        CommandDto commandDto = command.toDto();
        assertEquals(commandDto.getGameId(), game.getGameId());
        assertEquals(commandDto.getPlayerToken(), player.getBearerToken());
        assertEquals(commandDto.getRobotId(), robot.getExternalId());
        assertEquals(commandDto.getCommandType(), CommandType.buying);
        assertEquals(commandDto.getCommandObject().getCommandType(), CommandType.buying);
        assertEquals(commandDto.getCommandObject().getRobotId(), robot.getExternalId());
        assertEquals(commandDto.getCommandObject().getItemType(), ItemType.LONG_RANGE_BOMBARDMENT);
        assertEquals(commandDto.getCommandObject().getItemQuantity(), command.getItemQuantity());
        assertNull(commandDto.getCommandObject().getTargetId());
    }
}
