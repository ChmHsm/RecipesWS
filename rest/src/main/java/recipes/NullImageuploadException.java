package recipes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Created by Me on 29/05/2017.
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class NullImageuploadException extends RuntimeException{
    NullImageuploadException(){
        super("The uploaded image is empty :/");
    }
}
