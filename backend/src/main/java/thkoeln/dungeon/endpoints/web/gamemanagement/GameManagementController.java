package thkoeln.dungeon.endpoints.web.gamemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import thkoeln.dungeon.game.application.GameApplicationService;
import thkoeln.dungeon.game.domain.game.GameRepository;
import thkoeln.dungeon.player.application.PlayerApplicationService;
import thkoeln.dungeon.restadapter.GameDto;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.Optional;

@Controller
public class GameManagementController {

    private final GameApplicationService gameApplicationService;
    private final GameServiceRESTAdapter gameServiceRESTAdapter;

    @Autowired
    public GameManagementController(GameApplicationService gameApplicationService, GameServiceRESTAdapter gameServiceRESTAdapter) {
        this.gameApplicationService = gameApplicationService;
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }

    @PostMapping("/games/create")
    @ResponseBody
    public ResponseEntity authenticate(@RequestParam(name = "maxPlayers") Optional<Integer> maxPlayers,
                                @RequestParam(name = "maxRounds") Optional<Integer> maxRounds){
        if (maxPlayers.isEmpty() || maxRounds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try {
            gameServiceRESTAdapter.createNewGame(maxPlayers.get(), maxRounds.get());
            return ResponseEntity.ok("New Game Creation Requested");
        } catch (RESTConnectionFailureException | UnexpectedRESTException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't create new Game.");
        }
    }
}
