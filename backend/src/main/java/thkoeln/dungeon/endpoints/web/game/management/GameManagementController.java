package thkoeln.dungeon.endpoints.web.game.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import thkoeln.dungeon.restadapter.GameServiceRESTAdapter;
import thkoeln.dungeon.restadapter.exceptions.RESTConnectionFailureException;
import thkoeln.dungeon.restadapter.exceptions.UnexpectedRESTException;

import java.util.Optional;

@Controller
public class GameManagementController {

    private final GameServiceRESTAdapter gameServiceRESTAdapter;

    @Autowired
    public GameManagementController(GameServiceRESTAdapter gameServiceRESTAdapter) {
        this.gameServiceRESTAdapter = gameServiceRESTAdapter;
    }

    @PostMapping("/games/create")
    @ResponseBody
    public ResponseEntity<?> authenticate(@RequestParam(name = "maxPlayers") Optional<Integer> maxPlayers,
                                          @RequestParam(name = "maxRounds") Optional<Integer> maxRounds) {
        if (maxPlayers.isEmpty() || maxRounds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try {
            boolean newGameCreated = gameServiceRESTAdapter.createNewGame(maxPlayers.get(), maxRounds.get());
            if (newGameCreated) return ResponseEntity.ok("game_creation_successfully_requested");
        } catch (RESTConnectionFailureException | UnexpectedRESTException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't create new Game.");
        }
        return ResponseEntity.internalServerError().body("game_creation_request_failed");
    }
}
