package thkoeln.dungeon.endpoints.web.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Controller
public class AuthController {

    private final String secret = "letMePlay";


    @PostMapping("/authentication")
    @ResponseBody
    public Boolean authenticate(@RequestParam(name = "password") Optional<String> password) {
        if (password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        String providedPassword = password.get();
        if (Objects.equals(providedPassword, secret)) return true;
        else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
