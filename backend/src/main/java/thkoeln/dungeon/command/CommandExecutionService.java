package thkoeln.dungeon.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.Arrays;
import java.util.UUID;


@Component
public class CommandExecutionService implements thkoeln.dungeon.command.CommandExecutor {

    private final CommandRepository commandRepository;

    private final GameServiceRESTAdapter gameServiceRESTAdapter;

    private final Logger logger = LoggerFactory.getLogger(CommandExecutionService.class);

    @Autowired
    public CommandExecutionService(
            CommandRepository commandRepository,
            GameServiceRESTAdapter gameServiceRESTAdapter) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
        this.commandRepository = commandRepository;
    }

    @Override
    public UUID executeCommand(Command command) {
        logger.info("Executing "+ command.getCommandType() + " command...");
        CommandDto commandDto = command.toDto();
        UUID transactionId = null;
        try {
            transactionId = gameServiceRESTAdapter.sendCommand(commandDto);
        }catch (UnexpectedRESTException e){
            logger.error("Encountered unexpected REST exception while sending command. "+ e.getMessage());
        }catch (RESTConnectionFailureException e){
            logger.error("Rest connection failed. while sending command. "+ e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
        logger.debug("got transactionId "+ transactionId + " for command "+ command.getId() + ".");
        command.setTransactionId(transactionId);
        logger.debug("saving command");
        commandRepository.save(command);
        return transactionId;
    }

}
