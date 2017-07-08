package recipes.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Me on 27/05/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecipeNotFoundException  extends RuntimeException{
    public RecipeNotFoundException(){
        super("The recipe you requested does not exit :/");
    }
}
