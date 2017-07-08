package recipes.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Created by Me on 29/05/2017.
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class NullImageUploadException extends RuntimeException{
    public NullImageUploadException(){
        super("The uploaded image is empty :/");
    }
}
