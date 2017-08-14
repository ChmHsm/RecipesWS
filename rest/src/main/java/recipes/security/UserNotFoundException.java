package recipes.security;

/**
 * Created by Me on 14/08/2017.
 */
@SuppressWarnings("serial")
// tag::code[]
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("could not find user '" + userId + "'.");
    }
}