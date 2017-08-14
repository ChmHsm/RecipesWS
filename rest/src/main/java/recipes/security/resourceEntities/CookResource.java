package recipes.security.resourceEntities;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import recipes.Cook;
import recipes.GeneralRestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Me on 14/08/2017.
 */
public class CookResource extends ResourceSupport{
    private final Cook cook;

    public CookResource(Cook cook) {
        this.cook = cook;
        String username = cook.getUsername();
        this.add(new Link(String.valueOf(cook.getUsername()), "cook-username"));
        this.add(linkTo(GeneralRestController.class, username).withRel("general"));
        this.add(linkTo(
                methodOn(GeneralRestController.class, username).getCook(null, username)).withSelfRel());
    }

    public Cook getCook() {
        return cook;
    }
}
