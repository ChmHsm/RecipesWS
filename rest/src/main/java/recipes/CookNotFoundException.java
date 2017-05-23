package recipes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Me on 23/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class CookNotFoundException extends RuntimeException{
    public CookNotFoundException(String username){
        super("Cook " + username + " was not found :/");
    }
}
